/*
 * Copyright (c) 2004-2010, P. Simon Tuffs (simon@simontuffs.com)
 * Copyright (c) 2019=2020, Needham Software LLC
 * All rights reserved.
 *
 * See the full license at https://github.com/nsoft/uno-jar/blob/master/LICENSE.txt
 * See addition code licenses at: https://github.com/nsoft/uno-jar/blob/master/NOTICE.txt
 */

/*
 * Many thanks to the following for their contributions to One-Jar:
 *
 * Contributor: Christopher Ottley <xknight@users.sourceforge.net>
 * Contributor: Thijs Sujiten (www.semantica.nl)
 * Contributor: Gerold Friedmann
 */

package com.needhamsoftware.unojar;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

/**
 * Loads classes from pre-defined locations inside the jar file containing this
 * class.  Classes will be loaded from jar files contained in the following
 * locations within the main jar file (on the classpath of the application
 * actually, which when running with the "java -jar" command works out to be
 * the same thing).
 * <ul>
 * <li>
 *   /lib	Used to contain library jars.
 * </li>
 * <li>
 *   /main	Used to contain a default main jar.
 * </li>
 * </ul>
 *
 * @author simon@simontuffs.com (<a href="http://www.simontuffs.com">http://www.simontuffs.com</a>)
 */
public class JarClassLoader extends ClassLoader implements IProperties {

  public final static String PROPERTY_PREFIX = "uno-jar.";
  public final static String P_INFO = PROPERTY_PREFIX + "info";
  public final static String P_VERBOSE = PROPERTY_PREFIX + "verbose";
  public final static String P_SILENT = PROPERTY_PREFIX + "silent";
  public final static String P_JAR_NAMES = PROPERTY_PREFIX + "jar.names";
  public final static String P_RECORD = PROPERTY_PREFIX + "record";
  // System properties.
  public final static String P_EXPAND_DIR = JarClassLoader.PROPERTY_PREFIX + "expand.dir";
  @SuppressWarnings("RegExpEmptyAlternationBranch")
  public final static String P_PATH_SEPARATOR = "|";
  public final static String P_ONE_JAR_CLASS_PATH = JarClassLoader.PROPERTY_PREFIX + "class.path";
  public final static String MANIFEST = "META-INF/MANIFEST.MF";

  public final static String BINLIB_PREFIX = "binlib/";
  public final static String MAIN_PREFIX = "main/";
  public final static String MULTI_RELEASE = "Multi-Release";
  public final static String CLASS = ".class";
  public static final String LIB = "lib/";

  protected ClassLoader externalClassLoader;

  private static final Logger LOGGER = Logger.getLogger("JarClassLoader");

  // note: need to retain this name to avoid breaking support in classgraph
  protected String oneJarPath;

  public static final Pattern MR_PATTERN = Pattern.compile("META-INF/versions/(\\d+)/");

  // note: need to retain this name to avoid breaking support in classgraph
  public String getOneJarPath() {
    return oneJarPath;
  }

  // Synchronize for thread safety.  This is less important until we
  // start to do lazy loading, but it's a good idea anyway.
  protected Map<String, ByteCode> byteCode = Collections.synchronizedMap(new HashMap<>());
  protected Map<String, ProtectionDomain> pdCache = Collections.synchronizedMap(new HashMap<>());
  protected Map<String, String> binLibPath = Collections.synchronizedMap(new HashMap<>());
  protected Set<String> jarNames = Collections.synchronizedSet(new HashSet<>());

  protected String mainJar;
  protected boolean delegateToParent;

  protected static class ByteCode {
    public ByteCode(String name, String original, ByteArrayOutputStream baos, String codebase, Manifest manifest, int mrVersion) {
      this.name = name;
      this.original = original;
      this.bytes = baos.toByteArray();
      this.codebase = codebase;
      this.manifest = manifest;
      this.mrVersion = mrVersion;
    }

    public byte[] bytes;
    public String name, original, codebase;
    public Manifest manifest;
    public int mrVersion;
  }



  /*
   * Layout of the uno-jar.jar file
   *
   *  /
   *    /META-INF
   *      /MANIFEST.MF
   *    /com
   *      /needhamsoftware
   *        /unojar
   *          Boot.class
   *          JarClassLoader.class
   *          (supporting classes)
   *    /main
   *      main.jar
   *        /com
   *          /main
   *            Main.class
   *    /lib
   *      util.jar
   *        /com
   *          /util
   *            Util.class
   */

  /**
   * The main constructor for the Jar-capable classloader.
   *
   * @param parent The parent for this class loader.
   * @param unoJarPath the path to the uno-jar we are running from
   */
  public JarClassLoader(ClassLoader parent, String unoJarPath) {
    super(parent);
    this.oneJarPath = unoJarPath;
    delegateToParent = true;
    setProperties(this);
    init();
    // System.out.println(PREFIX() + this + " parent=" + parent + " loaded by " + this.getClass().getClassLoader());
  }

  protected static ThreadLocal<String> current = new ThreadLocal<>();

  /**
   * Common initialization code: establishes a classloader for delegation
   * to uno-jar.class.path resources.
   */
  protected void init() {
    String classpath = System.getProperty(JarClassLoader.P_ONE_JAR_CLASS_PATH);
    if (classpath != null) {
      String[] tokens = classpath.split("\\" + JarClassLoader.P_PATH_SEPARATOR);
      List<URL> list = new ArrayList<>();
      for (String path : tokens) {
        try {
          list.add(new URL(path));
        } catch (MalformedURLException mux) {
          // Try a file:// prefix and an absolute path.
          try {
            String _path = new File(path).getCanonicalPath();
            // URLClassLoader searches in a directory if and only if the path ends with '/':
            // toURI() takes care of adding the trailing slash in this case so everything's ok
            list.add(new File(_path).toURI().toURL());
          } catch (Exception e) {
            LOGGER.warning("Unable to parse external path: " + path + ":- " + e);
          }
        }
      }
      final URL[] urls = list.toArray(new URL[0]);
      LOGGER.info("external URLs=" + Arrays.asList(urls));
      // BUG-2833948
      // Delegate back into this classloader, use ThreadLocal to avoid recursion.
      externalClassLoader = AccessController.doPrivileged(
          new PrivilegedAction<>() {
            public ClassLoader run() {
              return new URLClassLoader(urls, JarClassLoader.this) {
                // Handle recursion for classes, and mutual recursion for resources.
                final static String LOAD_CLASS = "loadClass():";
                final static String GET_RESOURCE = "getResource():";
                final static String FIND_RESOURCE = "findResource():";

                // Protect entry points which could lead to recursion.  Strangely
                // inelegant because you can't proxy a class.  Or use closures.
                @SuppressWarnings("rawtypes")
                public Class loadClass(String name) throws ClassNotFoundException {
                  if (reentered(LOAD_CLASS + name)) {
                    throw new ClassNotFoundException(name);
                  }
                  LOGGER.fine("externalClassLoader.loadClass(" + name + ")");
                  String old = current.get();
                  current.set(LOAD_CLASS + name);
                  try {
                    return super.loadClass(name);
                  } finally {
                    current.set(old);
                  }
                }

                public URL getResource(String name) {
                  if (reentered(GET_RESOURCE + name))
                    return null;
                  LOGGER.fine("externalClassLoader.getResource(" + name + ")");
                  String old = current.get();
                  current.set(GET_RESOURCE + name);
                  try {
                    return super.getResource(name);
                  } finally {
                    current.set(old);
                  }
                }

                public URL findResource(String name) {
                  if (reentered(FIND_RESOURCE + name))
                    return null;
                  LOGGER.fine("externalClassLoader.findResource(" + name + ")");
                  String old = current.get();
                  current.set(name);
                  try {
                    current.set(FIND_RESOURCE + name);
                    return super.findResource(name);
                  } finally {
                    current.set(old);
                  }
                }

                private boolean reentered(String name) {
                  // Defend against null name: not sure about semantics there.
                  Object old = current.get();
                  return old != null && old.equals(name);
                }
              };
            }
          });

    }
  }

  public String load(String mainClass) {
    return load(mainClass, oneJarPath);
  }

  public String load(String mainClass, String jarName) {
    LOGGER.fine("load(" + mainClass + "," + jarName + ")");

    try {
      if (jarName == null) {
        jarName = oneJarPath;
      }
      JarInputStream jis = new JarInputStream(new URL(jarName).openConnection().getInputStream());
      Manifest manifest = jis.getManifest();
      JarEntry entry;
      while ((entry = (JarEntry) jis.getNextEntry()) != null) {
        if (entry.isDirectory())
          continue;

        String $entry = entry.getName();
        if ($entry.startsWith(LIB) || $entry.startsWith(MAIN_PREFIX)) {

          // Load it!
          LOGGER.fine("caching " + $entry);
          LOGGER.fine("using jarFile.getInputStream(" + entry + ")");

          // Note: loadByteCode consumes the input stream, so make sure its scope
          // does not extend beyond here.
          loadByteCode(jis, $entry);

          // Do we need to look for a main class?
          if ($entry.startsWith(MAIN_PREFIX)) {
            if (mainClass == null) {
              JarInputStream mis = new JarInputStream(jis);
              Manifest m = mis.getManifest();
              // Is this a jar file with a manifest?
              if (m != null) {
                mainClass = mis.getManifest().getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
                mainJar = $entry;
              }
            } else if (mainJar != null) {
              LOGGER.warning("A main class is defined in multiple jar files inside " + MAIN_PREFIX + mainJar + " and " + $entry);
              LOGGER.warning("The main class " + mainClass + " from " + mainJar + " will be used");
            }
          }
        } else if ($entry.endsWith(CLASS)) {
          // A plain vanilla class file rooted at the top of the jar file.
          loadBytes(entry, jis, "/", manifest);
          LOGGER.fine("Uno-Jar class: " + jarName + "!/" + entry.getName());
        } else {
          // A resource?
          loadBytes(entry, jis, "/", manifest);
          LOGGER.fine("Uno-Jar resource: " + jarName + "!/" + entry.getName());
        }
      }
      // If mainClass is still not defined, return null.  The caller is then responsible
      // for determining a main class.

    } catch (IOException iox) {
      LOGGER.severe("Unable to load resource: " + iox);
      iox.printStackTrace(System.err);
    }
    return mainClass;
  }

  public static String replaceProps(Map<Object, Object> replace, String string) {
    // Map above takes System props arg and Properties extends Hashtable<Object,Object> :(

    Pattern pat = Pattern.compile("\\$\\{([^}]*)");
    Matcher mat = pat.matcher(string);
    boolean found = mat.find();
    Map<String, Object> props = new HashMap<>();
    while (found) {
      String prop = mat.group(1);
      props.put(prop, replace.get(prop));
      found = mat.find();
    }
    for (Map.Entry<String, Object> stringObjectEntry : props.entrySet()) {
      string = string.replace("${" + stringObjectEntry.getKey() + "}", (String) stringObjectEntry.getValue());
    }
    return string;
  }

  protected void loadByteCode(InputStream is, String jar) throws IOException {
    JarInputStream jis = new JarInputStream(is);
    JarEntry entry;
    // TODO: implement lazy loading of bytecode.
    Manifest manifest = jis.getManifest();
    if (manifest == null) {
      LOGGER.warning("Null manifest from input stream associated with: " + jar);
    }
    while ((entry = jis.getNextJarEntry()) != null) {
      // if (entry.isDirectory()) continue;
      loadBytes(entry, jis, jar, manifest);
    }
    // Add in a fake manifest entry.
    if (manifest != null) {
      entry = new JarEntry(JarClassLoader.MANIFEST);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      manifest.write(baos);
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      loadBytes(entry, bais, jar, manifest);
    }

  }

  protected void loadBytes(JarEntry entry, InputStream is, String jar, Manifest man) throws IOException {
    String entryName = entry.getName();
    int index = entryName.lastIndexOf('.');
    String type = entryName.substring(index + 1);

    // agattung: patch (for One-Jar 0.95)
    // add package handling to avoid NullPointer exceptions
    // after calls to getPackage method of this ClassLoader
    int index2 = entryName.lastIndexOf('/', index - 1);
    if (entryName.endsWith(CLASS) && index2 > -1) {
      String packageName = entryName.substring(0, index2).replace('/', '.');
      if (getDefinedPackage(packageName) == null) {
        // Defend against null manifest.
        if (man != null) {
          definePackage(packageName, man, urlFactory.getCodeBase(jar));
        } else {
          definePackage(packageName, null, null, null, null, null, null, null);
        }
      }
    }
    // end patch

    // Because we are doing stream processing, we don't know what
    // the size of the entries is.  So we store them dynamically.
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    copy(is, baos);

    // If entry is a class, check to see that it hasn't been defined
    // already.  Class names must be unique within a classloader because
    // they are cached inside the VM until the classloader is released.
    if (type.equals("class")) {
      if (alreadyCached(entryName, jar, baos)) return;
      cacheBytes(entry, jar, man, entryName, baos);
      LOGGER.fine("cached bytes for class " + entryName);
    } else {
      // https://github.com/nsoft/uno-jar/issues/10 - package names must not end in /
      if (entryName.endsWith(File.separator)) {
        //System.out.println(entryName);
        entryName = entryName.substring(0, entryName.length() - 1);
      }
      // Another kind of resource.  Cache this by name, and also prefixed
      // by the jar name.  Don't duplicate the bytes.  This allows us
      // to map resource lookups to either jar-local, or globally defined.
      String localname = jar + "/" + entryName;
      cacheBytes(entry, jar, man, localname, baos);
      // Keep a set of jar names so we can do multiple-resource lookup by name
      // as in findResources().
      jarNames.add(jar);
      LOGGER.fine("cached bytes for local name " + localname);
      // Only keep the first non-local entry: this is like classpath where the first
      // to define wins.
      if (alreadyCached(entryName, jar, baos)) return;

      cacheBytes(entry, jar, man, entryName, baos);
      LOGGER.fine("cached bytes for entry name " + entryName);

    }
  }

  /**
   * Cache the bytecode or other bytes. Multi-release resources overwrite their original entries.
   *
   * @param entry     The JarEntry from which to read bytes
   * @param jar       The name of the jar file
   * @param man       The manifest from the jar file
   * @param entryName The name of the entry used as a key in the cache
   * @param baos      The stream to which bytes have been read.
   */
  private void cacheBytes(JarEntry entry, String jar, Manifest man, String entryName, ByteArrayOutputStream baos) {
    boolean multiRelease = man != null && Boolean.TRUE.toString().equals(man.getMainAttributes().getValue(MULTI_RELEASE));
    if (multiRelease) {
      String jVer = System.getProperty("java.version");
      //noinspection StatementWithEmptyBody
      if (!jVer.startsWith("1.")) {
        // determine the major version of java 9+
        int endIndex = jVer.indexOf('.');
        if (endIndex > 0) {
          jVer = jVer.substring(0, endIndex);
        }
        Matcher m = MR_PATTERN.matcher(entryName);

        if (m.find()) {
          //System.out.println(entryName);
          int mrVer = Integer.parseInt(m.group(1));
          m.reset();
          entryName = m.replaceAll("");
          ByteCode byteCode = this.byteCode.get(entryName);
          if (byteCode != null) {
            int oldVer = byteCode.mrVersion;
            if (mrVer > oldVer && mrVer <= Integer.parseInt(jVer)) {
              this.byteCode.put(entryName, new ByteCode(entryName, entry.getName(), baos, jar, man, mrVer));
              return;
            }
          }
        }
      } else {
        // java 8 or earlier, ignore
      }
    }

    byteCode.putIfAbsent(entryName, new ByteCode(entryName, entry.getName(), baos, jar, man, 8));
  }

  /**
   * Override to ensure that this classloader is the thread context classloader
   * when used to load a class.  Avoids subtle, nasty problems.
   */
  @SuppressWarnings("rawtypes")
  public Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
    // Set the context classloader in case any classloaders delegate to it.
    // Otherwise it would default to the sun.misc.Launcher$AppClassLoader which
    // is used to launch the jar application, and attempts to load through
    // it would fail if that code is encapsulated inside the uno-jar.
    if (!isJarClassLoaderAParent(Thread.currentThread().getContextClassLoader())) {
      AccessController.doPrivileged((PrivilegedAction<Class>) () -> {
        Thread.currentThread().setContextClassLoader(JarClassLoader.this);
        return null;
      });
    }
    return super.loadClass(name, resolve);
  }

  public boolean isJarClassLoaderAParent(ClassLoader loader) {
    return loader instanceof JarClassLoader
        || loader != null && (loader.getParent() != null && isJarClassLoaderAParent(loader.getParent()));
  }

  /**
   * Locate the named class in a jar-file, contained inside the
   * jar file which was used to load <u>this</u> class.
   */
  @SuppressWarnings("rawtypes")
  protected Class findClass(String name) throws ClassNotFoundException {
    // Delegate to external paths first
    Class cls;
    if (externalClassLoader != null) {
      try {
        return externalClassLoader.loadClass(name);
      } catch (ClassNotFoundException cnfx) {
        // continue...
      }
    }

    // Make sure not to load duplicate classes.
    cls = findLoadedClass(name);
    if (cls != null) return cls;

    // Look up the class in the byte codes.
    // Translate path?
    LOGGER.fine("findClass(" + name + ")");
    String cache = name.replace('.', '/') + CLASS;
    ByteCode bytecode = byteCode.get(cache);
    if (bytecode != null) {
      LOGGER.fine("found " + name + " in codebase '" + bytecode.codebase + "'");
      // Use a protectionDomain to associate the codebase with the
      // class.
      ProtectionDomain pd = pdCache.get(bytecode.codebase);
      if (pd == null) {
        try {
          URL url = urlFactory.getCodeBase(bytecode.codebase);

          CodeSource source = new CodeSource(url, (Certificate[]) null);
          pd = new ProtectionDomain(source, null, this, null);
          pdCache.put(bytecode.codebase, pd);
        } catch (MalformedURLException mux) {
          throw new ClassNotFoundException(name, mux);
        }
      }

      // Do it the simple way.
      byte[] bytes = bytecode.bytes;

      int i = name.lastIndexOf('.');
      if (i != -1) {
        String pkgname = name.substring(0, i);
        // Check if package already loaded.
        Package pkg = getDefinedPackage(pkgname);
        Manifest man = bytecode.manifest;
        if (pkg != null) {
          // Package found, so check package sealing.
          if (pkg.isSealed()) {
            // Verify that code source URL is the same.
            if (!pkg.isSealed(pd.getCodeSource().getLocation())) {
              throw new SecurityException("sealing violation: package " + pkgname + " is sealed");
            }

          } else {
            // Make sure we are not attempting to seal the package
            // at this code source URL.
            if ((man != null) && isSealed(pkgname, man)) {
              throw new SecurityException("sealing violation: can't seal package " + pkgname + ": already loaded");
            }
          }
        } else {
          if (man != null) {
            definePackage(pkgname, man, pd.getCodeSource().getLocation());
          } else {
            definePackage(pkgname, null, null, null, null, null, null, null);
          }
        }
      }

      return defineClass(name, bytes, pd);
    }
    LOGGER.fine(name + " not found");
    throw new ClassNotFoundException(name);

  }

  private boolean isSealed(String name, Manifest man) {
    String path = name.concat("/");
    Attributes attr = man.getAttributes(path);
    String sealed = null;
    if (attr != null) {
      sealed = attr.getValue(Name.SEALED);
    }
    if (sealed == null) {
      if ((attr = man.getMainAttributes()) != null) {
        sealed = attr.getValue(Name.SEALED);
      }
    }
    return "true".equalsIgnoreCase(sealed);
  }

  /**
   * Defines a new package by name in this ClassLoader. The attributes
   * contained in the specified Manifest will be used to obtain package
   * version and sealing information. For sealed packages, the additional URL
   * specifies the code source URL from which the package was loaded.
   *
   * @param name the package name
   * @param man  the Manifest containing package version and sealing
   *             information
   * @param url  the code source url for the package, or null if none
   * @throws IllegalArgumentException if the package name duplicates an existing package either
   *                                  in this class loader or one of its ancestors
   */
  protected void definePackage(String name, Manifest man, URL url) throws IllegalArgumentException {
    String path = name.concat("/");
    String specTitle = null, specVersion = null, specVendor = null;
    String implTitle = null, implVersion = null, implVendor = null;
    String sealed = null;
    URL sealBase = null;

    Attributes attr = man.getAttributes(path);
    if (attr != null) {
      specTitle = attr.getValue(Name.SPECIFICATION_TITLE);
      specVersion = attr.getValue(Name.SPECIFICATION_VERSION);
      specVendor = attr.getValue(Name.SPECIFICATION_VENDOR);
      implTitle = attr.getValue(Name.IMPLEMENTATION_TITLE);
      implVersion = attr.getValue(Name.IMPLEMENTATION_VERSION);
      implVendor = attr.getValue(Name.IMPLEMENTATION_VENDOR);
      sealed = attr.getValue(Name.SEALED);
    }
    attr = man.getMainAttributes();
    if (attr != null) {
      if (specTitle == null) {
        specTitle = attr.getValue(Name.SPECIFICATION_TITLE);
      }
      if (specVersion == null) {
        specVersion = attr.getValue(Name.SPECIFICATION_VERSION);
      }
      if (specVendor == null) {
        specVendor = attr.getValue(Name.SPECIFICATION_VENDOR);
      }
      if (implTitle == null) {
        implTitle = attr.getValue(Name.IMPLEMENTATION_TITLE);
      }
      if (implVersion == null) {
        implVersion = attr.getValue(Name.IMPLEMENTATION_VERSION);
      }
      if (implVendor == null) {
        implVendor = attr.getValue(Name.IMPLEMENTATION_VENDOR);
      }
      if (sealed == null) {
        sealed = attr.getValue(Name.SEALED);
      }
    }
    if (sealed != null) {
      boolean isSealed = Boolean.parseBoolean(sealed);
      if (isSealed) {
        sealBase = url;
      }
    }
    definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
  }

  @SuppressWarnings("rawtypes")
  protected Class defineClass(String name, byte[] bytes, ProtectionDomain pd) throws ClassFormatError {
    // Simple, non wrapped class definition.
    LOGGER.fine("defineClass(" + name + ")");
    return defineClass(name, bytes, 0, bytes.length, pd);
  }

  /**
   * Make a path canonical, removing . and ..
   *
   * @param path the path to refine
   * @return the path refined to it's canonical version
   */
  protected String canon(String path) {
    path = path.replaceAll("/\\./", "/");
    String canon = path;
    String next;
    do {
      next = canon;
      canon = canon.replaceFirst("([^/]*/\\.\\./)", "");
    } while (!next.equals(canon));
    return canon;
  }

  @Override
  public InputStream getResourceAsStream(String name) {
    Objects.requireNonNull(name);
    // no reason to look further if we already have the bytes
    InputStream byteStream = getByteStream(name);
    if (byteStream != null) {
      return byteStream;
    }
    return super.getResourceAsStream(name);
  }

  /**
   * Overriden to return resources from the appropriate codebase.
   * There are basically two ways this method will be called: most commonly
   * it will be called through the class of an object which wishes to
   * load a resource, i.e. this.getClass().getResourceAsStream().  Before
   * passing the call to us, java.lang.Class mangles the name.  It
   * converts a file path such as foo/bar/Class.class into a name like foo.bar.Class,
   * and it strips leading '/' characters e.g. converting '/foo' to 'foo'.
   * All of which is a nuisance, since we wish to do a lookup on the original
   * name of the resource as present in the Uno-Jar jar files.
   * The other way is more direct, i.e. this.getClass().getClassLoader().getResourceAsStream().
   * Then we get the name unmangled, and can deal with it directly.
   * <p>
   * The problem is this: if one resource is called /foo/bar/data, and another
   * resource is called /foo.bar.data, both will have the same mangled name,
   * namely 'foo.bar.data' and only one of them will be visible.  Perhaps the
   * best way to deal with this is to store the lookup names in mangled form, and
   * simply issue warnings if collisions occur.  This is not very satisfactory,
   * but is consistent with the somewhat limiting design of the resource name mapping
   * strategy in Java today.
   *
   * @param resource where to get the bytes
   * @return a stream of bytes from the resource.
   */
  public InputStream getByteStream(String resource) {

    LOGGER.fine("getByteStream(" + resource + ")");

    InputStream result = null;
    if (externalClassLoader != null) {
      result = externalClassLoader.getResourceAsStream(resource);
    }

    if (result == null) {
      // Delegate to parent classloader first.
      ClassLoader parent = getParent();
      if (parent != null) {
        result = parent.getResourceAsStream(resource);
      }
    }

    if (result == null) {
      // Make resource canonical (remove ., .., etc).
      resource = canon(resource);

      // Look up resolving first.  This allows jar-local
      // resolution to take place.
      ByteCode bytecode = byteCode.get(resolve(resource));
      if (bytecode == null) {
        // Try again with an unresolved name.
        bytecode = byteCode.get(resource);
      }
      if (bytecode != null) result = new ByteArrayInputStream(bytecode.bytes);
    }

    // Contributed by SourceForge "ffrog_8" (with thanks, Pierce. T. Wetter III).
    // Handles JPA loading from jars.
    if (result == null) {
      if (jarNames.contains(resource)) {
        // resource wanted is an actual jar
        LOGGER.info("loading resource file directly" + resource);
        result = super.getResourceAsStream(resource);
      }
    }

    // Special case: if we are a wrapping classloader, look up to our
    // parent codebase.  Logic is that the boot JarLoader will have
    // delegateToParent = false, the wrapping classloader will have
    // delegateToParent = true;
    if (result == null && delegateToParent) {
      result = checkParent(resource);
    }
    LOGGER.fine("getByteStream(" + resource + ") -> " + result);
    return result;
  }

  private InputStream checkParent(String resource) {
    InputStream result;// http://code.google.com/p/onejar-maven-plugin/issues/detail?id=16
    ClassLoader parentClassLoader = getParent();

    // JarClassLoader cannot satisfy requests for actual jar files themselves so it must delegate to it's
    // parent. However, the "parent" is not always a JarClassLoader.
    if (parentClassLoader instanceof JarClassLoader) {
      result = ((JarClassLoader) parentClassLoader).getByteStream(resource);
    } else {
      result = parentClassLoader.getResourceAsStream(resource);
    }
    return result;
  }

  /**
   * Resolve a resource name.  Look first in jar-relative, then in global scope.
   *
   * @param resource The resource to resolve
   * @return the path of the resource if it is known
   */
  protected String resolve(String resource) {

    if (resource.startsWith("/")) resource = resource.substring(1);

    String rsrc;
    String caller = getCaller();
    ByteCode callerCode = byteCode.get(caller);

    rsrc = findRsrc(resource, callerCode);
    if (rsrc == null && resource.endsWith("/")) {
      rsrc = findRsrc(resource.substring(0,resource.length() - 1), callerCode);
    }
    return rsrc;
  }

  private String findRsrc(String resource, ByteCode callerCode) {
    String rsrc = null;
    if (callerCode != null) {
      // Jar-local first, then global.
      String tmp = callerCode.codebase + "/" + resource;
      if (byteCode.get(tmp) != null) {
        rsrc = tmp;
      }
    }
    if (rsrc == null) {
      // One last try.
      if (byteCode.get(resource) != null) {
        rsrc = resource;
      }
    }
    LOGGER.fine("resource " + resource + " resolved to " + rsrc + (callerCode != null ? " in codebase " + callerCode.codebase : " (unknown codebase)"));
    return rsrc;
  }

  protected boolean alreadyCached(String name, String jar, ByteArrayOutputStream baos) {
    // TODO: check resource map to see how we will map requests for this
    //  resource from this jar file.  Only a conflict if we are using a
    //  global map and the resource is defined by more than
    //  one jar file (default is to map to local jar).
    ByteCode existing = byteCode.get(name);
    if (existing != null) {
      byte[] bytes = baos.toByteArray();
      // If bytecodes are identical, no real problem.  Likewise if it's in
      // META-INF.
      if (!Arrays.equals(existing.bytes, bytes) && !name.startsWith("META-INF")) {
        if (name.endsWith(".class")) {
          // This is probably trouble.
          LOGGER.warning(existing.name + " in " + jar + " is hidden by " + existing.codebase + " (with different bytecode)");
        } else {
          LOGGER.info(existing.name + " in " + jar + " is hidden by " + existing.codebase + " (with different bytes)");
        }
      } else {
        LOGGER.fine(existing.name + " in " + jar + " is hidden by " + existing.codebase + " (with same bytecode)");
      }
      // Speedup GC.
      return true;
    }
    return false;
  }


  protected String getCaller() {
    StackWalker walker = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);
    Optional<StackWalker.StackFrame> firstByteCode = walker.walk(s -> s.filter(f -> {
      String caller = f.getClassName();
      String cls = getByteCodeName(caller);
      if (byteCode.get(cls) != null) {
        return !caller.startsWith("com.needhamsoftware.unojar");
      }
      return false;
    }).findFirst());
    return firstByteCode.map(stackFrame -> getByteCodeName(stackFrame.getClassName())).orElse(null);
  }

  private String getByteCodeName(String className) {
    return className.replace(".", "/") + ".class";
  }


  public void setVerbose(boolean verbose) {
    if (verbose) {
      Logger.setLevel(Logger.LOGLEVEL_VERBOSE);
    } else {
      Logger.setLevel(Logger.LOGLEVEL_INFO);
    }
  }

  public void setInfo(boolean info) {
    Logger.setLevel(Logger.LOGLEVEL_INFO);
  }

  public void setSilent(boolean silent) {
    if (silent) {
      Logger.setLevel(Logger.LOGLEVEL_NONE);
    } else {
      Logger.setLevel(Logger.LOGLEVEL_INFO);
    }
  }

  // Injectable URL factory.
  public interface IURLFactory {
    URL getURL(String codebase, String resource) throws MalformedURLException;

    URL getCodeBase(String jar) throws MalformedURLException;
  }

  // Injectable binary library resolver.  E.g suppose you want to place all windows
  // binaries in /binlib/windows, and all redhat-9-i386 binaries in /binlib/redhat/i386/9
  // then you would inject a resolver that checked os.name, os.arch, and os.version,
  // and for redhat-9-i386 returned "redhat/i386/9", for any os.name starting with
  // "windows" returned "windows".
  public interface IBinlibResolver {
    String find(String prefix);
  }

  // Resolve URL from codebase and resource.  Allow URL factory to be specified by
  // user of JarClassLoader.

  /**
   * FileURLFactory generates URL's which are resolved relative to the filesystem.
   * These are compatible with frameworks like Spring, but require knowledge of the
   * location of the uno-jar file via unoJarPath.
   */
  public static class FileURLFactory implements IURLFactory {
    JarClassLoader jcl;

    public FileURLFactory(JarClassLoader jcl) {
      this.jcl = jcl;
    }

    public URLStreamHandler jarHandler = new URLStreamHandler() {
      protected URLConnection openConnection(URL url) throws IOException {
        URLConnection connection = new UnoJarURLConnection(url);
        connection.connect();
        return connection;
      }
    };

    // TODO: Unify getURL and getCodeBase, if possible.
    public URL getURL(String codebase, String resource) throws MalformedURLException {
      if (!codebase.equals("/")) {
        codebase = codebase + "!/";
      } else {
        codebase = "";
      }
      String path = jcl.getOneJarPath() + "!/" + codebase + resource;
      return new URL("jar", "", -1, path, jarHandler);
    }

    public URL getCodeBase(String jar) throws MalformedURLException {
      ProtectionDomain cd = JarClassLoader.class.getProtectionDomain();
      URL url = cd.getCodeSource().getLocation();
      if (url != null) {
        url = new URL("jar", "", -1, url + "!/" + jar, jarHandler);
      }
      return url;
    }
  }

  /**
   * UnoJarURLFactory generates URL's which are efficient, using the in-memory bytecode
   * to access the resources.
   *
   * @author simon
   */
  @SuppressWarnings("unused") // instantiated by reflection!
  public static class UnoJarURLFactory implements IURLFactory {
    public UnoJarURLFactory(JarClassLoader jcl) {
      // Argument not used.
    }

    public URL getURL(String codebase, String resource) throws MalformedURLException {
      String base = resource.endsWith(".class") ? "" : codebase + "/";
      return new URL(Handler.PROTOCOL + ":/" + base + resource);
    }

    public URL getCodeBase(String jar) throws MalformedURLException {
      return new URL(Handler.PROTOCOL + ":" + jar);
    }
  }

  public URL getResource(String name) {
    // Delegate to external first.
    if (externalClassLoader != null) {
      URL url = externalClassLoader.getResource(name);
      if (url != null)
        return url;
    }
    return super.getResource(name);
  }

  protected IURLFactory urlFactory = new FileURLFactory(this);

  // Allow override for urlFactory
  @SuppressWarnings("rawtypes")
  public void setURLFactory(String urlFactory) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SecurityException, IllegalArgumentException, InvocationTargetException {
    Class factory = loadClass(urlFactory);
    try {
      // With single JarClassLoader parameter?

      @SuppressWarnings("unchecked")
      Constructor ctor = factory.getConstructor(JarClassLoader.class);
      this.urlFactory = (IURLFactory) ctor.newInstance(new Object[]{JarClassLoader.this});
    } catch (NoSuchMethodException x) {
      // Default constructor?
      try {
        this.urlFactory = (IURLFactory) loadClass(urlFactory).getDeclaredConstructor().newInstance();
      } catch (NoSuchMethodException e) {
        throw new RuntimeException("Could not load URL factory:" + urlFactory, e);
      }
    }
  }

  // Default implementation handles the legacy uno-jar cases.
  protected IBinlibResolver defaultBinlibResolver = prefix -> {
    final String os = System.getProperty("os.name").toLowerCase();
    final String arch = System.getProperty("os.arch").toLowerCase();

    final String BINLIB_LINUX32_PREFIX = prefix + "linux32/";
    final String BINLIB_LINUX64_PREFIX = prefix + "linux64/";
    final String BINLIB_MACOSX_PREFIX = prefix + "macosx/";
    final String BINLIB_WINDOWS32_PREFIX = prefix + "windows32/";
    final String BINLIB_WINDOWS64_PREFIX = prefix + "windows64/";

    String binlib;

    // Mac
    if (os.startsWith("mac os x")) {
      //TODO Need arch detection on mac
      binlib = BINLIB_MACOSX_PREFIX;
      // Windows
    } else if (os.startsWith("windows")) {
      if (arch.equals("x86")) {
        binlib = BINLIB_WINDOWS32_PREFIX;
      } else {
        binlib = BINLIB_WINDOWS64_PREFIX;
      }
      // So it have to be Linux
    } else {
      if (arch.equals("i386")) {
        binlib = BINLIB_LINUX32_PREFIX;
      } else {
        binlib = BINLIB_LINUX64_PREFIX;
      }
    }
    return binlib;
  };


  protected IBinlibResolver binlibResolver = defaultBinlibResolver;

  // Allow override for urlFactory
  @SuppressWarnings("rawtypes")
  public void setBinlibResolver(String resolver) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SecurityException, IllegalArgumentException, InvocationTargetException {
    Class cls = loadClass(resolver);
    try {
      // With single JarClassLoader parameter?
      @SuppressWarnings("unchecked")
      Constructor ctor = cls.getConstructor(JarClassLoader.class);
      this.binlibResolver = (IBinlibResolver) ctor.newInstance(new Object[]{JarClassLoader.this});
    } catch (NoSuchMethodException x) {
      // Default constructor?
      try {
        this.binlibResolver = (IBinlibResolver) loadClass(resolver).getDeclaredConstructor().newInstance();
      } catch (NoSuchMethodException e) {
        throw new RuntimeException("could not load binlib resolver:" + resolver, e);
      }
    }
  }

  /* (non-Javadoc)
   * @see java.lang.ClassLoader#findResource(java.lang.String)
   */
  // TODO: Revisit the issue of protocol handlers for findResource() and findResources();
  // TODO: I'm not sure this method should be calling getResources at all. java.lang.Classloader will have already
  //  attempted "get" up the tree before calling "find" In a deep hierarchy this causes a redundant round
  //  of getResource requests all the way up the chain.
  protected URL findResource(String $resource) {
    try {
      LOGGER.fine("findResource(\"" + $resource + "\")");
      URL url = externalClassLoader != null ? externalClassLoader.getResource($resource) : null;
      if (url != null) {
        LOGGER.info("findResource() found in external: \"" + $resource + "\"");
        //LOGGER.VERBOSE("findResource(): " + $resource + "=" + url);
        return url;
      }
      // Delegate to parent.
      ClassLoader parent = getParent();
      if (parent != null) {
        url = parent.getResource($resource);
        if (url != null) {
          return url;
        }
      }
      // Do we have the named resource in our cache?  If so, construct a
      // 'onejar:' URL so that a later attempt to access the resource
      // will be redirected to our Handler class, and thence to this class.
      String resource = resolve($resource);
      if (resource != null) {
        // We know how to handle it.
        ByteCode entry = byteCode.get(resource);
        LOGGER.info("findResource() found: \"" + $resource + "\" for caller " + getCaller() + " in codebase " + entry.codebase);
        return urlFactory.getURL(entry.codebase, $resource);
      }
      LOGGER.info("findResource(): unable to locate \"" + $resource + "\"");
      // If all else fails, return null.
      return null;
    } catch (MalformedURLException mux) {
      LOGGER.warning("unable to locate " + $resource + " due to " + mux);
    }
    return null;

  }

  protected Enumeration<URL> findResources(String name) throws IOException {
    LOGGER.info("findResources(" + name + ")");
    LOGGER.info("findResources: looking in " + jarNames);
    Iterator<String> iter = jarNames.iterator();
    final List<URL> resources = new ArrayList<>();
    while (iter.hasNext()) {
      String resource = iter.next() + "/" + name;
      ByteCode entry = byteCode.get(resource);
      if (byteCode.containsKey(resource)) {
        URL url = urlFactory.getURL(entry.codebase, name);
        LOGGER.info("findResources(): Adding " + url + " to resources list.");
        resources.add(url);
      }
    }
    final Iterator<URL> ri = resources.iterator();
    return new Enumeration<>() {
      public boolean hasMoreElements() {
        return ri.hasNext();
      }

      public URL nextElement() {
        return ri.next();
      }
    };
  }

  /**
   * Utility to assist with copying InputStream to OutputStream.  All
   * bytes are copied, but both streams are left open.
   *
   * @param in  Source of bytes to copy.
   * @param out Destination of bytes to copy.
   * @throws IOException if either stream has a problem
   */
  protected void copy(InputStream in, OutputStream out) throws IOException {
    byte[] buf = new byte[1024];
    while (true) {
      int len = in.read(buf);
      if (len < 0) break;
      out.write(buf, 0, len);
    }
  }

  public String toString() {
    return super.toString() + (getName() != null ? "(" + getName() + ")" : "");
  }


  /**
   * Preloader for {@link JarClassLoader#findTheLibrary(String, String)} to allow arch-specific native libraries
   *
   * @param name the (system specific) name of the requested library
   */
  protected String findLibrary(String name) {

    String binlib = binlibResolver.find(BINLIB_PREFIX);
    if (binlibResolver != defaultBinlibResolver && binlib == null)
      binlib = defaultBinlibResolver.find(BINLIB_PREFIX);

    LOGGER.fine("Using arch-specific native library path: " + binlib);

    String retValue = findTheLibrary(binlib, name);
    if (retValue != null) {
      LOGGER.fine("Found in arch-specific directory!");
      return retValue;
    } else {
      LOGGER.fine("Search in standard native directory!");
      return findTheLibrary(BINLIB_PREFIX, name);
    }
  }

  /**
   * If the system specific library exists in the JAR, expand it and return the path
   * to the expanded library to the caller. Otherwise return null so the caller
   * searches the java.library.path for the requested library.
   *
   * @param name          the (system specific) name of the requested library
   * @param BINLIB_PREFIX the (system specific) folder to search in
   * @return the full pathname to the requested library, or null
   * @see Runtime#loadLibrary(String)
   * @since 1.2
   */
  protected String findTheLibrary(String BINLIB_PREFIX, String name) {
    String result = null; // By default, search the java.library.path for it

    String resourcePath = BINLIB_PREFIX + System.mapLibraryName(name);

    // If it isn't in the map, try to expand to temp and return the full path
    // otherwise, remain null so the java.library.path is searched.

    // If it has been expanded already and in the map, return the expanded value
    if (binLibPath.get(resourcePath) != null) {
      result = binLibPath.get(resourcePath);
    } else {

      // See if it's a resource in the JAR that can be extracted
      File tempNativeLib;
      FileOutputStream os;
      try {
        int lastDot = resourcePath.lastIndexOf('.');
        String suffix = null;
        if (lastDot >= 0) {
          suffix = resourcePath.substring(lastDot);
        }
        InputStream is = this.getClass().getResourceAsStream("/" + resourcePath);

        if (is != null) {
          tempNativeLib = File.createTempFile(name + "-", suffix);
          tempNativeLib.deleteOnExit();
          os = new FileOutputStream(tempNativeLib);
          copy(is, os);
          os.close();
          LOGGER.fine("Stored native library " + name + " at " + tempNativeLib);
          result = tempNativeLib.getPath();
          binLibPath.put(resourcePath, result);
        } else {
          // Library is not in the jar
          // Return null by default to search the java.library.path
          LOGGER.fine("No native library at " + resourcePath +
              "java.library.path will be searched instead.");
        }
      } catch (Throwable e) {
        // Couldn't load the library
        // Return null by default to search the java.library.path
        LOGGER.warning("Unable to load native library: " + e);
      }

    }

    return result;
  }

  public void setProperties(IProperties jarLoader) {
    LOGGER.info("setProperties(" + jarLoader + ")");
    if (JarClassLoader.getProperty(JarClassLoader.P_VERBOSE)) {
      jarLoader.setVerbose(true);
    }
    if (JarClassLoader.getProperty(JarClassLoader.P_INFO)) {
      jarLoader.setInfo(true);
    }
    if (JarClassLoader.getProperty(JarClassLoader.P_SILENT)) {
      jarLoader.setSilent(true);
    }
  }

  public static boolean getProperty(String key) {
    return Boolean.parseBoolean(System.getProperty(key, "false"));
  }

}

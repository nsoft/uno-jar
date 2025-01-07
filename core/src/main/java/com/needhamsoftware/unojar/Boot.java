/*
 * Copyright (c) 2004-2010, P. Simon Tuffs (simon@simontuffs.com)
 * Copyright (c) 2019=2020, Needham Software LLC
 * All rights reserved.
 *
 * See the full license at https://github.com/nsoft/uno-jar/blob/master/LICENSE.txt
 * See addition code licenses at: https://github.com/nsoft/uno-jar/blob/master/NOTICE.txt
 */

package com.needhamsoftware.unojar;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import static com.needhamsoftware.unojar.UnoJarPrintlnLogger.defer;
import static com.needhamsoftware.unojar.VersionSpecific.VERSION_SPECIFIC;

/**
 * Run a java application which requires multiple support jars from inside
 * a single jar file.
 *
 * <p>
 * Developer time JVM properties:
 * <pre>
 *   -Duno-jar.main.class={name}  Use named class as main class to run.
 * </pre>
 *
 * Logging for uno-jar itself is kept rudimentary and limited to console
 * output (System.out.println) because uno-jar's code is being kept dependency
 * free. By default only Errors are printed. Obviously in some environments any
 * output to std-out is unacceptable, and
 */
public class Boot {

  public static final String P_DEBUG_PROP_LOADING = JarClassLoader.PROPERTY_PREFIX + "debug.property.loading";

  static {
    // important to load up our properties before anything else happens
    initializeProperties();
  }

  private final static UnoJarPrintlnLogger LOGGER = UnoJarPrintlnLogger.getLogger("Boot");

  public final static String ONE_JAR_CLASSLOADER = "Uno-Jar-Class-Loader";
  public final static String ONE_JAR_MAIN_CLASS = "Uno-Jar-Main-Class";
  public final static String ONE_JAR_DEFAULT_MAIN_JAR = "Uno-Jar-Default-Main-Jar";
  public final static String ONE_JAR_MAIN_ARGS = "Uno-Jar-Main-Args";
  public final static String ONE_JAR_URL_FACTORY = "Uno-Jar-URL-Factory";
  public final static String ONE_JAR_BINLIB_RESOLVER = "Uno-Jar-Binlib-Resolver";

  public final static String MAIN_JAR = "main/main.jar";

  public final static String P_MAIN_CLASS = JarClassLoader.PROPERTY_PREFIX + "main.class";
  public final static String P_MAIN_JAR = JarClassLoader.PROPERTY_PREFIX + "main.jar";
  public final static String P_MAIN_APP = JarClassLoader.PROPERTY_PREFIX + "main.app";
  public final static String P_STATISTICS = JarClassLoader.PROPERTY_PREFIX + "statistics";
  public final static String P_SHOW_PROPERTIES = JarClassLoader.PROPERTY_PREFIX + "show.properties";
  public final static String P_JARPATH = JarClassLoader.PROPERTY_PREFIX + "jar.path";
  // Command-line arguments
  public final static String A_HELP = "--uno-jar-help";
  public final static String A_VERSION = "--uno-jar-version";

  public final static String[] HELP_PROPERTIES = {
      P_MAIN_CLASS, "Specifies the name of the class which should be executed \n(via public static void main(String[])",
      P_MAIN_APP, "Specifies the name of the main/<app>.jar to be executed",
      P_DEBUG_PROP_LOADING, "true: enable special printlns for debugging loading of properties",
      JarClassLoader.P_RECORD, "true:  Enables recording of the classes loaded by the application",
      JarClassLoader.P_JAR_NAMES, "true:  Recorded classes are kept in directories corresponding to their jar names.\n" +
      "false: Recorded classes are flattened into a single directory.  \nDuplicates are ignored (first wins)",
      UnoJarPrintlnLogger.P_LOG_LEVEL, "Control logging level, NONE, ERROR, WARN, INFO, DEBUG, defaults to only print ERROR",
      P_STATISTICS, "true:  Shows statistics about the Uno-Jar Classloader",
      P_JARPATH, "Full path of the uno-Jar file being executed.  \nOnly needed if java.class.path does not contain the path to the jar, e.g. on Max OS/X.",
      JarClassLoader.P_ONE_JAR_CLASS_PATH, "Extra classpaths to be added to the execution environment.  \nUse platform independent path separator '" + JarClassLoader.P_PATH_SEPARATOR + "'",
      JarClassLoader.P_EXPAND_DIR, "Directory to use for expanded files.",
      P_SHOW_PROPERTIES, "true:  Shows the JVM system properties.",
  };

  public final static String[] HELP_ARGUMENTS = {
      A_HELP, "Shows this message, then exits.",
      A_VERSION, "Shows the version of Uno-Jar, then exits.",
  };

  protected static String mainJar;

  protected static boolean statistics = true;
  protected static String myJarPath;

  protected static long startTime = System.currentTimeMillis();
  protected static long endTime = 0;

  // Singleton loader.  This must not be changed once it is set, otherwise all
  // sorts of nasty class-cast exceptions will ensue.  Hence, we control
  // access to it strongly.
  private static JarClassLoader loader = null;

  /**
   * This method provides access to the bootstrap Uno-Jar classloader which
   * is needed in the URL connection Handler when opening streams relative
   * to classes.
   *
   * @return the classloader
   */
  public synchronized static JarClassLoader getClassLoader() {
    return loader;
  }

  protected static void PRINTLN(String message) {
    System.out.println("Boot: " + message);
  }

  public static void main(String[] args) throws Exception {
    args = processArgs(args);
    statistics = Boolean.getBoolean(P_STATISTICS);

    // Is the main class specified on the command line?  If so, boot it.
    // Otherwise, read the main class out of the manifest.
    String mainClass = null;
    initializeProperties();

    try {
      if (Boolean.parseBoolean(System.getProperty(P_SHOW_PROPERTIES, "false"))) {
        // What are the system properties.
        Properties props = System.getProperties();
        @SuppressWarnings("SuspiciousToArrayCall")
        String[] keys = props.keySet().toArray(new String[]{});
        Arrays.sort(keys);

        for (String key : keys) {
          PRINTLN(key + "=" + props.get(key));
        }
      }

      // Process developer properties:
      mainClass = System.getProperty(P_MAIN_CLASS);

      if (mainJar == null) {
        String app = System.getProperty(P_MAIN_APP);
        if (app != null) {
          mainJar = "main/" + app + ".jar";
        } else {
          mainJar = System.getProperty(P_MAIN_JAR, MAIN_JAR);
        }
      }
    } catch (SecurityException x) {
      LOGGER.warning(x.toString());
    }
    // Pick some things out of the top-level JAR file.
    String jar = getMyJarPath();
    JarInputStream jis = new JarInputStream(new URL(jar).openConnection().getInputStream());
    Manifest manifest = jis.getManifest();
    Attributes attributes = manifest.getMainAttributes();
    String bootLoaderName = attributes.getValue(ONE_JAR_CLASSLOADER);

    if (mainJar == null) {
      mainJar = attributes.getValue(ONE_JAR_DEFAULT_MAIN_JAR);
    }

    args = parseMainArgs(args, attributes);

    // If no main-class specified, check the manifest of the main jar for
    // a Boot-Class attribute.
    if (mainClass == null) {
      mainClass = attributes.getValue(ONE_JAR_MAIN_CLASS);
    }

    if (mainClass == null) {
      // Still don't have one (default).  One final try: look for a jar file in a
      // main directory.  There should be only one, and it's manifest
      // Main-Class attribute is the main class.  The JarClassLoader will take
      // care of finding it.
      InputStream is = Boot.class.getResourceAsStream("/" + mainJar);
      if (is != null) {
        JarInputStream mis = new JarInputStream(is);
        Manifest mainmanifest = mis.getManifest();
        jis.close();
        mainClass = mainmanifest.getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
      } else {
        // There is no main jar. Info unless mainJar is empty string.
        // The load(mainClass) will scan for main jars anyway.
        if (!"".equals(mainJar)) {
          LOGGER.info("Unable to locate main jar '%s' in the JAR file %s", mainJar, getMyJarPath());
        }
      }
    }

    synchronized (Boot.class) {
      if (loader != null) throw new RuntimeException("Attempt to set a second Boot loader");
      String myJarPath1 = Boot.getMyJarPath();
      loader = getBootLoader(bootLoaderName, myJarPath1);
    }
    LOGGER.info("using JarClassLoader: %s", getClassLoader().getClass().getName());

    // Allow injection of the URL factory.
    String urlfactory = attributes.getValue(ONE_JAR_URL_FACTORY);
    if (urlfactory != null) {
      loader.setURLFactory(urlfactory);
    }

    String resolver = attributes.getValue(ONE_JAR_BINLIB_RESOLVER);
    if (resolver != null) {
      loader.setBinlibResolver(resolver);
    }

    mainClass = loader.load(mainClass);

    if (mainClass == null)
      throw new Exception(getMyJarName() + " main class was not found (fix: add main/main.jar with a Main-Class manifest attribute, or specify -D" + P_MAIN_CLASS + "=<your.class.name>), or use " + ONE_JAR_MAIN_CLASS + " in the manifest");

    // Guard against the main.jar pointing back to this
    // class, and causing an infinite recursion.
    String bootClass = Boot.class.getName();
    if (bootClass.equals(mainClass))
      throw new Exception(getMyJarName() + " main class (" + mainClass + ") would cause infinite recursion: check main.jar/META-INF/MANIFEST.MF/Main-Class attribute: " + mainClass);

    @SuppressWarnings("rawtypes")
    Class cls = loader.loadClass(mainClass);

    endTime = System.currentTimeMillis();
    showTime();

    @SuppressWarnings("unchecked")
    Method main = cls.getMethod("main", String[].class);
    main.invoke(null, new Object[]{args});
  }

  static String[] parseMainArgs(String[] args, Attributes attributes) {
    if (args.length == 0) {
      String mainargs = attributes.getValue(ONE_JAR_MAIN_ARGS);
      if (mainargs != null) {

        // Replace the args with built-in.  Support escaped whitespace.
        args = mainargs.split("(?<!\\\\)\\s");
        for (int i = 0; i < args.length; i++) {
          args[i] = args[i].replaceAll("\\\\(\\s)", "$1");
          args[i] = JarClassLoader.replaceProps(System.getProperties(), args[i]);
        }
      }
    }
    return args;
  }

  private static void initializeProperties()  {
    {
      Properties properties;
      try {
        properties = getUnoJarProperties();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      // Set system properties only if not already specified.
      @SuppressWarnings("rawtypes")
      Enumeration enm = properties.propertyNames();
      while (enm.hasMoreElements()) {
        String name = (String) enm.nextElement();
        if (System.getProperty(name) == null) {
          System.setProperty(name, properties.getProperty(name));
        }
      }
    }
  }

  private static Properties getUnoJarProperties() throws IOException {
    // Default properties are in resource 'uno-jar.properties'.
    // NOTE: DO NOT use LOGGER in this method it won't be initialized yet.
    Properties properties = new Properties();
    String props = "uno-jar.properties";
    InputStream is = Boot.class.getResourceAsStream("/" + props);
    try {

      if (is != null) {
        propDebug(defer(() -> "loading properties from " + props));
        properties.load(is);
      }
    } finally {
      if (is != null)
        is.close();
    }

    // Merge in anything in a local file with the same name.
    try {
      if (new File(props).exists()) {
        try {
          is = Files.newInputStream(Paths.get(props));
          propDebug(defer(() -> "merging properties from " + props));
          properties.load(is);
        } finally {
          if (is != null) is.close();
        }
      }
    } catch (SecurityException e) {
      System.out.println(e);
    }
    return properties;
  }

  private static void propDebug(UnoJarPrintlnLogger.DeferredLogValue msg) {
    // don't use LOGGER because properties loaded may influence logger level so don't cause
    // that class to load by referencing it before properties are loaded.
    if (Boolean.getBoolean(P_DEBUG_PROP_LOADING)) {
      System.out.println(msg);
    }
  }

  public static void showTime() {
    long endtime = System.currentTimeMillis();
    if (statistics) {
      PRINTLN("Elapsed time: " + (endtime - startTime) + "ms");
    }
  }

  public static String getMyJarName() {
    return new File(getMyJarPath()).getName();
  }

  public interface IJarPath {
    String getOneJarPath();
  }

  public static String getMyJarPath() {
    if (myJarPath != null)
      return myJarPath;
    try {
      myJarPath = System.getProperty(P_JARPATH);
    } catch (SecurityException x) {
      LOGGER.warning(x.toString());
    }
    if (myJarPath == null) {
      try {
        String icb = System.getProperty("uno-jar.ijarpath");
        if (icb != null) {
          @SuppressWarnings("unchecked")
          Class<IJarPath> cls = (Class<IJarPath>) Class.forName(icb);
          IJarPath ic = cls.getDeclaredConstructor().newInstance();
          myJarPath = ic.getOneJarPath();
          return myJarPath;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (myJarPath == null) {
      Class<Boot> cls = Boot.class;
      ProtectionDomain pDomain = cls.getProtectionDomain();
      CodeSource cSource = pDomain.getCodeSource();
      myJarPath = cSource.getLocation().toString();
      LOGGER.info("myJarPath=%s", myJarPath);
      return myJarPath;
    }
    // Normalize those annoying DOS backslashes.
    myJarPath = myJarPath.replace('\\', '/');
    return new File(myJarPath).toURI().toString();
  }

  public static int firstWidth(String[] table) {
    int width = 0;
    for (int i = 0; i < table.length; i += 2) {
      if (table[i].length() > width) width = table[i].length();
    }
    return width;
  }

  public static String pad(String indent, String string, int width) {
    return VERSION_SPECIFIC.pad(indent, string, width);
  }

  public static String wrap(String indent, String string, int width) {
    String padding = pad(indent, "", width);
    return string.replaceAll("\n", "\n" + padding);
  }

  public static String[] processArgs(String[] args) throws Exception {
    // Check for arguments which matter to us, and strip them.
    LOGGER.debug("processArgs(%s)", defer(() -> Arrays.asList(args).toString()));
    ArrayList<String> list = new ArrayList<>();
    for (String argument : args) {
      if (argument.startsWith(A_HELP)) {
        int width = firstWidth(HELP_ARGUMENTS);
        // Width of first column

        System.out.println("Uno-Jar uses the following command-line arguments");
        for (int i = 0; i < HELP_ARGUMENTS.length; i++) {
          System.out.print(pad("    ", HELP_ARGUMENTS[i++], width + 1));
          System.out.println(wrap("    ", HELP_ARGUMENTS[i], width + 1));
        }
        System.out.println();

        width = firstWidth(HELP_PROPERTIES);
        System.out.println("Uno-Jar uses the following VM properties (-D<property>=<true|false|string>)");
        for (int i = 0; i < HELP_PROPERTIES.length; i++) {
          System.out.print(pad("    ", HELP_PROPERTIES[i++], width + 1));
          System.out.println(wrap("    ", HELP_PROPERTIES[i], width + 1));
        }
        System.out.println();
        System.exit(0);
      } else if (argument.startsWith(A_VERSION)) {
        String version = version();
        if (version != null && !"".equals(version.trim())) {
          System.out.println("Uno-Jar version " + version);
        } else {
          System.out.println("Unable to determine Uno-Jar version (missing /.version resource in Uno-Jar archive)");
        }
        System.exit(0);
      } else {
        list.add(argument);
      }
    }
    return list.toArray(new String[0]);
  }

  public static String version() throws IOException {
    InputStream is = Boot.class.getResourceAsStream("/.version");
    String version = null;
    if (is != null) {
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      version = br.readLine();
      br.close();
    }
    return version;
  }

  protected static JarClassLoader getBootLoader(String bootLoaderName, final String jarPath) {
    return AccessController.doPrivileged(
        (PrivilegedAction<JarClassLoader>) () -> {
          if (loader != null) {
            try {
              @SuppressWarnings("rawtypes")
              Class cls = Class.forName(bootLoaderName);
              @SuppressWarnings("unchecked")
              Constructor<JarClassLoader> ctor = cls.getConstructor(ClassLoader.class);
              return ctor.newInstance(Boot.class.getClassLoader());
            } catch (Exception x) {
              LOGGER.warning("Unable to instantiate %s: %s continuing using default %s", loader,x,JarClassLoader.class.getName());
            }
          }
          return new JarClassLoader(Boot.class.getClassLoader(), jarPath);
        }
    );
  }
}

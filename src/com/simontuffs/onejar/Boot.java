/*
 * Copyright (c) 2004, P. Simon Tuffs (simon@simontuffs.com)
 * All rights reserved.
 *
 * See the full license at http://www.simontuffs.com/one-jar/one-jar-license.html
 * This license is also included in the distributions of this software
 * under doc/one-jar-license.txt
 */	 

package com.simontuffs.onejar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * Run a java application which requires multiple support jars from inside
 * a single jar file.
 * 
 * <p>
 * Developer time JVM properties:
 * <pre>
 *   -Done-jar.main-class={name}  Use named class as main class to run. 
 *   -Done-jar.record[=recording] Record loaded classes into "recording" directory.
 *                                Flatten jar-names into directory tree suitable 
 * 								  for use as a classpath.
 *   -Done-jar.jar-names          Record loaded classes, preserve jar structure
 *   -Done-jar.verbose            Run the JarClassLoader in verbose mode.
 * </pre>
 * @author simon@simontuffs.com (<a href="http://www.simontuffs.com">http://www.simontuffs.com</a>)
 */
public class Boot {
	
	/**
	 * The name of the manifest attribute which controls which class 
	 * to bootstrap from the jar file.  The boot class can
	 * be in any of the contained jar files.
	 */
	public final static String BOOT_CLASS = "Boot-Class";
    public final static String ONE_JAR_MAIN_CLASS = "One-Jar-Main-Class";
	
	public final static String MANIFEST = "META-INF/MANIFEST.MF";
	public final static String MAIN_JAR = "main/main.jar";

	public final static String WRAP_CLASS_LOADER = "Wrap-Class-Loader";
    public final static String WRAP_DIR = "wrap";
	public final static String WRAP_JAR = "/" + WRAP_DIR + "/wraploader.jar";

    // System properties.
	public final static String PROPERTY_PREFIX = "one-jar.";
	public final static String P_MAIN_CLASS = PROPERTY_PREFIX + "main-class";
	public final static String P_RECORD = PROPERTY_PREFIX + "record";
	public final static String P_JARNAMES = PROPERTY_PREFIX + "jar-names";
	public final static String P_VERBOSE = PROPERTY_PREFIX + "verbose";
	public final static String P_INFO = PROPERTY_PREFIX + "info";
    
    // Command-line arguments
    public final static String HELP = "--one-jar-help";
    public final static String VERSION = "--one-jar-version";
    
    public final static String[] HELP_PROPERTIES = {
        P_MAIN_CLASS, "Specifies the name of the class which should be executed (via public static void main(String[])", 
        P_RECORD,     "true:  Enables recording of the classes loaded by the application",
        P_JARNAMES,   "true:  Recorded classes are kept in directories corresponding to their jar names.\n" + 
                    "false: Recorded classes are flattened into a single directory.  Duplicates are ignored (first wins)",
        P_VERBOSE,    "true:  Print verbose classloading information", 
        P_INFO,       "true:  Print informative classloading information"
    };
	
    public final static String[] HELP_ARGUMENTS = {
        HELP,       "Shows this message, then exits.",
        VERSION,    "Shows the version of One-JAR, then exits."
    };
    
    
	protected static boolean info, verbose;
    protected static String myJarPath;

	// Singleton loader.
	protected static JarClassLoader loader = null;
	
	public static JarClassLoader getClassLoader() {
		return loader;
	}
    
    public static void setClassLoader(JarClassLoader $loader) {
        if (loader != null) throw new RuntimeException("Attempt to set a second Boot loader");
        loader = $loader;
        setProperties(loader);
    }

	protected static void VERBOSE(String message) {
		if (verbose) System.out.println("Boot: " + message);
	}
    
	protected static void WARNING(String message) {
		System.err.println("Boot: Warning: " + message); 
	}
	
	protected static void INFO(String message) {
		if (info) System.out.println("Boot: Info: " + message);
	}

    public static void main(String[] args) throws Exception {
    	run(args);
    }
    
    public static void run(String args[]) throws Exception {
    	
		if (false) {
			// What are the system properties.
	    	Properties props = System.getProperties();
	    	Enumeration _enum = props.keys();
	    	
	    	while (_enum.hasMoreElements()) {
	    		String key = (String)_enum.nextElement();
	    		System.out.println(key + "=" + props.get(key));
	    	}
		}
        
        processArgs(args);
        
    	// Is the main class specified on the command line?  If so, boot it.
    	// Othewise, read the main class out of the manifest.
		String mainClass = null;
		
		{
			// Default properties are in resource 'one-jar.properties'.
			Properties properties = new Properties();
			String props = "/one-jar.properties";
			InputStream is = Boot.class.getResourceAsStream(props); 
			if (is != null) {
				INFO("loading properties from " + props);
				properties.load(is);
			}
				 
			// Merge in anything in a local file with the same name.
			props = "file:one-jar.properties";
			is = Boot.class.getResourceAsStream(props);
			if (is != null) {
				INFO("loading properties from " + props);
				properties.load(is);
			} 
			// Set system properties only if not already specified.
			Enumeration _enum = properties.propertyNames();
			while (_enum.hasMoreElements()) {
				String name = (String)_enum.nextElement();
				if (System.getProperty(name) == null) {
					System.setProperty(name, properties.getProperty(name));
				}
			}
		}		
		// Process developer properties:
		mainClass = System.getProperty(P_MAIN_CLASS);

		// If no main-class specified, check the manifest of the main jar for
		// a Boot-Class attribute.
		if (mainClass == null) {
            String jar = getMyJarPath();
            JarFile jarFile = new JarFile(jar);
            Manifest manifest = jarFile.getManifest();
            Attributes attributes = manifest.getMainAttributes();
            mainClass = attributes.getValue(ONE_JAR_MAIN_CLASS);
            if (mainClass == null) {
                mainClass = attributes.getValue(BOOT_CLASS);
                if (mainClass != null) {
                    WARNING("The manifest attribute " + BOOT_CLASS + " is deprecated in favor of the attribute " + ONE_JAR_MAIN_CLASS);
                }
            } 
		}
		
		if (mainClass == null) {
			// Still don't have one (default).  One final try: look for a jar file in a
			// main directory.  There should be only one, and it's manifest 
			// Main-Class attribute is the main class.  The JarClassLoader will take
			// care of finding it.
			InputStream is = Boot.class.getResourceAsStream("/" + MAIN_JAR);
			if (is != null) {
				JarInputStream jis = new JarInputStream(is);
				Manifest manifest = jis.getManifest();
				Attributes attributes = manifest.getMainAttributes();
				mainClass = attributes.getValue(Attributes.Name.MAIN_CLASS);
			} else {
			    // There is no main jar. Warning.
                WARNING("Unable to locate " + MAIN_JAR + " in the JAR file " + getMyJarPath());
            }
		}
	
		// Do we need to create a wrapping classloader?  Check for the
		// presence of a "wrap" directory at the top of the jar file.
		URL url = Boot.class.getResource(WRAP_JAR);
		
		if (url != null) {
			// Wrap class loaders.
			JarClassLoader bootLoader = new JarClassLoader(WRAP_DIR);
            setProperties(bootLoader);
			bootLoader.load(null);
			
			// Read the "Wrap-Class-Loader" property from the wraploader jar file.
			// This is the class to use as a wrapping class-loader.
			JarInputStream jis = new JarInputStream(Boot.class.getResourceAsStream(WRAP_JAR));
			String wrapLoader = jis.getManifest().getMainAttributes().getValue(WRAP_CLASS_LOADER);
			if (wrapLoader == null) {
				WARNING(url + " did not contain a " + WRAP_CLASS_LOADER + " attribute, unable to load wrapping classloader");
			} else {
				INFO("using " + wrapLoader);
				Class jarLoaderClass = bootLoader.loadClass(wrapLoader);
				Constructor ctor = jarLoaderClass.getConstructor(new Class[]{ClassLoader.class});
				loader = (JarClassLoader)ctor.newInstance(new Object[]{bootLoader});
			}
				
		} else {
			INFO("using JarClassLoader");
			loader = new JarClassLoader(Boot.class.getClassLoader());
		}
        setProperties(loader);
		mainClass = loader.load(mainClass);
        
        if (mainClass == null && !loader.isExpanding()) 
            throw new Exception(getMyJarName() + " main class was not found (fix: add main/main.jar with a Main-Class manifest attribute, or specify -D" + P_MAIN_CLASS + "=<your.class.name>), or use " + ONE_JAR_MAIN_CLASS + " in the manifest");

        if (mainClass != null) {
        	// Guard against the main.jar pointing back to this
        	// class, and causing an infinite recursion.
            String bootClass = Boot.class.getName();
        	if (bootClass.equals(mainClass))
        		throw new Exception(getMyJarName() + " main class (" + mainClass + ") would cause infinite recursion: check main.jar/META-INF/MANIFEST.MF/Main-Class attribute: " + mainClass);
        	
    		// Set the context classloader in case any classloaders delegate to it.
    		// Otherwise it would default to the sun.misc.Launcher$AppClassLoader which
    		// is used to launch the jar application, and attempts to load through
    		// it would fail if that code is encapsulated inside the one-jar.
    		Thread.currentThread().setContextClassLoader(loader);
            
        	Class cls = loader.loadClass(mainClass);
        	
        	Method main = cls.getMethod("main", new Class[]{String[].class}); 
        	main.invoke(null, new Object[]{args});
        }
    }
    
    public static void setProperties(IProperties jarloader) {
        INFO("setProperties(" + jarloader + ")");
        if (getProperty(P_RECORD)) {
            jarloader.setRecord(true);
            jarloader.setRecording(System.getProperty(P_RECORD));
        } 
        if (getProperty(P_JARNAMES)) {
            jarloader.setRecord(true);
            jarloader.setFlatten(false);
        }
        if (getProperty(P_VERBOSE)) {
            jarloader.setVerbose(true);
            jarloader.setInfo(true);
        } 
        if (getProperty(P_INFO)) {
            jarloader.setInfo(true);
        } 
    }
    
    public static boolean getProperty(String key) {
        return new Boolean(System.getProperty(key, "false")).booleanValue();
    }
    
    public static String getMyJarName() {
        String name = getMyJarPath();
        int last = name.lastIndexOf("/");
        if (last >= 0) {
            name = name.substring(last+1); 
        }
        return name;
    }
    
    public static String getMyJarPath() {
        if (myJarPath != null) {
            return myJarPath;
        }
        myJarPath = System.getProperty(PROPERTY_PREFIX + "jarname"); 
        if (myJarPath == null) {
            try {
                // Hack to obtain the name of this jar file.
                String jarname = System.getProperty(JarClassLoader.JAVA_CLASS_PATH);
                // Open each Jar file looking for this class name.  This allows for
                // JVM's that place more than the jar file on the classpath.
                String jars[] =jarname.split(System.getProperty("path.separator"));
                for (int i=0; i<jars.length; i++) {
                    jarname = jars[i];
                    // Allow for URL based paths, as well as file-based paths.  File
                    InputStream is = null;
                    try {
                        is = new URL(jarname).openStream();
                    } catch (MalformedURLException mux) {
                        // Try a local file.
                        try {
                            is = new FileInputStream(jarname);
                        } catch (IOException iox) {
                            // Ignore...
                            continue;
                        }
                    }
                    JarEntry entry = findJarEntry(new JarInputStream(is), Boot.class.getName().replace('.', '/') + ".class");
                    if (entry != null) {
                        myJarPath = jarname;
                        break;
                    }
                }
            } catch (Exception x) {
                x.printStackTrace();
                WARNING("jar=" + myJarPath + " loaded from " + JarClassLoader.JAVA_CLASS_PATH + " (" + System.getProperty(JarClassLoader.JAVA_CLASS_PATH) + ")");
            }
        }
        // Normalize those annoying DOS backslashes.
        myJarPath = myJarPath.replace('\\', '/');
        return myJarPath;
    }
    
    public static JarEntry findJarEntry(JarInputStream jis, String name) throws IOException {
        JarEntry entry; ;
        while ((entry = jis.getNextJarEntry()) != null) {
            if (entry.getName().equals(name)) {
                return entry;
            }
        }
        return null;
    }
    
    public static int firstWidth(String[] table) {
        int width = 0;
        for (int i=0; i<table.length; i+=2) {
            if (table[i].length() > width) width = table[i].length();
        }
        return width;
    }
    
    public static String pad(String indent, String string, int width) {
        StringBuffer buf = new StringBuffer();
        buf.append(indent);
        buf.append(string);
        for (int i=0; i<width-string.length(); i++) {
            buf.append(" ");
        }
        return buf.toString();
    }
    
    public static String wrap(String indent, String string, int width) {
        String padding = pad(indent, "", width);
        string = string.replaceAll("\n", "\n" + padding);
        return string;
    }

    public static void processArgs(String args[]) throws Exception {
        // Check for arguments which matter to us.  Process them, but pass them through to the
        // application too. (TODO: maybe make this passthrough optional).
        Set arguments = new HashSet(Arrays.asList(args));
        if (arguments.contains(HELP)) {
            int width = firstWidth(HELP_ARGUMENTS);
            // Width of first column
            
            System.out.println("One-Jar uses the following command-line arguments");
            for (int i=0; i<HELP_ARGUMENTS.length; i++) {
                System.out.print(pad("    ", HELP_ARGUMENTS[i++], width+1));
                System.out.println(wrap("    ", HELP_ARGUMENTS[i], width+1));
            }
            System.out.println();
            
            width = firstWidth(HELP_PROPERTIES);
            System.out.println("One-Jar uses the following VM properties (-D<property>=<true|false|string>)");
            for (int i=0; i<HELP_PROPERTIES.length; i++) {
                System.out.print(pad("    ", HELP_PROPERTIES[i++], width+1));
                System.out.println(wrap("    ", HELP_PROPERTIES[i], width+1));
            }
            System.out.println();
            System.exit(0);
        } else if (arguments.contains(VERSION)) {
            InputStream is = Boot.class.getResourceAsStream("/.version");
            String version = new BufferedReader(new InputStreamReader(is)).readLine();
            System.out.println("One-JAR version " + version);
            System.exit(0);
        }
    }
    
}

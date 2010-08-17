/*
 * Copyright (c) 2004-2010, P. Simon Tuffs (simon@simontuffs.com)
 * All rights reserved.
 *
 * See full license at http://one-jar.sourceforge.net/one-jar-license.html
 * This license is also included in the distributions of this software
 * under doc/one-jar-license.txt
 */


/**
 * Note: this class has no dependencies on JUnit, but can be
 * wrapped using a JUnit wrapper to enable it.  See 
 * com.simontuffs.onejar.test.SelfTest for an example of this.
 */
package com.simontuffs.onejar.example.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import com.simontuffs.onejar.Boot;
import com.simontuffs.onejar.JarClassLoader;
import com.simontuffs.onejar.example.external.External;
import com.simontuffs.onejar.example.unique.Unique;
import com.simontuffs.onejar.example.util.Util;
import com.simontuffs.onejar.test.Testable;

/**
 * @author simon@simontuffs.com
 */
public class Test extends Testable {

    // Make sure Java Logging works.
    protected static Logger logger = Logger.getLogger(Test.class.getPackage().getName());
    
    public final static String JAVA_SECURITY_POLICY = "java.security.policy";
    
	public Test() {
		System.out.println("Test: loaded by " + this.getClass().getClassLoader());
		System.out.println("Test: codesource is " + this.getClass().getProtectionDomain().getCodeSource().getLocation());
        System.out.println("Test: java.class.path=" + System.getProperty("java.class.path"));
        boolean security = Boolean.valueOf(System.getProperty("one-jar.test.security", "false")).booleanValue();
        if (security && System.getSecurityManager() == null) {
            String policy = System.getProperty(JAVA_SECURITY_POLICY);
            if (policy == null) {
                // Allow invoker of the JVM to set policy file.
                System.setProperty(JAVA_SECURITY_POLICY, "onejar:/one-jar.policy");
            }
            System.out.println("Test: java.security.policy=" + System.getProperty("java.security.policy"));
            System.setSecurityManager(new SecurityManager());
            System.out.println("Test: security manager installed: " + System.getSecurityManager());
        }
	}
	
	public void testUseUtil() throws Exception {
        count++;
		Util util = new Util();
		util.sayHello();
		System.out.println();
		util.innerClasses();
		System.out.println();
		System.out.println("Test.testUseUtil() OK");
	}
	
	// TODO: Hack the protection domains to use a custom protocol so we can 
	// intercept and parse references to nested jars.  For example,  
	public void testLoadCodeSource() throws Exception {
        count++;
        if (shouldSkip()) return;
		URL codesource = this.getClass().getProtectionDomain().getCodeSource().getLocation();
		System.out.println("testLoadCodeSource(): dumping entries in " + codesource);
		// Can we load from our own codesource (which is a jar file).
		InputStream is = this.getClass().getProtectionDomain().getCodeSource().getLocation().openConnection().getInputStream();
        if (is != null) {
    		JarInputStream jis = new JarInputStream(is);
            
            int count = 0, expected = 28;
    		JarEntry entry = null;
    		while ((entry = jis.getNextJarEntry()) != null) {
    			System.out.println("testLoadCodeSource(): entry=" + entry);
                count++;
    		}
            if (count != expected) {
                fail("testLoadCodeSource(): Error: Huh? Should find " + expected + " entries in codesource, found " + count);
            }
            jis.close();
        }
	
	}
    
    public void testDumpResource(String resource) throws Exception {
        count++;
		InputStream is = Test.class.getResourceAsStream(resource);
		if (is == null) throw new Exception("testDumpResource: Unable to load resource " + resource);
		System.out.println("Test.useResource(" + resource + ") OK");
		// Dump it.
		byte buf[] = new byte[256];
		System.out.println("testDumpResource: " + resource);
		System.out.println("-------------------------------------------");
		while (true) {
			int len = is.read(buf);
			if (len < 0) break;
			System.out.print(new String(buf, 0, len));
		}
		System.out.println("-------------------------------------------");
	}
	
	/**
	 * A badly behaved classloader.  Needs to be wrapped to make it work
	 * properly inside One-Jar.
	 * @author simon@simontuffs.com
	 */
	public static class TestLoader extends ClassLoader {
		// A badly behaved loadClass does not delegate to its loading 
		// class or its parent.
		public Class loadClass(String name) throws ClassNotFoundException {
			System.out.println("TestLoader.loadClass(" + name + ")");
			return super.loadClass(name);
		}
		// A well behaved classloader must delegate findResource to its loading class.
		public URL findResource(String resource) {
			return TestLoader.class.getResource(resource); 
		}
	}
	
	public void testClassLoader() throws ClassNotFoundException {
        count++;
		System.out.println("testClassLoader(): Creating new TestLoader()");
		TestLoader testLoader = new TestLoader();
		// Try it. If wrapped, it should succeed!
		String name = "com.simontuffs.onejar.example.util.Util";
		System.out.println("testClassLoader(): loading " + name);
		testLoader.loadClass(name);
		System.out.println("testClassLoader(): loaded " + name + " OK!");
		try {
			name = "com.simontuffs.onejar.example.util.NonExistent";
			System.out.println("testClassLoader(): loading " + name);
			testLoader.loadClass(name);
			fail("testClassLoader(): Error: Huh?  Should not find " + name);
		} catch (ClassNotFoundException cnfx) {
			System.out.println("testClassLoader(): not found " + name + " OK!");
		}

		// Pick up a class as a resource.
		name = "/com/simontuffs/onejar/example/util/Util.class";
		InputStream is = testLoader.getResourceAsStream(name);
		if (is == null) {
			fail("testClassLoader(): Error: Huh? Should find " + name + " as a resource");
		}
			
	}
	
    public boolean shouldSkip() {
        // Some tests should be skipped unless they are executed under One-JAR.  This test relies on the
        // late-loading property of Java classes (otherwise this whole test class would fail to load).
        try {
            ClassLoader loader = this.getClass().getClassLoader(); 
            if (loader instanceof JarClassLoader) return false;
        } catch (NoClassDefFoundError nfe) {
            System.out.println("skipping test because: : " + nfe);
        }
        skipped ++;
        return true;
    }
    
	public void testClassURL() throws IOException, MalformedURLException {
        count++;
        // Skip this test unless we're running inside One-JAR.
        if (shouldSkip()) return;
		String className = "/com/simontuffs/onejar/example/main/Main.class";
		String resource = "onejar:" + className;
		System.out.println("testClassURL(): Opening onejar resource using new URL(" + resource + ")");
		URL url = new URL(resource);
		InputStream is = url.openStream();
		System.out.println("testClassURL(): Opened: " + url);
		if (is == null) {
			fail("testClassURL(): Error: Huh? Should find " + resource + " as a resource");
		} else {
			System.out.println("testClassURL(): OK.");
		}
		
		// Now do it using getResource().
		System.out.println("testClassURL(): opening using getResource(" + className + ")");
		url = Test.class.getResource(className);
        System.out.println("testClassURL(): Opened: " + url);
		is = url.openStream();
		if (is == null) {
			fail("testClassURL(): Error: Huh? Should find " + resource + " as a resource");
		} else {
			System.out.println("testClassURL(): OK.");
		}
	}
    
	/**
	 * Tests the ability to load a resource from an absolute path
	 * URL, and also relative to this class as an absolute path.
	 * @throws IOException
	 * @throws MalformedURLException
	 */
    public void testResourceURL() throws IOException, MalformedURLException {
        count++;
        if (shouldSkip()) return;
        String image = "/images/button.mail.gif";
        String resource = "onejar:" + image;
        System.out.println("testResourceURL(): Opening onejar resource using new URL(" + resource + ")");
        URL url = new URL(resource);
        InputStream is = url.openStream();
        System.out.println("testResourceURL(): Opened: " + url);
        if (is == null) {
            fail("testResourceURL(): Error: Huh? Should find " + resource + " as a resource");
        } else {
            System.out.println("testResourceURL(): OK.");
        }
        
        // Now do it using getResource().
        System.out.println("testResourceURL(): opening using getResource(" + image + ")");
        url = Test.class.getResource(image);
        System.out.println("testResourceURL(): Opened: " + url);
        if (url == null) {
            fail("testResourceURL(): Error: Huh? Should find " + resource + " using getResource()");
        } else {
            is = url.openStream();
            if (is == null) {
                fail("testResourceURL(): Error: Huh? Should find " + resource + " as a resource");
            } else {
                System.out.println("testResourceURL(): OK.");
            }
        }
    }
    
    /**
     * Test the ability to find resources inside various places.
     */
    public void testFindResourcesInMainJar() throws IOException {
        ClassLoader cl = getClass().getClassLoader();
        System.out.println("classloader=" + cl);
        Enumeration e = cl.getResources("index.html");
        int count = 0;
        while (e.hasMoreElements()) {
            System.out.println("testFindResourcesInJars(): " + e.nextElement());
            count++;
        }
        if (count != 1) {
            fail("testFindResourcesInMain(): Error: Huh? Should find 1 copies of index.html (found " + count + ")");
        } else {
            System.out.println("testFindResourcesInJars(): OK.");
        }
    }
    
    public void testFindResourcesInJars() throws IOException {
        ClassLoader cl = getClass().getClassLoader();
        System.out.println("classloader=" + cl);
        Enumeration e = cl.getResources("duplicate.txt");
        int count = 0;
        while (e.hasMoreElements()) {
            System.out.println("testFindResourcesInJars: " + e.nextElement());
            count++;
        }
        int expect = 3;
        if (count != expect) {
            fail("testFindResourcesInJars(): Error: Huh? Should find " + expect + " copies of duplicate.txt (found " + count + ")");
        } else {
            System.out.println("testFindResourcesInJars(): OK.");
        }
        
    }

    /**
	 * Tests the ability to load a resource from a relative path 
	 * (relative to this class).
	 * @throws IOException
	 * @throws MalformedURLException
	 */
    public void testResourceRelativeURL() throws IOException, MalformedURLException {
        count++;
        String image = "button.mail.1.gif";
        
        // Now do it using getResource().
        System.out.println("testResourceRelativeURL(): opening using getResource(" + image + ")");
        URL url = Test.class.getResource(image);
        System.out.println("testResourceRelativeURL(): Opened: " + url);
        if (url == null) {
            fail("testResourceRelativeURL(): Error: Huh? Should find " + image + " using getResource()");
        } else {
            InputStream is = url.openStream();
            if (is == null) {
                fail("testResourceRelativeURL(): Error: Huh? Should find " + image + " as a resource");
            } else {
                System.out.println("testResourceRelativeURL(): OK.");
            }
        }
    }
    
    public void testImageIcon() {
        count++;
        String image = "button.mail.1.gif";
        URL url = Test.class.getResource(image);
        if (url == null) {
        	fail("testImageIcon(): Error: unable to resolve url for image " + image);
        	return;
        }
        System.out.println("testImageIcon(): loaded image url OK: " + url);
    	ImageIcon icon = new ImageIcon(url);
    	if (icon == null) {
    		fail("testImageIcon(): Error: unable to load icon from " + url);
    		return;
    	}
    	System.out.println("testImageIcon(): loaded image OK: " + icon);
    }
    
    /**
     * Tests the ability to load classes through a classpath argument passed into
     * the bootstrap loader, e.g. -Done-jar.cp=external.jar
     */
    public void testExternal() {
        count++;
        External external = new External();
        ClassLoader cl = external.getClass().getClassLoader();
        System.out.println("testExternal(): " + "External: loaded by: " + cl);
        System.out.println("testExternal(): " + "Codebase: " + external.getClass().getProtectionDomain().getCodeSource());
        external.external();
    }
    
    /**
     * Tests the ability to load classes through a classpath argument passed into
     * the bootstrap loader, e.g. -Done-jar.cp=external.jar
     * BUG-2845624
     */
    public void testExternalFile() throws IOException {
        count++;
        // Open and verify copy using classloader.
        ClassLoader cl = this.getClass().getClassLoader();
        URL url = cl.getResource("expand-subdir.txt");
        copy(url.openStream(), null);
        // Open and verify copy using class.
        InputStream is = this.getClass().getResourceAsStream("/expand-subdir.txt");
        copy(is, null);
    }

    /**
     * Tests the ability to determine the package name for a one-jar loaded class.
     */
    public void testPackageName() {
        count++;
        if (shouldSkip()) return;
        Class cls = this.getClass();
        Package pkg = cls.getPackage();
        if (pkg == null) {
            fail("testPackageName(): Error: package is null for " + cls + " loaded by " + 
                cls.getClassLoader());
            return;
        }
        String packagename = cls.getPackage().getName();
        String expected = cls.getName();
        int last = expected.lastIndexOf(".");
        expected = expected.substring(0, last);
        if (!packagename.equals(expected)) {
            fail("testPackageName(): Error: Whoops: package name '" + packagename + " is not the expected '" + expected + "'");
        } else {
            System.out.println("testPackageName() ok: " + packagename);
        }
        String expect, got;
        expect = "main-manifest.mf Implementation-Title";
        got = pkg.getImplementationTitle();
        if (!expect.equals(got)) {
            fail("testPackageName(): Error in implementation title: expected '" + expect + "' got '" + got + "'");
        }
        expect = "main-manifest.mf Specification-Title";
        got = pkg.getSpecificationTitle();
        if (!expect.equals(got)) {
            fail("testPackageName(): Error in specification title: expected '" + expect + "' got '" + got + "'");
        }
    }
    
    public void testLibPackageName() {
        count++;
        Class cls = Unique.class;
        Package pkg = cls.getPackage();
        if (pkg == null) {
            fail("testPackageName(): Error: package is null for " + cls + " loaded by " + 
                cls.getClassLoader());
            return;
        }
        String packagename = cls.getPackage().getName();
        String expected = cls.getName();
        int last = expected.lastIndexOf(".");
        expected = expected.substring(0, last);
        if (!packagename.equals(expected)) {
            fail("testPackageName(): Error: Whoops: package name '" + packagename + " is not the expected '" + expected + "'");
        } else {
            System.out.println("testPackageName() ok: " + packagename);
        }
        String expect, got;
        expect = "util-manifest.mf Implementation-Title";
        got = pkg.getImplementationTitle();
        if (!got.equals(expect)) {
            fail("testPackageName(): Error in implementation title: package=" + packagename + " expected '" + expect + "' got '" + got + "'");
        }
        expect = "util-manifest.mf Specification-Title";
        got = pkg.getSpecificationTitle();
        if (!got.equals(expect)) {
            fail("testPackageName(): Error in specification title: package=" + packagename + " expected '" + expect + "' got '" + got + "'");
        }
    }

    /**
     * Exercises the Class.getResource() loader mechanisms.
     */
    public void testGetResource() throws IOException {
        count++;
        String resource = "resources/resource-1.txt";
        InputStream is = Test.class.getResource(resource).openStream();
        if (is == null) {
            fail("testGetResource(): Error: unable to load " + resource + " using Test.class().getResource().openStream()");
        } else {
            System.out.println("testGetResource(): OK: able to load " + resource + " using Test.class().getResource().openStream()");
        }
    }
    
    
    /**
     * Tests the ability to load a resource using getResourceAsStream based
     * on the class of this object.  A number of users have reported problems
     * with doing this, resolved by using the thread context classloader (
     * which should not be necessary).
     */
    public void testGetResourceAsStream() {
        count++;
        String resource = "/main-manifest.mf";
        InputStream stream = Test.class.getResourceAsStream(resource);
        if (stream == null) {
            fail("testGetResourceAsStream(): Error: Whoops: unable to load " + resource + " using Test.class.getResourceAsStream() ");
        } else {
            System.out.println("testGetResourceAsStream(): OK: able to load " + resource + " using Test.class.getResourceAsStream()");
        }
        // Strange but true: this case is supposed to fail (or at least it does with the standard Sun URLClassPath.FileLoader, 
        // so one-jar will mimic the same.
        stream = Test.class.getClassLoader().getResourceAsStream(resource);
        if (stream == null) {
            System.out.println("testGetResourceAsStream(): OK: unable to load " + resource + " using Test.class.getClassLoader().getResourceAsStream()");
        } else {
            fail("testGetResourceAsStream(): Error: Whoops: able to load " + resource + " using Test.class.getClassloader().getResourceAsStream(). Classloader=" + Test.class.getClassLoader());
        }
        // This should also pass.
        resource = "main-manifest.mf";
        stream = Test.class.getClassLoader().getResourceAsStream(resource);
        if (stream == null) {
            fail("testGetResourceAsStream(): Error: Whoops: unable to load " + resource + " using Test.class.getClassloader().getResourceAsStream(). Classloader=" + Test.class.getClassLoader());
        } else {
            System.out.println("testGetResourceAsStream(): OK: able to load " + resource + " using Test.class.getClassLoader().getResourceAsStream()");
        }
        // But this should fail, since the path is relative.
        stream = Test.class.getResourceAsStream(resource);
        if (stream != null) {
            fail("testGetResourceAsStream(): Error: Whoops: incorrect load of " + resource + " using Test.class.getResourceAsStream(). Classloader=" + Test.class.getClassLoader());
        } else {
            System.out.println("testGetResourceAsStream(): OK: unable to load " + resource + " using Test.class.getResourceAsStream()");
        }
        
        // The following is expected to fail, since the ClassLoader class is
        // part of the Java bootstrap classloader, and should not be able to
        // see into this codebase.
        if (!shouldSkip()) {
            stream = ClassLoader.class.getResourceAsStream(resource);
            if (stream != null) {
                fail("testGetResourceAsStream(): Error: Whoops: should not be able to load /main-manifest.mf using ClassLoader.class.getResourceAsStream()");
            } else {
                System.out.println("testGetResourceAsStream(): OK: unable to load stream using ClassLoader.class.getResourceAsStream()");
            }
        }
    }
    
    public void testCodesourceURL() {
        count++;
        Class cls = this.getClass();
        System.out.println("codesource=" + cls.getProtectionDomain().getCodeSource().getLocation());
    }
    
    public void testServices() throws IOException {
        count++;
        if (shouldSkip()) return;
        ClassLoader loader = getClass().getClassLoader();
        Enumeration services = loader.getResources("META-INF/services/com.simontuffs.onejar.services.IHelloService");
        int count = 0;
        while (services.hasMoreElements()) {
            URL url = (URL)services.nextElement();
            System.out.println("testServices(): found " + url);
            // Test resource can be opened and read.
            InputStream is = url.openStream();
            copy(is, null);
            is.close();
            count++;
        }
        if (count != 3) {
            fail("testServices(): incorrect number of services found: should be 3, was " + count + " loader=" + loader);
        } else {
            System.out.println("testServices(): OK: found 3 services");
        }
        
    }
    
    public void testExpanded() throws IOException {
        count++;
        if (shouldSkip()) return;
        
        // By the time we get here, the contents of the expand directory in the One-Jar file 
        // should be present in the filesystem.  Verify this.
        // TODO: remove use of JarFile to support URL paths properly.
        String jarName = new URL(Boot.getMyJarPath()).getPath();
        JarFile jarFile = new JarFile(jarName);
        Enumeration _enum = jarFile.entries();
        Manifest manifest = jarFile.getManifest();
        String expand = manifest.getMainAttributes().getValue(JarClassLoader.EXPAND);
        if (expand == null) 
            return;
        String paths[] = expand.split(",");

        System.out.println("testExpanded(): checking expanded files");
        String missing = "";
        while (_enum.hasMoreElements()) {
            JarEntry entry = (JarEntry)_enum.nextElement();
            String name = entry.getName();
            if (JarClassLoader.shouldExpand(paths, entry.getName())) {
                if (!new File(name).exists()) {
                    System.out.println("testExpanded(): Unable to locate expanded file: " + name);
                    if (missing.length() > 0) 
                        missing +=",";
                    missing += name;
                } else {
                    System.out.println("testExpanded(): Found: " + name);
                }
            }
        }
        // Test the reverse, for each entry in expandedPaths, there should be a file in the
        // filesystem.
        for (int i=0; i<paths.length; i++) {
            String name = paths[i];
            if (! new File(name).exists()) {
                System.out.println("testExpanded(): Unable to locate expansion path: " + name);
                if (missing.length() > 0) 
                    missing +=",";
                missing += name;
            }
        }
        
        if (missing.length() > 0) {
            fail("testExpanded(): missing expanded file(s): " + missing);
        }

    }
    
    public void testContentType() throws Exception {
        count++;
        if (shouldSkip()) return;
        String uri = "onejar:index.html";
        URL url = new URL(uri);
        URLConnection connection = url.openConnection();
        String contenttype = connection.getContentType();
        if (!contenttype.equals("text/html")) {
            fail("Unexpected content type for " + uri + ": " + contenttype);
        }
    }
    
    public void testHtmlAnchor() throws Exception {
        count++;
        if (shouldSkip()) return;
        String uri = "onejar:index.html#anchor";
        URL url = new URL(uri);
        InputStream is = url.openStream();
        if (is == null) {
            fail("Unable to load anchored URL: " + uri);
        }
        System.out.println("testHtmlAnchor(): found URL with anchor OK: " + uri);
    }
    
    public void testExpand() throws Exception {
        count++;
        if (!new File("expand").exists()) {
            fail("expand directory does not exist");
        }
        System.out.println("testExpand(): found 'expand' OK");
    }
    
    public void testLogging() throws Exception {
        count++;
        logger.info("testLogging(): Logging is working");
    }
    
   /**
    * Tests the ability to load all MANIFEST.MFs in all jars in the classpath.
    */
   public void testFindAllManifestMfs() throws IOException {
       count++;
       ClassLoader loader = getClass().getClassLoader();
       Enumeration manifests = loader.getResources("META-INF/MANIFEST.MF");
       int manifestCount = 0;
       String found = "";
       while (manifests.hasMoreElements()) {
           URL manifest = (URL)manifests.nextElement();
           System.out.println("testFindAllManifestMfs(): found: " + manifest);
           found += manifest + "\n";
           manifestCount++;
           // Make sure we can open and read it.
           InputStream is = manifest.openStream();
           copy(is, null);
           is.close();
       }

       // Should be at least 4 manifests in the whole classpath
       if (manifestCount < 4) {
           fail("testFindAllManifestMfs(): incorrect number of manifests found: should be more than 3, " +
                "was " + manifestCount + " loader=" + loader);
       } else {
           System.out.println("testFindAllManifestMfs(): OK: found 4 or more manifests");
       }

       String expected[] = new String[]{"/wrap/wraploader.jar", "/main/main.jar", 
               "/lib/util.jar", "/lib/english.jar", 
               "/lib/french.jar", "/lib/german.jar"
       };
       for (int i=0; i<expected.length; i++) {
           if (!found.contains(expected[i])) {
               fail("manifest list did not contain expected entry: " + expected[i]);
           }
       }
           
   }

   public void testRelativeURL() throws Exception {
       final URL url1 = this.getClass().getResource( "x1.txt" );
       if (url1 == null) {
           fail("url x1.txt was null");
       } else {
           System.out.println("testRelativeURL: url1=" + url1);
           // Can we open it?
           copy(url1.openStream(), null);
           final URL url2 = new URL( url1, "x2.txt" );
           if (url2 == null) {
               fail("testRelativeURL: url x2.txt was null relative to url1");
           } else {
               System.out.println("url2=" + url2);
               // Can we open it?
               copy(url2.openStream(), null);
           }
       }
   }

   /**
    * Utility to assist with copying InputStream to OutputStream.  All
    * bytes are copied, but both streams are left open.
    * @param in Source of bytes to copy.
    * @param out Destination of bytes to copy.
    * @throws IOException
    */
   public static void copy(InputStream in, OutputStream out) throws IOException {
       byte[] buf = new byte[1024];
       while (true) {
           int len = in.read(buf);
           if (len < 0) break;
           if (out != null) 
               out.write(buf, 0, len);
       }
   }

}

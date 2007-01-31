/*
 * Copyright (c) 2004, P. Simon Tuffs (simon@simontuffs.com)
 * All rights reserved.
 *
 * See full license at http://one-jar.sourceforge.net/one-jar-license.txt
 * This license is also included in the distributions of this software
 * under doc/one-jar-license.txt
 */


/**
 * Note: this class has no dependencies on JUnit, but can be
 * wrapped using a JUnit wrapper to enable it.  See 
 * com.simontuffs.onejar.test.SelfTest for an example of this.
 */
package com.simontuffs.onejar.example.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.swing.ImageIcon;

import com.simontuffs.onejar.Boot;
import com.simontuffs.onejar.JarClassLoader;
import com.simontuffs.onejar.example.external.External;
import com.simontuffs.onejar.example.util.Util;

/**
 * @author simon@simontuffs.com
 */
public class Test {
    
    public Error cause;
    public int failures;
    
	public Test() {
		System.out.println("Test: loaded by " + this.getClass().getClassLoader());
		System.out.println("Test: codesource is " + this.getClass().getProtectionDomain().getCodeSource().getLocation());
	}
	
	public void testUseUtil() throws Exception {
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
		URL codesource = this.getClass().getProtectionDomain().getCodeSource().getLocation();
		System.out.println("testLoadCodeSource(): dumping entries in " + codesource);
		// Can we load from our own codesource (which is a jar file).
		InputStream is = this.getClass().getProtectionDomain().getCodeSource().getLocation().openConnection().getInputStream();
		JarInputStream jis = new JarInputStream(is);
        
        int count = 0, expected = 20;
		JarEntry entry = null;
		while ((entry = jis.getNextJarEntry()) != null) {
			System.out.println("testLoadCodeSource(): entry=" + entry);
            count++;
		}
        if (count != expected) {
            fail("testLoadCodeSource(): Error: Huh? Should find " + expected + " entries in codesource, found " + count);
        }
	
	}
    
    public void fail(String reason) throws Error {
        cause = new Error(reason);
        System.out.println("******************************************************************************************************************");
        System.out.println("* fail: " + reason);
        System.out.println("******************************************************************************************************************");
        failures++;
    }
	
	public void testDumpResource(String resource) throws Exception {
		InputStream is = this.getClass().getResourceAsStream(resource);
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
			return this.getClass().getResource(resource); 
		}
	}
	
	public void testClassLoader() throws ClassNotFoundException {
		System.out.println("testClassLoader(): Creating new TestLoader()");
		TestLoader testLoader = new TestLoader();
		// Try it. If wrapped, it should succeed!
		String name = "com.simontuffs.onejar.example.util.Util";
		System.out.println("testClassLoader(): loading " + name);
		testLoader.loadClass(name);
		System.out.println("testClassLoader(): loaded " + name + " OK!");
		try {
			name = "com.simontuffs.onejar.example.util.NonExistant";
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
	
	public void testClassURL() throws IOException, MalformedURLException {
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
		url = this.getClass().getResource(className);
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
        url = this.getClass().getResource(image);
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
	 * Tests the ability to load a resource from a relative path 
	 * (relative to this class).
	 * @throws IOException
	 * @throws MalformedURLException
	 */
    public void testResourceRelativeURL() throws IOException, MalformedURLException {
        String image = "button.mail.1.gif";
        
        // Now do it using getResource().
        System.out.println("testResourceRelativeURL(): opening using getResource(" + image + ")");
        URL url = this.getClass().getResource(image);
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
        String image = "button.mail.1.gif";
        URL url = this.getClass().getResource(image);
        if (url == null) {
        	fail("testImageIcon(): Error: unable to resolve url for image " + image + ": " + url);
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
        External external = new External();
        external.external();
    }
    
    /**
     * Tests the ability to determine the package name for a one-jar loaded class.
     */
    public void testPackageName() {
        Package pkg = this.getClass().getPackage();
        if (pkg == null) {
            fail("testPackageName(): Error: package is null for " + this.getClass() + " loaded by " + 
                this.getClass().getClassLoader());
            return;
        }
        String packagename = this.getClass().getPackage().getName();
        String expected = this.getClass().getName();
        int last = expected.lastIndexOf(".");
        expected = expected.substring(0, last);
        if (!packagename.equals(expected)) {
            fail("testPackageName(): Error: Whoops: package name '" + packagename + " is not the expected '" + expected + "'");
        } else {
            System.out.println("testPackageName() ok: " + packagename);
        }
    }
    
    /**
     * Tests the ability to load a resource using getResourceAsStream based
     * on the class of this object.  A number of users have reported problems
     * with doing this, resolved by using the thread context classloader (
     * which should not be necessary).
     */
    public void testGetResourceAsStream() {
        InputStream stream = Test.class.getResourceAsStream("/main-manifest.mf");
        if (stream == null) {
            fail("testGetResourceAsStream(): Error: Whoops: unable to load /main-manifest.mf using Test.class.getResourceAsStream()");
        } else {
            System.out.println("testGetResourceAsStream(): OK: able to load stream using Test.class.getResourceAsStream()");
        }
        stream = Test.class.getClassLoader().getResourceAsStream("/main-manifest.mf");
        if (stream == null) {
            fail("testGetResourceAsStream(): Error: Whoops: unable to load /main-manifest.mf using Test.class.getClassloader().getResourceAsStream()");
        } else {
            System.out.println("testGetResourceAsStream(): OK: able to load stream using Test.class.getClassLoader().getResourceAsStream()");
        }
        // The following is expected to fail, since the ClassLoader class is
        // part of the Java bootstrap classloader, and should not be able to
        // see into this codebase.
        stream = ClassLoader.class.getResourceAsStream("/main-manifest.mf");
        if (stream != null) {
            fail("testGetResourceAsStream(): Error: Whoops: should not be able to load /main-manifest.mf using ClassLoader.class.getResourceAsStream()");
        } else {
            System.out.println("testGetResourceAsStream(): OK: unable to load stream using ClassLoader.class.getResourceAsStream()");
        }
    }
    
    public void testServices() throws IOException {
        ClassLoader loader = getClass().getClassLoader();
        Enumeration services = loader.getResources("META-INF/services/com.simontuffs.onejar.services.IHelloService");
        int count = 0;
        while (services.hasMoreElements()) {
            System.out.println("testServices(): found " + services.nextElement());
            count++;
        }
        if (count != 3) {
            fail("testServices(): incorrect number of services found: should be 3, was " + count + "\nloader=" + loader);
        } else {
            System.out.println("testServices(): OK: found 3 services");
        }
        
    }
    
    public void testExpanded() throws IOException {
        // By the time we get here, the contents of the expand directory in the One-Jar file 
        // should be present in the filesystem.  Verify this.
        String jarName = Boot.getMyJarName();
        JarFile jarFile = new JarFile(jarName);
        Enumeration _enum = jarFile.entries();
        Manifest manifest = jarFile.getManifest();
        String paths[] = manifest.getMainAttributes().getValue(JarClassLoader.EXPAND).split(",");

        System.out.println("testExpanded(): checking expanded files");
        boolean missing = false;
        while (_enum.hasMoreElements()) {
            JarEntry entry = (JarEntry)_enum.nextElement();
            String name = entry.getName();
            if (JarClassLoader.shouldExpand(paths, entry.getName())) {
                if (!new File(name).exists()) {
                    System.out.println("testExpanded(): Unable to locate expanded file: " + name);
                    missing = true;
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
                missing = true;
            }
        }
        
        if (missing) {
            fail("testExpanded(): missing expanded file");
        }

    }
    
    public void testContentType() throws Exception {
        String uri = "onejar:index.html";
        URL url = new URL(uri);
        URLConnection connection = url.openConnection();
        String contenttype = connection.getContentType();
        if (!contenttype.equals("text/html")) {
            fail("Unexpected content type for " + uri + ": " + contenttype);
        }
    }
    
    public void testHtmlAnchor() throws Exception {
        String uri = "onejar:index.html#anchor";
        URL url = new URL(uri);
        InputStream is = url.openStream();
        if (is == null) {
            fail("Unable to load anchored URL: " + uri);
        }
    }
    
}

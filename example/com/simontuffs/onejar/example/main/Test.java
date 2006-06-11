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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.swing.ImageIcon;

import com.simontuffs.onejar.example.external.External;
import com.simontuffs.onejar.example.util.Util;

/**
 * @author simon@simontuffs.com
 */
public class Test {
    
	public int failures = 0;

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
        
        int count = 0, expected = 19;
		JarEntry entry = null;
		while ((entry = jis.getNextJarEntry()) != null) {
			System.out.println("testLoadCodeSource(): entry=" + entry);
            count++;
		}
        if (count != expected) {
            System.out.println("testLoadCodeSource(): Error: Huh? Should find " + expected + " entries in codesource, found " + count);
            failures++;
        }
	
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
			System.out.println("testClassLoader(): Error: Huh?  Should not find " + name);
			failures++;
		} catch (ClassNotFoundException cnfx) {
			System.out.println("testClassLoader(): not found " + name + " OK!");
		}

		// Pick up a class as a resource.
		name = "/com/simontuffs/onejar/example/util/Util.class";
		InputStream is = testLoader.getResourceAsStream(name);
		if (is == null) {
			System.out.println("testClassLoader(): Error: Huh? Should find " + name + " as a resource");
			failures++;
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
			System.out.println("testClassURL(): Error: Huh? Should find " + resource + " as a resource");
			failures++;
		} else {
			System.out.println("testClassURL(): OK.");
		}
		
		// Now do it using getResource().
		System.out.println("testClassURL(): opening using getResource(" + className + ")");
		url = this.getClass().getResource(className);
        System.out.println("testClassURL(): Opened: " + url);
		is = url.openStream();
		if (is == null) {
			System.out.println("testClassURL(): Error: Huh? Should find " + resource + " as a resource");
			failures++;
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
            System.out.println("testResourceURL(): Error: Huh? Should find " + resource + " as a resource");
            failures++;
        } else {
            System.out.println("testResourceURL(): OK.");
        }
        
        // Now do it using getResource().
        System.out.println("testResourceURL(): opening using getResource(" + image + ")");
        url = this.getClass().getResource(image);
        System.out.println("testResourceURL(): Opened: " + url);
        if (url == null) {
            System.out.println("testResourceURL(): Error: Huh? Should find " + resource + " using getResource()");
            failures++;
        } else {
            is = url.openStream();
            if (is == null) {
                System.out.println("testResourceURL(): Error: Huh? Should find " + resource + " as a resource");
                failures++;
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
            System.out.println("testResourceRelativeURL(): Error: Huh? Should find " + image + " using getResource()");
            failures++;
        } else {
            InputStream is = url.openStream();
            if (is == null) {
                System.out.println("testResourceRelativeURL(): Error: Huh? Should find " + image + " as a resource");
                failures++;
            } else {
                System.out.println("testResourceRelativeURL(): OK.");
            }
        }
    }
    
    public void testImageIcon() {
        String image = "button.mail.1.gif";
        URL url = this.getClass().getResource(image);
        if (url == null) {
        	System.out.println("testImageIcon(): Error: unable to resolve url for image " + image + ": " + url);
        	failures++;
        	return;
        }
        System.out.println("testImageIcon(): loaded image url OK: " + url);
    	ImageIcon icon = new ImageIcon(url);
    	if (icon == null) {
    		System.out.println("testImageIcon(): Error: unable to load icon from " + url);
    		failures++;
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
            System.out.println("testPackageName(): Error: package is null for " + this.getClass() + " loaded by " + 
                this.getClass().getClassLoader());
            failures++;
            return;
        }
        String packagename = this.getClass().getPackage().getName();
        String expected = this.getClass().getName();
        int last = expected.lastIndexOf(".");
        expected = expected.substring(0, last);
        if (!packagename.equals(expected)) {
            System.out.println("testPackageName(): Error: Whoops: package name '" + packagename + " is not the expected '" + expected + "'");
            failures++;
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
            System.out.println("testGetResourceAsStream(): Error: Whoops: unable to load /main-manifest.mf using Test.class.getResourceAsStream()");
            failures++;
        } else {
            System.out.println("testGetResourceAsStream(): OK: able to load stream using Test.class.getResourceAsStream()");
        }
        stream = Test.class.getClassLoader().getResourceAsStream("/main-manifest.mf");
        if (stream == null) {
            System.out.println("testGetResourceAsStream(): Error: Whoops: unable to load /main-manifest.mf using Test.class.getClassloader().getResourceAsStream()");
            failures++;
        } else {
            System.out.println("testGetResourceAsStream(): OK: able to load stream using Test.class.getClassLoader().getResourceAsStream()");
        }
        // The following is expected to fail, since the ClassLoader class is
        // part of the Java bootstrap classloader, and should not be able to
        // see into this codebase.
        stream = ClassLoader.class.getResourceAsStream("/main-manifest.mf");
        if (stream != null) {
            System.out.println("testGetResourceAsStream(): Error: Whoops: should not be able to load /main-manifest.mf using ClassLoader.class.getResourceAsStream()");
            failures++;
        } else {
            System.out.println("testGetResourceAsStream(): OK: unable to load stream using ClassLoader.class.getResourceAsStream()");
        }
    }
}

/*
 * Copyright (c) 2004, P. Simon Tuffs (simon@simontuffs.com)
 * All rights reserved.
 *
 * See full license at http://one-jar.sourceforge.net/one-jar-license.txt
 * This license is also included in the distributions of this software
 * under doc/one-jar-license.txt
 */

package com.simontuffs.onejar.example.main;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

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
	
	public void useUtil() throws Exception {
		Util util = new Util();
		util.sayHello();
		System.out.println();
		util.innerClasses();
		System.out.println();
		System.out.println("Test.useUtil() OK");
	}
	
	// TODO: Hack the protection domains to use a custom protocol so we can 
	// intercept and parse references to nested jars.  For example,  
	public void loadCodeSource() throws Exception {
		URL codesource = this.getClass().getProtectionDomain().getCodeSource().getLocation();
		System.out.println("Test.loadCodeSource(): dumping entries in " + codesource);
		// Can we load from our own codesource (which is a jar file).
		InputStream is = this.getClass().getProtectionDomain().getCodeSource().getLocation().openConnection().getInputStream();
		JarInputStream jis = new JarInputStream(is);
        
        int count = 0, expected = 17;
		JarEntry entry = null;
		while ((entry = jis.getNextJarEntry()) != null) {
			System.out.println("Test: entry=" + entry);
            count++;
		}
        if (count != expected) {
            System.out.println("Huh? Should find " + expected + " entries in codesource, found " + count);
            failures++;
        }
	
	}
	
	public void dumpResource(String resource) throws Exception {
		InputStream is = this.getClass().getResourceAsStream(resource);
		if (is == null) throw new Exception("dumpResource: Unable to load resource " + resource);
		System.out.println("Test.useResource(" + resource + ") OK");
		// Dump it.
		byte buf[] = new byte[256];
		System.out.println("dumpResource: " + resource);
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
	
	public void classLoader() throws ClassNotFoundException {
		System.out.println("Creating new TestLoader()");
		TestLoader testLoader = new TestLoader();
		// Try it. If wrapped, it should succeed!
		String name = "com.simontuffs.onejar.example.util.Util";
		System.out.println("loading " + name);
		testLoader.loadClass(name);
		System.out.println("loaded " + name + " OK!");
		try {
			name = "com.simontuffs.onejar.example.util.NonExistant";
			System.out.println("loading " + name);
			testLoader.loadClass(name);
			System.out.println("Huh?  Should not find " + name);
			failures++;
		} catch (ClassNotFoundException cnfx) {
			System.out.println("not found " + name + " OK!");
		}

		// Pick up a class as a resource.
		name = "/com/simontuffs/onejar/example/util/Util.class";
		InputStream is = testLoader.getResourceAsStream(name);
		if (is == null) {
			System.out.println("Huh? Should find " + name + " as a resource");
			failures++;
		}
			
	}
	
	public void classURL() throws IOException, MalformedURLException {
		String className = "/com/simontuffs/onejar/example/main/Main.class";
		String resource = "onejar:" + className;
		System.out.println("classURL(): Opening onejar resource using new URL(" + resource + ")");
		URL url = new URL(resource);
		InputStream is = url.openStream();
		System.out.println("classURL(): Opened: " + url);
		if (is == null) {
			System.out.println("Huh? Should find " + resource + " as a resource");
			failures++;
		} else {
			System.out.println("classURL(): OK.");
		}
		
		// Now do it using getResource().
		System.out.println("classURL(): opening using getResource(" + className + ")");
		url = this.getClass().getResource(className);
		is = url.openStream();
		if (is == null) {
			System.out.println("Huh? Should find " + resource + " as a resource");
			failures++;
		} else {
			System.out.println("classURL(): OK.");
		}
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
            System.out.println("testPackageName(): Error - package is null for " + this.getClass() + " loaded by " + 
                this.getClass().getClassLoader());
            failures++;
            return;
        }
        String packagename = this.getClass().getPackage().getName();
        String expected = this.getClass().getName();
        int last = expected.lastIndexOf(".");
        expected = expected.substring(0, last);
        if (!packagename.equals(expected)) {
            System.out.println("Whoops: package name '" + packagename + " is not the expected '" + expected + "'");
            failures++;
        } else {
            System.out.println("Package name ok: " + packagename);
        }
    }
}

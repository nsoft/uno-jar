/*
 * Copyright (c) 2004-2010, P. Simon Tuffs (simon@simontuffs.com)
 * All rights reserved.
 *
 * See full license at http://one-jar.sourceforge.net/one-jar-license.html
 * This license is also included in the distributions of this software
 * under doc/one-jar-license.txt
 */

package com.simontuffs.onejar.example.util;

import java.io.InputStream;

/**
 * @author simon@simontuffs.com
 */
public class Util {
    
    protected int variable;
    
	public Util() {
		System.out.println("Util: loaded by " + this.getClass().getClassLoader());
	}
	
	// This class is never used, and should be pruned from the jar file.
	public static class NeverUsedClass {
		NeverUsedClass() {
		}
	}
	
	public class InnerClass {
		InnerClass() {
			System.out.println("Util.InnerClass loaded by " + this.getClass().getClassLoader());
            System.out.println("Util.variable=" + variable++);
		}
	}
	
	public static class StaticInnerClass {
		StaticInnerClass() {
			System.out.println("Util.StaticInnerClass loaded by " + this.getClass().getClassLoader());
		}
	}
	
	
	public void sayHello() throws Exception {
		System.out.println("Util.sayHello()");
		// Dump a resource relative to this jar file.
		dumpResource("/duplicate.txt");
	}
	
	public void dumpResource(String resource) throws Exception {
		InputStream is = Util.class.getResourceAsStream(resource);
		if (is == null) throw new Exception("Unable to load resource " + resource);
		System.out.println("Test.useResource(" + resource + ") OK");
		// Dump it.
		byte buf[] = new byte[256];
		System.out.println("-------------------------------------------");
		while (true) {
			int len = is.read(buf);
			if (len < 0) break;
			System.out.print(new String(buf, 0, len));
		}
		System.out.println("-------------------------------------------");
	}

	public void innerClasses() {
		new InnerClass();
		new StaticInnerClass();
	}
}

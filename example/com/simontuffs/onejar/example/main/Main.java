/*
 * Copyright (c) 2004, P. Simon Tuffs (simon@simontuffs.com)
 * All rights reserved.
 *
 * See full license at http://one-jar.sourceforge.net/one-jar-license.txt
 * This license is also included in the distributions of this software
 * under doc/one-jar-license.txt
 */

package com.simontuffs.onejar.example.main;

import java.util.Date;

/**
 * @author simon@simontuffs.com
 */
public class Main {

    public static void main(String[] args) throws Exception {
    	System.out.print("Main: " + Main.class.getName() + ".main(");
    	for (int i=0; i<args.length; i++) {
			if (i > 0) System.out.print(" ");
    		System.out.print(args[i]);
    	}
    	System.out.println(")");
    	Test test = new Test();
    	
    	long start = new Date().getTime();
    	int failures = 0;
    	
    	try {
			test.useUtil();
    	} catch (Exception x) {
    		System.out.println("test.useUtil() failed: " + x);
    		x.printStackTrace();
			failures++;
    	}
    	
    	System.out.println();
    	try {
			test.dumpResource("/main-manifest.mf");
    	} catch (Exception x) {
    		System.out.println("test.useResource() failed: " + x);
    		x.printStackTrace();
    	}
		System.out.println();
		try {
			// Dump a resource relative to this jar file.
			test.dumpResource("/duplicate.txt");
		} catch (Exception x) {
			System.out.println("test.useResource() failed: " + x);
			x.printStackTrace();
			failures++;
		}
		System.out.println();
		
		try {
			// Dump a resource relative to this class file.
			test.dumpResource("main.txt");
		} catch (Exception x) {
			System.out.println("test.useResource() failed: " + x);
			x.printStackTrace();
			failures++;
		}
		System.out.println();

		test.loadCodeSource();
		System.out.println();

		try {
			test.classLoader();
		} catch (Exception x) {
			System.out.println("Test.classLoader() failed: " + x);
			failures++;
		}
		System.out.println();
		
		long end = new Date().getTime();
		
		try {
			test.classURL();
		} catch (Exception x) {
			System.out.println("Test.classURL() failed: " + x);
			x.printStackTrace();
			failures++;
		}
		System.out.println();
		
		failures += test.failures;
		
		System.out.println("Main: finished in " + (end - start) + " ms");
		String f = "failure" + (failures==0 || failures>2? "s": "");
		System.out.println(failures + " " + f + " (TODO: JUnit!)");
		
    }
}

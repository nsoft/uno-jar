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
    
    // Do a test which requires an external jar on the boot classpath?
    public static boolean TEST_EXTERNAL = false;

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
			test.testUseUtil();
    	} catch (Exception x) {
    		System.out.println("test.testUseUtil() failed: " + x);
    		x.printStackTrace();
			failures++;
    	}
    	
    	System.out.println();
    	try {
			test.testDumpResource("/main-manifest.mf");
    	} catch (Exception x) {
    		System.out.println("test.testDumpResource() failed: " + x);
    		x.printStackTrace();
    	}
		System.out.println();
		try {
			// Dump a resource relative to this jar file.
			test.testDumpResource("/duplicate.txt");
		} catch (Exception x) {
			System.out.println("test.testDumpResource() failed: " + x);
			x.printStackTrace();
			failures++;
		}
		System.out.println();
		
		try {
			// Dump a resource relative to this class file.
			test.testDumpResource("main.txt");
		} catch (Exception x) {
			System.out.println("test.testDumpResource() failed: " + x);
			x.printStackTrace();
			failures++;
		}
		System.out.println();

		test.testLoadCodeSource();
		System.out.println();

		try {
			test.testClassLoader();
            failures++;
		} catch (Exception x) {
			System.out.println("Test.testClassLoader() failed (as expected!): " + x);
		}
		System.out.println();
		
		
		try {
			test.testClassURL();
		} catch (Exception x) {
			System.out.println("Test.testClassURL() failed: " + x);
			x.printStackTrace();
			failures++;
		}
		System.out.println();

		try {
			test.testResourceURL();
		} catch (Exception x) {
			System.out.println("Test.testResourceURL() failed: " + x);
			x.printStackTrace();
			failures++;
		}
		System.out.println();

		try {
			test.testResourceRelativeURL();
		} catch (Exception x) {
			System.out.println("Test.testResourceRelativeURL() failed: " + x);
			x.printStackTrace();
			failures++;
		}
		System.out.println();

		try {
			test.testImageIcon();
		} catch (Exception x) {
			System.out.println("Test.testImageIcon() failed: " + x);
			x.printStackTrace();
			failures++;
		}
		System.out.println();

        try {
            test.testServices();
        } catch (Exception x) {
            System.out.println("Test.testServices() failed: " + x);
            x.printStackTrace();
            failures++;
        }
        System.out.println();

		if (TEST_EXTERNAL) {
            try {
                test.testExternal();
            } catch (Exception x) {
                System.out.println("Test.testExternal() failed: " + x);
                x.printStackTrace();
                failures++;
            }
            System.out.println();
        }

        failures += test.failures;

        long end = new Date().getTime();
		
		System.out.println("Main: finished in " + (end - start) + " ms");
		String f = "failure" + (failures==0 || failures>2? "s": "");
		System.out.println(failures + " " + f);
		
    }
}

/*
 * Copyright (c) 2004, P. Simon Tuffs (simon@simontuffs.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of P. Simon Tuffs nor the names of any contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
		
		System.out.println("Main: finished in " + (end - start) + " ms");
		String f = "failure" + (failures==0 || failures>2? "s": "");
		System.out.println(failures + " " + f + " (TODO: JUnit!)");
		
    }
}

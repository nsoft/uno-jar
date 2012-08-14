/*
 * Copyright (c) 2004-2010, P. Simon Tuffs (simon@simontuffs.com)
 * All rights reserved.
 *
 * See the full license at http://one-jar.sourceforge.net/one-jar-license.html
 * This license is also included in the distributions of this software
 * under doc/one-jar-license.txt
 */

package com.simontuffs.onejar;

import java.security.ProtectionDomain;


/**
 * @author simon
 *
 */
public class DetectClassLoader extends JarClassLoader {
  
    private static final Logger LOGGER = Logger.getLogger("DetectClassLoader");
        

	public DetectClassLoader(String $wrap) {
		super($wrap);
	}

	public DetectClassLoader(ClassLoader parent) {
		super(parent);
	}

	/**
	 * @see com.simontuffs.onejar.JarClassLoader#defineClass(java.lang.String, byte[], java.security.ProtectionDomain)
	 */
	protected Class defineClass(String name, byte[] bytes, ProtectionDomain pd)
		throws ClassFormatError {
	    LOGGER.info("DetectClassLoader.defineClass("+name+")");
		// Use the superclass to define the class, then check and see
		// whether it is a classloader.  Too late to do anything but issue
		// a warning, but better a warning than failed class-loads with no
		// warning.
		Class cls = super.defineClass(name, bytes, pd);
		
		if (ClassLoader.class.isAssignableFrom(cls)) {
			// If the classloader defines loadClass, problems ahead?
			try {
			        if (cls.getMethod("loadClass", new Class[]{String.class}) != null) {    	
				    LOGGER.warning(name + " is a ClassLoader");
				    LOGGER.warning("loaded from codesource " + pd.getCodeSource());
				    LOGGER.warning("and declared 'loadClass(String)'. It may not be able to load classes without being modified.");
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException ok) {
			}
			try {
				if (cls.getMethod("loadClass", new Class[]{String.class, Boolean.TYPE}) != null) {    	
				    LOGGER.warning(name + " is a ClassLoader");
				    LOGGER.warning("and declared 'loadClass(String, boolean)'. It may not be able to load classes without being modified.");
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException ok) {
			}
		}
		return cls;
	}


}

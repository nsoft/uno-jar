/*
 * Created on Jun 9, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.simontuffs.onejar;

import java.security.ProtectionDomain;

/**
 * @author Owner
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DetectClassLoader extends JarClassLoader {

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
		// Use the superclass to define the class, then check and see
		// whether it is a classloader.  Too late to do anything but issue
		// a warning, but better a warning than failed class-loads with no
		// warning.
		Class cls = super.defineClass(name, bytes, pd);
		
		if (ClassLoader.class.isAssignableFrom(cls)) {
			// If the classloader defines loadClass, problems ahead?
			try {
				if (cls.getMethod("loadClass", new Class[]{String.class}) != null) {    	
					WARNING(name + " is a ClassLoader");
					WARNING("and declared 'loadClass(String)'. It may not be able to load classes without being modified.");
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException ok) {
			}
			try {
				if (cls.getMethod("loadClass", new Class[]{String.class, Boolean.TYPE}) != null) {    	
					WARNING(name + " is a ClassLoader");
					WARNING("and declared 'loadClass(String, boolean)'. It may not be able to load classes without being modified.");
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException ok) {
			}
		}
		return cls;
	}

	protected String PREFIX() {
		return "DetectClassLoader: ";
	}

}

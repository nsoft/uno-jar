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


package com.simontuffs.onejar;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

/**
 * Loads classes from pre-defined locations inside the jar file containing this
 * class.  Classes will be loaded from jar files contained in the following 
 * locations within the main jar file (on the classpath of the application 
 * actually, which when running with the "java -jar" command works out to be
 * the same thing).
 * <ul>
 * <li>
 *   /lib	Used to contain library jars.
 * </li>
 * <li>
 *   /main	Used to contain a default main jar.
 * </li>
 * </ul> 
 * @author simon@simontuffs.com (<a href="http://www.simontuffs.com">http://www.simontuffs.com</a>)
 */
public class JarClassLoader extends ClassLoader {
	
	public final static String JAVA_CLASS_PATH = "java.class.path";
	public final static String LIB_PREFIX = "lib/";
	public final static String MAIN_PREFIX = "main/";
	public final static String RECORDING = "recording";
	public final static String TMP = "tmp";
	public final static String UNPACK = "unpack";

	protected String PREFIX() {
		return "JarClassLoader: ";
	}

	protected void VERBOSE(String message) {
		if (verbose) System.out.println(PREFIX() + message);
	}

	protected void WARNING(String message) {
		System.err.println(PREFIX() + "Warning: " + message); 
	}
	
	protected void INFO(String message) {
		if (info) System.out.println(PREFIX() + "Info: " + message);
	}


	protected Map byteCode = new HashMap();
	protected Map pdCache = Collections.synchronizedMap(new HashMap());
	
	protected boolean record = false, flatten = false;
	protected boolean verbose = false, info = false;
	protected String recording = RECORDING;
	
	protected String jarName, mainJar, wrap;
	
	protected class ByteCode {
		public ByteCode(String $name, String $original, byte $bytes[], String $codebase) {
			name = $name;
			original = $original;
			bytes = $bytes;
			codebase = $codebase;
		}
		public byte bytes[];
		public String name, original, codebase;
	}
	
	
	/**
	 * Create a non-delegating but jar-capable classloader for bootstrap
	 * purposes.
	 * @param $wrap  The directory in the archive from which to load a 
	 * wrapping classloader.
	 */
	public JarClassLoader(String $wrap) {
		wrap = $wrap;
		// System.out.println(PREFIX() + this + " boot=" + boot + " loaded by " + this.getClass().getClassLoader());
	}
	
	/**
	 * The main constructor for the Jar-capable classloader.
	 * @param $record	If true, the JarClassLoader will record all used classes
	 * 					into a recording directory (called 'recording' by default)
	 *				 	The name of each jar file will be used as a directory name
	 *					for the recorded classes.
	 * @param $flatten  Whether to flatten out the recorded classes (i.e. eliminate
	 * 					the jar-file name from the recordings).
	 * 
	 * Example: Given the following layout of the one-jar.jar file
	 * <pre>
	 *    /
	 *    /META-INF
	 *    | MANIFEST.MF
	 *    /com
	 *      /simontuffs
	 *        /onejar
	 *          Boot.class
	 *          JarClassLoader.class
	 *    /main
	 *        main.jar
	 *        /com
	 *          /main
	 *            Main.class 
	 *    /lib
	 *        util.jar
	 *          /com
	 *            /util
	 *              Util.clas
	 * </pre>
	 * The recording directory will look like this:
	 * <ul>
	 * <li>flatten=false</li>
	 * <pre>
	 *   /recording
	 *     /main.jar
	 *       /com
	 *         /main
	 *            Main.class
	 *     /util.jar
	 *       /com
	 *         /util
	 *            Util.class
	 * </pre>
	 *
	 * <li>flatten = true</li>
	 * <pre>
	 *   /recording
	 *     /com
	 *       /main
	 *          Main.class
	 *       /util
	 *          Util.class
	 *   
	 * </ul>
	 * Flatten mode is intended for when you want to create a super-jar which can
	 * be launched directly without using one-jar's launcher.  Run your application
	 * under all possible scenarios to collect the actual classes which are loaded,
	 * then jar them all up, and point to the main class with a "Main-Class" entry
	 * in the manifest.  
	 *       
	 */
	public JarClassLoader(ClassLoader parent) {
		super(parent);
		// System.out.println(PREFIX() + this + " parent=" + parent + " loaded by " + this.getClass().getClassLoader());
	}
	
	public String load(String mainClass) {
		if (record) {
			new File(recording).mkdirs();
		}
		try {
			// Hack to get the lib directory entries.   We know we are being
			// loaded out of a jar file, so there is only one jar-file on the
			// classpath: ours!  So we open it.
			jarName = System.getProperty(JAVA_CLASS_PATH);
			JarFile jarFile = new JarFile(jarName);
			Enumeration enum = jarFile.entries();
			while (enum.hasMoreElements()) {
				JarEntry entry = (JarEntry)enum.nextElement();
				if (entry.isDirectory()) continue;
				String jar = entry.getName();
				if (wrap != null && jar.startsWith(wrap) || jar.startsWith(LIB_PREFIX) || jar.startsWith(MAIN_PREFIX)) {
					if (wrap != null && !entry.getName().startsWith(wrap)) continue;
					// Load it! 
					INFO("caching " + jar);
					InputStream is = this.getClass().getResourceAsStream("/" + jar);
					if (is == null) throw new IOException(jar);
					loadByteCode(is, jar);
					
					// Do we need to look for a main class?
					if (jar.startsWith(MAIN_PREFIX)) {
						if (mainClass == null) {
							is = this.getClass().getResourceAsStream("/" + jar);
							JarInputStream jis = new JarInputStream(is);
							mainClass = jis.getManifest().getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
							mainJar = jar;
						} else if (mainJar != null) {
							WARNING("mainClass is defined in multiple jar files inside " + MAIN_PREFIX + mainJar + " and " + jar);
						}
					} 
				} else if (wrap == null && entry.getName().startsWith(UNPACK)) {
					// Unpack into a temporary directory which is on the classpath of
					// the application classloader.  Badly designed code which relies on the
					// application classloader can be made to work in this way.
					InputStream is = this.getClass().getResourceAsStream("/" + jar);
					if (is == null) throw new IOException(jar);
					// Make a sentinel.
					File dir = new File(TMP);
					File sentinel = new File(dir, jar.replace('/', '.'));
					if (!sentinel.exists()) {
						INFO("unpacking " + jar + " into " + dir.getCanonicalPath());
						loadByteCode(is, jar, TMP);
						sentinel.getParentFile().mkdirs();
						sentinel.createNewFile();
					}
				}
			}
		
		} catch (IOException iox) {
			System.err.println("Unable to load resource: " + iox);
			iox.printStackTrace(System.err);
		}
		return mainClass;
	}
	
	protected void loadByteCode(InputStream is, String jar) throws IOException {
		loadByteCode(is, jar, null);
	}
	
	protected void loadByteCode(InputStream is, String jar, String tmp) throws IOException {
		JarInputStream jis = new JarInputStream(new BufferedInputStream(is));
		JarEntry entry = null;
		// TODO: implement lazy loading of bytecode.
		while ((entry = jis.getNextJarEntry()) != null) {
			if (entry.isDirectory()) continue;
			
			String entryName = entry.getName().replace('/', '.');
			int index = entryName.lastIndexOf('.');
			String name = entryName.substring(0, index);
			String type = entryName.substring(index+1);
			
			// Because we are doing stream processing, we don't know what
			// the size of the entries is.  So we store them dynamically.
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			while (true) {
				int len = jis.read(buf);
				if (len < 0) break;
				baos.write(buf, 0, len);
			}

			if (tmp != null) {
				// Unpack into a temporary working directory which is on the classpath.
				File file = new File(tmp, entry.getName());
				file.getParentFile().mkdirs();
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(baos.toByteArray());
				fos.close();
				
			} else {
				// If entry is a class, check to see that it hasn't been defined
				// already.  Class names must be unique within a classloader because
				// they are cached inside the VM until the classloader is released.
				byte[] bytes = baos.toByteArray();
				if (type.equals("class")) {
					if (alreadyCached(name, jar, bytes)) continue;
					byteCode.put(name, new ByteCode(name, entry.getName(), bytes, jar));
					VERBOSE("Cached bytes for " + name);
				} else {
					// Another kind of resource.  Cache this by name, and also prefixed
					// by the jar name.  Don't duplicate the bytes.  This allows us
					// to map resource lookups to either jar-local, or globally defined.
					name = "/" + entry.getName();
					String localname = "/" + jar + "/" + entry.getName();
					byteCode.put(localname, new ByteCode(localname, entry.getName(), bytes, jar));
					VERBOSE("Cached bytes for " + localname);
					if (alreadyCached(name, jar, bytes)) continue;
					byteCode.put(name, new ByteCode(name, entry.getName(), bytes, jar));
					VERBOSE("Cached bytes for " + name);
					
				}
			}
		}
	}
	
	protected boolean classPool = false;

	/**
	 * Locate the named class in a jar-file, contained inside the
	 * jar file which was used to load <u>this</u> class.
	 */
    protected Class findClass(String name) throws ClassNotFoundException {
    	// Look up the class in the byte codes.
    	// Translate path?
    	VERBOSE("findClass(" + name + ")");
		name = name.replace('/', '.');
    	ByteCode bytecode = (ByteCode)byteCode.get(name);
    	if (bytecode != null) {
    		VERBOSE("found " + name + " in " + bytecode.codebase);
    		if (record) {
    			record(bytecode);
    		}
    		// Use a protectionDomain to associate the codebase with the
    		// class.
    		ProtectionDomain pd = (ProtectionDomain)pdCache.get(bytecode.codebase);
    		if (pd == null) {
    			ProtectionDomain cd = JarClassLoader.class.getProtectionDomain();
	    		URL url = cd.getCodeSource().getLocation();
	    		try {
					url = new URL("jar:" + url + "!/" + bytecode.codebase);
	    		} catch (MalformedURLException mux) {
					mux.printStackTrace(System.out);    			
	    		}
	    		
	    		CodeSource source = new CodeSource(url, null);
	    		pd = new ProtectionDomain(source, null, this, null);
	    		pdCache.put(bytecode.codebase, pd);
			}
			
			// Do it the simple way.
			byte bytes[] = bytecode.bytes;
			return defineClass(name, bytes, pd);
    	}
    	VERBOSE(name + " not found");
        throw new ClassNotFoundException(name);
        
    }

	protected Class defineClass(String name, byte[] bytes, ProtectionDomain pd) throws ClassFormatError {
		// Simple, non wrapped class definition.
		return defineClass(name, bytes, 0, bytes.length, pd);
	}

	protected void record(ByteCode bytecode) {
		String fileName = bytecode.original;
		// Write out into the record directory.
		File dir = new File(recording, flatten? "": bytecode.codebase);
		File file = new File(dir, fileName);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			VERBOSE("" + file);
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(bytecode.bytes);
				fos.close();
						
			} catch (IOException iox) {
				System.err.println(PREFIX() + "unable to record " + file + ": " + iox);
			}
					
		}
	}
   
	/**
	 * Overriden to return resources from the appropriate codebase.
	 */
	public InputStream getResourceAsStream(String resource) {

		// Special case for .class files.
		if (resource.endsWith(".class")) {
			int index = resource.lastIndexOf('.');
			resource = resource.substring(0, index).replace('/', '.');
			ByteCode bytecode = (ByteCode)byteCode.get(resource);
			if (bytecode == null) {
				VERBOSE("could not locate: " + resource);
				return null;
			} 
			return new ByteArrayInputStream(bytecode.bytes);
		}
		
		// If we are the bootstrap classloader, simply load the resource.
		if (wrap != null) {
			VERBOSE("getResourceAsStream() boot mode resource " + resource);
			// Do we have it in our cache?
			ByteCode bytecode = null;
			bytecode = (ByteCode)byteCode.get(resource);
			byte bytes[] = null;
			if (bytecode != null) {
				bytes = bytecode.bytes; 
			} else {
				bytecode = (ByteCode)byteCode.get(resource);
				if (bytecode != null) {
					bytes = bytecode.bytes;
				} else {
					bytecode = (ByteCode)byteCode.get("/" + resource);
					if (bytecode != null) bytes = bytecode.bytes;
				}
			}
			if (bytes != null) {
				VERBOSE("returned " + resource + " from cache");
				return new ByteArrayInputStream(bytes);
			}
			// Look one last time.
			InputStream is = super.getResourceAsStream(resource);
			VERBOSE("returning " + is);
			return is;
		}
		
		// Try to load resources relative to the jar file containing the
		// calling class.  The calling class should always be three or more levels
		// above us.  For example, as reported by getStackTrace():
		// com.simontuffs.onejar.example.main.Test.useResource(Test.java:56)
		StackTraceElement[] stack = new Throwable().getStackTrace();
		// Search upward until we get to a known class.
		String caller = null;
		for (int i=0; i<stack.length; i++) {
			if (byteCode.get(stack[i].getClassName()) != null) {
				caller = stack[i].getClassName();
				break;
			}
		}
		
		VERBOSE("getResourceAsStream(" + resource + ") called by " + caller);
		
		// Default is global lookup.
		String location = "/" + resource;
		ByteCode bytecode = (ByteCode)byteCode.get(location);
		// Look up the codebase for the caller.
		ByteCode callerCode = ((ByteCode)byteCode.get(caller));
		if (callerCode != null) {
			String jar = callerCode.codebase;	
			String local = "/" + jar + "/" + resource;
			ByteCode bc = (ByteCode)byteCode.get(local);
			if (bc != null) bytecode = bc;
		}
		if (bytecode != null) {
			VERBOSE("resource " + resource + " loaded from " + location);
			if (record) record(bytecode);
			return new ByteArrayInputStream(bytecode.bytes);
		}
		VERBOSE("not found, return null");
		// Not found?  What do we return?
		return null;
	}
	
	protected boolean alreadyCached(String name, String jar, byte[] bytes) {
		// TODO: check resource map to see how we will map requests for this
		// resource from this jar file.  Only a conflict if we are using a
		// global map and the resource is defined by more than
		// one jar file (default is to map to local jar).
		ByteCode existing = (ByteCode)byteCode.get(name);
		if (existing != null) {
			// If bytecodes are identical, no real problem.  Likewise if it's in
			// META-INF.
			if (!Arrays.equals(existing.bytes, bytes) && !name.startsWith("/META-INF")) {
				WARNING(existing.name + " in " + jar + " is hidden by " + existing.codebase + " (with different bytecode)");
			} else {
				VERBOSE(existing.name + " in " + jar + " is hidden by " + existing.codebase + " (with same bytecode)");
			}
			return true;
		}
		return false;
	}

    /**
     * Sets the name of the used  classes recording directory.
     * 
     * @param $recording A value of "" will use the current working directory 
     * (not recommended).  A value of 'null' will use the default directory, which
     * is called 'recording' under the launch directory (recommended).
     */
    public void setRecording(String $recording) {
    	recording = $recording;
    	if (recording == null) recording = RECORDING;
    }
    
    public String getRecording() {
    	return recording;
    }
    
    public void setRecord(boolean $record) {
    	record = $record;
    }
    public boolean getRecord() {
    	return record;
    }
    
    public void setFlatten(boolean $flatten) {
    	flatten = $flatten;
    }
    public boolean isFlatten() {
    	return flatten;
    }
    
	public void setVerbose(boolean $verbose) {
		verbose = $verbose;
	}
	
	public boolean getVerbose() {
		return verbose;
	}
	
	public void setInfo(boolean $info) {
		info = $info;
	}
	public boolean getInfo() {
		return info;
	}

    /* (non-Javadoc)
     * @see java.lang.ClassLoader#findResource(java.lang.String)
     */
    protected URL findResource(String $resource) {
    	// TODO: Either need to automatically unpack into a temporary
    	// directory so that a URL will be valid, or a better solution
    	// would be to improve the 'jar:' protocol so that it can handle
    	// nested jar files.  Use the q&d solution for now.
    	String resource = $resource;
    	if (!resource.startsWith("/")) resource = "/" + resource;
		ByteCode bytecode = (ByteCode)byteCode.get(resource);
		if (bytecode != null) {
			File dir = new File(TMP);
			File sentinel = new File(dir, resource.replace('/', '.'));
			if (!sentinel.exists()) {
				try {
					INFO("unpacking " + $resource + " into " + dir.getCanonicalPath());
					File file = new File(dir, $resource);
					file.getParentFile().mkdirs();
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(bytecode.bytes);
					fos.close();
	
					sentinel.getParentFile().mkdirs();
					sentinel.createNewFile();
				} catch (IOException iox) {
					iox.printStackTrace();
				}
			}
			if (record) record(bytecode);
		}
    	// Now that we are unpacked, we can use the app class loader to 
    	// load the resouce, since it has TMP on its classpath by virtue
    	// of the jar manifest.
        return super.findResource($resource);
    }

}

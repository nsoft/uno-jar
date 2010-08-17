/*
 * Copyright (c) 2004-2010, P. Simon Tuffs (simon@simontuffs.com)
 * All rights reserved.
 * 
 * See the full license at http://one-jar.sourceforge.net/one-jar-license.html
 * This license is also included in the distributions of this software
 * under doc/one-jar-license.txt
 * 
 * Contributor: sebastian : http://code.google.com/u/@WBZRRlBYBxZHXQl9/ 
 *   Original creator of the OneJarFile/OneJarUrlConnecion solution to resource location
 *   using jar protocols.
 *                   
 */

package com.simontuffs.onejar;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarFile;
public class OneJarURLConnection extends JarURLConnection {

	private JarFile jarFile;

	public OneJarURLConnection(URL url) throws MalformedURLException {
		super(url);
	}

	public JarFile getJarFile() throws IOException {
		return jarFile;
	}

	public void connect() throws IOException {
		String jarWithContent = getEntryName();
		int separator = jarWithContent.indexOf("!/");
		// TODO: generalize to allow codebase to be a URL.  This may require a complete
		// rewrite of OneJarFile since JarFile can only handle Files.
		String codebase = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		// Handle the case where a URL points to the top-level jar file, i.e. no '!/' separator.
		if (separator >= 0) {
	        String jarFilename = jarWithContent, filename = null;
		    jarFilename = jarWithContent.substring(0, separator++);
		    filename = jarWithContent.substring(++separator);
            jarFile = new OneJarFile(codebase, jarFilename, filename);
		} else {
		    // Entry in the top-level One-JAR.
	        jarFile = new JarFile(codebase);
		}
	}

	public InputStream getInputStream() throws IOException {
		return jarFile.getInputStream(jarFile.getJarEntry(getEntryName()));
	}
	
	public int getContentLength() {
	    // Return the size of the jarfile.
	    return jarFile.size();
	}

}

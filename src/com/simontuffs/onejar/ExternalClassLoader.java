/*
 * Copyright (c) 2004-2007, P. Simon Tuffs (simon@simontuffs.com)
 * All rights reserved.
 *
 * See the full license at http://www.simontuffs.com/one-jar/one-jar-license.html
 * This license is also included in the distributions of this software
 * under doc/one-jar-license.txt
 */
package com.simontuffs.onejar;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

/**
 * @author simon
 * A wrapping class-loader that knows how to process external classpath 
 * entries.  These entries are passed into it using a system property
 * just like the -cp entries for the JVM (except that the separator is
 * chosen to be ',' to be platform independent and unlikely to occur in
 * filenames, and non-interfering with common shells (DOS, bash).
 * The entries are assumed to be files if they don't contain a scheme
 * prefix: for example a.jar is a file.  If a path must contain a ':'
 * as on DOS filesystems 'c:/a.jar', then it must be presented as a file url:
 * 'file:c:/a.jar'.  
 * 
 * Example:
 *   -Done-jar.cp=a.jar,b.jar
 */
public class ExternalClassLoader extends JarClassLoader {
    
    public static final String CP = Boot.PROPERTY_PREFIX + "cp";

    public ExternalClassLoader(ClassLoader _parent) throws MalformedURLException {
        // Note this hack needed to wrap the _parent with a URL classloader.
        super(delegate(_parent));
    }
    
    /**
     * Examine the one-jar.cp property and build a URL classloader based on its
     * contents.  This method is static so that it can be called as part of the
     * constructor invoking super(ClassLoader parent).
     * @param cl
     * @return
     * @throws MalformedURLException
     */
    protected static ClassLoader delegate(ClassLoader cl) throws MalformedURLException {
        String cp = System.getProperty(CP);
        ArrayList urls = new ArrayList(); 
        if (cp != null) {
            String paths[] = cp.split(","); 
            for (int i=0; i<paths.length; i++) {
                String path = paths[i];
                URL url = null;
                try {
                    url = new URL(path);
                } catch (MalformedURLException mux) {
                    // Is it a file?
                    url = new URL("file:" + path);
                }
                urls.add(url);
            }
        }
        if (urls.size() > 0) {
            return new URLClassLoader((URL[])urls.toArray(new URL[0]), cl);
        } else {
            return cl;
        }
    }
    
    
}

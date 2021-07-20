/*
 * Copyright (c) 2004-2010, P. Simon Tuffs (simon@simontuffs.com)
 * Copyright (c) 2019=2020, Needham Software LLC
 * All rights reserved.
 *
 * See the full license at https://github.com/nsoft/uno-jar/blob/master/LICENSE.txt
 * See addition code licenses at: https://github.com/nsoft/uno-jar/blob/master/NOTICE.txt
 */

/*
 *Many thanks to the following for their contributions to One-Jar:
 * Contributor: sebastian : http://code.google.com/u/@WBZRRlBYBxZHXQl9/
 *   Original creator of the OneJarFile/OneJarUrlConnecion solution to resource location
 *   using jar protocols.
 *
 */

package com.needhamsoftware.unojar;

import java.io.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

public class UnoJarFile extends JarFile {

  protected final String jarFilename;
  protected final JarEntry wrappedJarFile;

  public UnoJarFile(String myJarPath, String jarFilename) throws IOException {
    super(myJarPath);
    this.jarFilename = jarFilename;
    wrappedJarFile = super.getJarEntry(this.jarFilename);
  }

  public JarEntry getJarEntry(String name) {
    String filename = name.substring(name.indexOf("!/") + 2);
    if (filename.equals(MANIFEST_NAME)) {
      // Synthesize a JarEntry.
      return new JarEntry(filename) {
      };
    }
    try {
      try (JarInputStream is = new JarInputStream(super.getInputStream(wrappedJarFile))) {
        JarEntry entry;
        while ((entry = is.getNextJarEntry()) != null) {
          if (entry.getName().equals(filename)) {
            return entry;
          }
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException("Undefined Error", e);
    }
    return null;
    // throw new RuntimeException("Entry not found : " + name);
  }

  public Enumeration<JarEntry> entries() {
    try {
      final JarInputStream is = new JarInputStream(super.getInputStream(wrappedJarFile));
      return new Enumeration<>() {

        protected JarEntry next;

        public JarEntry nextElement() {
          if (next != null) {
            JarEntry tmp = next;
            next = null;
            return tmp;
          }

          try {
            return is.getNextJarEntry();
          } catch (IOException e) {
            throw new RuntimeException("Undefined Error", e);
          }
        }

        public boolean hasMoreElements() {
          if (next != null) {
            return true;
          }
          try {
            next = is.getNextJarEntry();
            if (next == null) {
              is.close();
            }
          } catch (IOException e) {
            throw new RuntimeException("Undefined Error", e);
          }
          return next != null;
        }
      };
    } catch (IOException e) {
      throw new RuntimeException("Undefined Error", e);
    }
  }

  public synchronized InputStream getInputStream(ZipEntry ze) {
    if (ze == null)
      return null;
    String filename = ze.getName();
    try {
      try (JarInputStream is = new JarInputStream(super.getInputStream(wrappedJarFile))) {
        if (filename.equals(MANIFEST_NAME)) {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          is.getManifest().write(baos);
          return new ByteArrayInputStream(baos.toByteArray());
        }
        JarEntry entry;
        while ((entry = is.getNextJarEntry()) != null) {
          if (entry.getName().equals(ze.getName())) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            copy(is, baos);
            return new ByteArrayInputStream(baos.toByteArray());
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("Undefined Error", e);
    }

    throw new RuntimeException("Entry not found : " + ze.getName());
  }

  protected void copy(InputStream in, OutputStream out) throws IOException {
    byte[] buf = new byte[1024];
    while (true) {
      int len = in.read(buf);
      if (len < 0)
        break;
      out.write(buf, 0, len);
    }
  }

}

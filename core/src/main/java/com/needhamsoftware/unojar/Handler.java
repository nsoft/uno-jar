/*
 * Copyright (c) 2004-2010, P. Simon Tuffs (simon@simontuffs.com)
 * Copyright (c) 2019=2020, Needham Software LLC
 * All rights reserved.
 *
 * See the full license at https://github.com/nsoft/uno-jar/blob/master/LICENSE.txt
 * See addition code licenses at: https://github.com/nsoft/uno-jar/blob/master/NOTICE.txt
 */

package com.needhamsoftware.unojar;

import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * @author simon@simontuffs.com
 */
public class Handler extends URLStreamHandler {

  /**
   * This protocol name must match the name of the package in which this class
   * lives.
   */
  public static String PROTOCOL = "onejar";

  /**
   * @see URLStreamHandler#openConnection(URL)
   */
  protected URLConnection openConnection(final URL u) {
    final String resource = u.getPath();
    return new URLConnection(u) {
      public void connect() {
      }

      public String getContentType() {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentType = fileNameMap.getContentTypeFor(resource);
        if (contentType == null)
          contentType = "text/plain";
        return contentType;
      }

      public InputStream getInputStream() throws IOException {
        // Use the Boot classloader to get the resource.  There
        // is only one per one-jar.
        // TODO: this is unacceptable coupling with Boot.  The classloader
        // needs to be injected somehow.
        JarClassLoader cl = Boot.getClassLoader();
        InputStream is = cl.getByteStream(resource);
        // sun.awt image loading does not like null input streams returned here.
        // Throw IOException (probably better anyway).
        if (is == null)
          throw new IOException("cl.getByteStream() returned null for " + resource);
        return is;
      }
    };
  }

}

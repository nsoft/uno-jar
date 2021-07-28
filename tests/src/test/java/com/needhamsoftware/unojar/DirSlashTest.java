package com.needhamsoftware.unojar;

import java.io.IOException;
import java.net.URL;
import java.util.jar.JarEntry;

import static org.junit.Assert.assertNotNull;

public class DirSlashTest {
  public void test() throws IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    URL resource = classLoader.getResource("dirTestSlashes");
    assert resource != null;
    UnoJarURLConnection urlConnection = (UnoJarURLConnection) resource.openConnection();
    JarEntry jarEntry = urlConnection.getJarEntry();
    assertNotNull(resource);
    assertNotNull(jarEntry);
    resource = classLoader.getResource("dirTestSlashes/");
    assertNotNull(resource);
    assert resource != null;
    urlConnection = (UnoJarURLConnection) resource.openConnection();
    jarEntry = urlConnection.getJarEntry();
    assertNotNull(resource);
    assertNotNull(jarEntry);
  }
}

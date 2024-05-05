package com.needhamsoftware.sandbox.relaunch;

import com.needhamsoftware.unojar.Boot;

import java.io.IOException;
import java.net.URL;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class Util {
  @SuppressWarnings("UnnecessaryLocalVariable")
  static String getManifestAttribute(String name) throws IOException {
    String jar = Boot.getMyJarPath();
    JarInputStream jis = new JarInputStream(new URL(jar).openConnection().getInputStream());
    Manifest manifest = jis.getManifest();
//    System.out.println(manifest.getMainAttributes().entrySet());
//    System.out.println(name);
    String value = manifest.getMainAttributes().getValue(name);
//    System.out.println("foo");
//    System.out.println(value);
    return value;
  }
}

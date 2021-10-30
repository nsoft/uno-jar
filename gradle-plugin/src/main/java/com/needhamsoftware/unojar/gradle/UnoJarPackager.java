package com.needhamsoftware.unojar.gradle;

import com.needhamsoftware.unojar.Boot;
import com.needhamsoftware.unojar.JarClassLoader;
import org.apache.commons.io.IOUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.CRC32;

public class UnoJarPackager
    implements Closeable {

  private static final String DEFAULT_BOOT_CLASS_NAME = "com.needhamsoftware.unojar.Boot";

  private final JarOutputStream jarOutputStream;

  public UnoJarPackager(OutputStream outputStream, String mainClassName, Manifest manifest)
      throws IOException {
    super();

    final Manifest adjustedManifest = new Manifest(manifest);
    if (adjustedManifest.getMainAttributes().getValue(Attributes.Name.MANIFEST_VERSION) == null) {
      adjustedManifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
    }
    adjustedManifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, DEFAULT_BOOT_CLASS_NAME);
    adjustedManifest.getMainAttributes().putValue(Boot.ONE_JAR_MAIN_CLASS, mainClassName);
    adjustedManifest.getMainAttributes().putValue("Archive-Type", "uno-jar");

    jarOutputStream = new JarOutputStream(outputStream, adjustedManifest);
  }

  public void addBootJar(File jarFile)
      throws IOException {
    try (final JarInputStream jarInputStream = new JarInputStream(new FileInputStream(jarFile))) {
      while (true) {
        final JarEntry jarEntry = jarInputStream.getNextJarEntry();
        if (jarEntry == null) {
          break;
        }
        if (jarEntry.getName().equalsIgnoreCase("META-INF/MANIFEST.MF")) {
          continue;
        }

        final JarEntry newJarEntry = new JarEntry(jarEntry.getName());
        newJarEntry.setTime(jarEntry.getTime());
        jarOutputStream.putNextEntry(newJarEntry);

        if (!jarEntry.isDirectory()) {
          IOUtils.copy(jarInputStream, jarOutputStream);
        }

        jarOutputStream.closeEntry();
      }
    }
  }

  public void addMainJar(File jarFile)
      throws IOException {
    addMainJar(jarFile, jarFile.getName());
  }

  public void addMainJar(File jarFile, String entryName)
      throws IOException {
    addJar(JarClassLoader.MAIN_PREFIX, jarFile, entryName);
  }

  public void addLibJar(File jarFile)
      throws IOException {
    addLibJar(jarFile, jarFile.getName());
  }

  public void addLibJar(File jarFile, String entryName)
      throws IOException {
    addJar(JarClassLoader.LIB, jarFile, entryName);
  }

  private void addJar(String prefix, File jarFile, String entryName)
      throws IOException {
    final JarEntry jarEntry = new JarEntry(String.format("%s%s", prefix, entryName));
    jarEntry.setTime(jarFile.lastModified());
    jarEntry.setMethod(JarEntry.STORED);
    jarEntry.setSize(jarFile.length());
    jarEntry.setCrc(getCRC32(jarFile));
    jarOutputStream.putNextEntry(jarEntry);
    try (final InputStream inputStream = new FileInputStream(jarFile)) {
      IOUtils.copy(inputStream, jarOutputStream);
    }
    jarOutputStream.closeEntry();
  }

  private long getCRC32(File file)
      throws IOException {
    try (final InputStream inputStream = new FileInputStream(file)) {
      return getCRC32(inputStream);
    }
  }

  private long getCRC32(InputStream inputStream)
      throws IOException {
    final byte[] buffer = new byte[4096];
    final CRC32 crc32 = new CRC32();
    while (true) {
      final int len = inputStream.read(buffer);
      if (len < 0) {
        return crc32.getValue();
      }
      crc32.update(buffer, 0, len);
    }
  }

  @Override
  public void close()
      throws IOException {
    jarOutputStream.close();
  }
}

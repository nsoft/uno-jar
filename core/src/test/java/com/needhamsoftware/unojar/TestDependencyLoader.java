package com.needhamsoftware.unojar;

import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

@SuppressWarnings("resource")
public class TestDependencyLoader {

  @Test
  public void testLoadLocalFirst() throws Exception {
    ClassLoader defaultCL = getClass().getClassLoader();
    Class<?> aClass = defaultCL.loadClass("com.needhamsoftware.unojar.ClassToLoad");
    CodeSource codeSource = aClass.getProtectionDomain().getCodeSource();
    URL location = codeSource.getLocation();
    assertEquals(defaultCL, aClass.getClassLoader());

    ClassLoader testCL = new URLClassLoader(new URL[]{location}, null);
    aClass = testCL.loadClass("com.needhamsoftware.unojar.ClassToLoad");
    assertEquals(testCL, aClass.getClassLoader());

    Class<?> urlClass = aClass;
    UnoJarDependencyLoader depLoader = new UnoJarDependencyLoader(new URL[]{location}, defaultCL);
    ClassLoader extLoader = new URLClassLoader(new URL[]{location}, null);
    depLoader.addExtLoader(extLoader);
    aClass = depLoader.loadClass("com.needhamsoftware.unojar.ClassToLoad");
    assertEquals(extLoader, aClass.getClassLoader());

    // Verify we loaded the same named class from different loaders and therefore have
    // two separate classes.
    assertEquals(aClass.getCanonicalName(), urlClass.getCanonicalName());
    assertNotEquals(aClass, urlClass);

    // THIS is the key step. These loaders added as ExtLoaders are consulted first
    // before delegating upward.
    depLoader.addExtLoader(extLoader);

    aClass = depLoader.loadClass("org.junit.Assert");
    assertEquals(defaultCL, aClass.getClassLoader());

    assertEquals(aClass.getCanonicalName(), Assert.class.getCanonicalName());
    assertEquals(aClass, Assert.class);

    try {
     testCL.loadClass("org.junit.Assert");
     fail("When did this start working!!?");
    } catch (ClassNotFoundException e) {
      // success (demonstrates benefit of depLoader!) Without depLoader you
      // can't load classes from the parent.
    }

  }
}

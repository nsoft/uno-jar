/*
 * Created on May 20, 2005
 *
 */
package com.simontuffs.onejar.test;

import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.simontuffs.onejar.Boot;
import com.simontuffs.onejar.JarClassLoader;

/**
 * @author simon
 *
 */
public class SelfTest extends TestCase {

    protected static Object test;
    protected static int failures = 0;
    
    public static void main(String args[]) {
        new TestSuite(SelfTest.class).run(new TestResult());
    }
    
    public SelfTest() throws Exception {
        if (test == null) {
            // Load Test object from the jar.
            JarClassLoader loader = (JarClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return new JarClassLoader(""); 
                }
            });
            Boot.setClassLoader(loader);
            loader.setOneJarPath(Boot.getMyJarPath());
            // loader.setVerbose(true);
            loader.load(null);
            test = loader.loadClass("com.simontuffs.onejar.example.main.Test").newInstance();
        }
    }
    
    // Renaming classes to help with JUnit test reporting.
    public static class Task extends SelfTest {
        public Task() throws Exception {
            super();
        }
    }
    
    public static class Macro extends SelfTest {
        public Macro() throws Exception {
            super();
        }
    }
    
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(SelfTest.class);
        return suite;
    }
    
    public void testUseUtil() throws Exception {
        Invoker.invoke(test, "testUseUtil");
    }
    
    public void testDumpResource() throws Exception {
        Invoker.invoke(test, "testDumpResource", new Class[]{String.class}, new String[]{"/main-manifest.mf"});
    }
    
    public void testDumpDuplicateResource() throws Exception {
        Invoker.invoke(test, "testDumpResource", new Class[]{String.class}, new String[]{"/duplicate.txt"});
    }
    
    public void testDumpRelativeResource() throws Exception {
        Invoker.invoke(test, "testDumpResource", new Class[]{String.class}, new String[]{"main.txt"});
    }

    public void testClassLoader() throws Exception {
        try {
            Invoker.invoke(test, "testClassLoader");
            fail("test.testClassLoader() should throw ClassNotFoundException");
        } catch (InvocationTargetException ix) {
            Throwable t = ix.getTargetException();
            assertTrue("Exception " + t + " is not a ClassNotFoundException (expected)", t instanceof ClassNotFoundException);
            return;
        }
        fail("test.testClassLoader() should throw ClassNotFoundException");
    }
    
    public void testClassURL() throws Exception {
        Invoker.invoke(test, "testClassURL");
    }

    public void testResourceURL() throws Exception {
        Invoker.invoke(test, "testResourceURL");
    }

    public void testResourceRelativeURL() throws Exception {
        Invoker.invoke(test, "testResourceRelativeURL");
    }

    public void testFindResourcesInMainJar() throws Exception {
        Invoker.invoke(test, "testFindResourcesInMainJar");
    }

    public void testFindResourcesInJars() throws Exception {
        Invoker.invoke(test, "testFindResourcesInJars");
    }

    public void testImageIcon() throws Exception {
        Invoker.invoke(test, "testImageIcon");
    }

    public void testLoadCodeSource() throws Exception {
        Invoker.invoke(test, "testLoadCodeSource");
    }
    
    public void testPackageName() throws Exception {
        Invoker.invoke(test, "testPackageName");
    }
    
    public void testLibPackageName() throws Exception {
        Invoker.invoke(test, "testLibPackageName");
    }
    
    public void testGetResourceAsStream() throws Exception {
        Invoker.invoke(test, "testGetResourceAsStream");
    }
    
    public void testGetResource() throws Exception {
        Invoker.invoke(test, "testGetResource");
    }
    
    public void testServices() throws Exception {
        Invoker.invoke(test, "testServices");
    }
    
    public void testExpanded() throws Exception {
        Invoker.invoke(test, "testExpanded");
    }
    
    public void testContentType() throws Exception {
        Invoker.invoke(test, "testContentType");
    }
    
    public void testHtmlAnchor() throws Exception {
        Invoker.invoke(test, "testHtmlAnchor");
    }
    
    public void testExpand() throws Exception {
        Invoker.invoke(test, "testExpand");
    }

    public void testLogging() throws Exception {
        Invoker.invoke(test, "testLogging");
    }
    
    public void testExternal() throws Exception {
        if (System.getProperty("one-jar.test.external") != null) {
            Invoker.invoke(test, "testExternal");
        }
    }
    
    public void testFindAllManifestMfs() throws Exception {
        Invoker.invoke(test, "testFindAllManifestMfs");
    }
    
    public void testRelativeURL() throws Exception {
        Invoker.invoke(test, "testRelativeURL");
    }

    // TODO: add self-test for loading native library.
    // TODO: add self-test for external classpath loader.

}

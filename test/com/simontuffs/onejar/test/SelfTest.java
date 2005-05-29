/*
 * Created on May 20, 2005
 *
 */
package com.simontuffs.onejar.test;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;
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
    
    public SelfTest() throws Exception {
        if (test == null) {
            // Load Test object from the jar.
            JarClassLoader loader = new JarClassLoader("");
            Boot.setClassLoader(loader);
            // loader.setVerbose(true);
            loader.load(null);
            test = loader.loadClass("com.simontuffs.onejar.example.main.Test").newInstance();
        }
    }
    
    protected static void checkFailures() throws Exception {
        int fail = ((Integer)Invoker.get(test, "failures")).intValue();
        if (fail > failures) {
            failures = fail;
            fail("failed: look at stdout log for messages");
       }
    }
    
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(SelfTest.class);
        return suite;
    }
    
    public void testUseUtil() throws Exception {
        Invoker.invoke(test, "useUtil");
        checkFailures();
    }
    
    public void testDumpResource() throws Exception {
        Invoker.invoke(test, "dumpResource", new Class[]{String.class}, new String[]{"/main-manifest.mf"});
        checkFailures();
    }
    
    public void testDumpDuplicateResource() throws Exception {
        Invoker.invoke(test, "dumpResource", new Class[]{String.class}, new String[]{"/duplicate.txt"});
        checkFailures();
    }
    
    public void testDumpRelativeResource() throws Exception {
        Invoker.invoke(test, "dumpResource", new Class[]{String.class}, new String[]{"main.txt"});
        checkFailures();
    }

    public void testClassLoader() throws Exception {
        try {
            Invoker.invoke(test, "classLoader");
            fail("test.classLoader() should throw ClassNotFoundException");
        } catch (InvocationTargetException ix) {
            assertTrue(ix.getTargetException() instanceof ClassNotFoundException);
            return;
        }
        fail("test.classLoader() should throw ClassNotFoundException");
    }
    
    public void testClassURL() throws Exception {
        Invoker.invoke(test, "classURL");
        checkFailures();
    }

    public void testLoadCodeSource() throws Exception {
        Invoker.invoke(test, "loadCodeSource");
        checkFailures();
    }
    
    public void testPackageName() throws Exception {
        Invoker.invoke(test, "testPackageName");
        checkFailures();
    }
    
    // TODO: add self-test for loading native library.
    // TODO: add self-test for external classpath loader.

}

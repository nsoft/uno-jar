/*
 * Created on May 20, 2005
 *
 */
package com.simontuffs.onejar.test;

import java.lang.reflect.InvocationTargetException;

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
        Invoker.invoke(test, "testUseUtil");
        checkFailures();
    }
    
    public void testDumpResource() throws Exception {
        Invoker.invoke(test, "testDumpResource", new Class[]{String.class}, new String[]{"/main-manifest.mf"});
        checkFailures();
    }
    
    public void testDumpDuplicateResource() throws Exception {
        Invoker.invoke(test, "testDumpResource", new Class[]{String.class}, new String[]{"/duplicate.txt"});
        checkFailures();
    }
    
    public void testDumpRelativeResource() throws Exception {
        Invoker.invoke(test, "testDumpResource", new Class[]{String.class}, new String[]{"main.txt"});
        checkFailures();
    }

    public void testClassLoader() throws Exception {
        try {
            Invoker.invoke(test, "testClassLoader");
            fail("test.testClassLoader() should throw ClassNotFoundException");
        } catch (InvocationTargetException ix) {
            assertTrue(ix.getTargetException() instanceof ClassNotFoundException);
            return;
        }
        fail("test.testClassLoader() should throw ClassNotFoundException");
    }
    
    public void testClassURL() throws Exception {
        Invoker.invoke(test, "testClassURL");
        checkFailures();
    }

    public void testResourceURL() throws Exception {
        Invoker.invoke(test, "testResourceURL");
        checkFailures();
    }

    public void testResourceRelativeURL() throws Exception {
        Invoker.invoke(test, "testResourceRelativeURL");
        checkFailures();
    }

    public void testImageIcon() throws Exception {
        Invoker.invoke(test, "testImageIcon");
        checkFailures();
    }

    public void testLoadCodeSource() throws Exception {
        Invoker.invoke(test, "testLoadCodeSource");
        checkFailures();
    }
    
    public void testPackageName() throws Exception {
        Invoker.invoke(test, "testPackageName");
        checkFailures();
    }
    
    public void testGetResourceAsStream() throws Exception {
        Invoker.invoke(test, "testGetResourceAsStream");
        checkFailures();
    }
    
    public void testServices() throws Exception {
        Invoker.invoke(test, "testServices");
        checkFailures();
    }
    
    // TODO: add self-test for loading native library.
    // TODO: add self-test for external classpath loader.

}

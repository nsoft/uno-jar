package com.simontuffs.onejar.test;

import junit.framework.TestResult;
import junit.framework.TestSuite;

public class ExternalSelfTest extends SelfTest {

    public ExternalSelfTest() throws Exception {
        super();
        System.setProperty("one-jar.test.external", "true");
    }

    /**
     * @param args
     */
    public static void main(String args[]) {
        new TestSuite(ExternalSelfTest.class).run(new TestResult());
    }

    public void testExternal() throws Exception {
        Invoker.invoke(test, "testExternal");
    }

    public void testExternalFile() throws Exception {
        Invoker.invoke(test, "testExternalFile");
    }

    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(ExternalSelfTest.class);
        return suite;
    }

}

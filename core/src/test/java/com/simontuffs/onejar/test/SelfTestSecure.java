package com.simontuffs.onejar.test;

import junit.framework.TestResult;
import junit.framework.TestSuite;

public class SelfTestSecure extends SelfTest {

    static {
        System.setProperty("one-jar.test.security", "true");
    }
    
    public SelfTestSecure() throws Exception {
        super();
    }

    /**
     * @param args
     */
    public static void main(String args[]) {
        new TestSuite(SelfTestSecure.class).run(new TestResult());
    }
    
    public static void testSecurityManager() {
        SecurityManager manager = System.getSecurityManager();
        if (manager == null) {
            fail("Security manager is not present");
        }
    }
    
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(SelfTestSecure.class);
        return suite;
    }

}

package com.simontuffs.onejar.example.main;

/**
 * This class is used to assist with JUnit testing.  It lets the one-jar-example be built
 * without any dependency on JUnit. 
 * @author simon
 *
 */
public class Testable {

    public Error cause;
    public int failures;
    public int count;
    
    public void fail(String reason) throws Error {
        cause = new Error(reason);
        System.out.println("******************************************************************************************************************");
        System.out.println("* fail: " + reason);
        System.out.println("******************************************************************************************************************");
        failures++;
    }

}

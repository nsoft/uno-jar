package com.simontuffs.onejar.example.main;

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

package com.simontuffs.onejar.example.external;

/**
 * @author simon
 * A class which is packed into an external Jar to test ability to reference an
 * external Jar file on the classpath.
 */
public class External {
    public void external() {
        System.out.println("External.external() called!");
    }
}

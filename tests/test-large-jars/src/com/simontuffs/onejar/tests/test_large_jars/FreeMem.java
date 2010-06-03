package com.simontuffs.onejar.tests.test_large_jars;

import java.lang.management.ManagementFactory;

public class FreeMem {
    
    public static void main(String args[]) {
        System.out.println("Free memory=" + Runtime.getRuntime().freeMemory()/1024 + "KB");
        System.out.println("Total memory=" + Runtime.getRuntime().totalMemory()/1024 + "KB");
        System.out.println("Max memory=" + Runtime.getRuntime().maxMemory()/1024 + "KB");
    }
}

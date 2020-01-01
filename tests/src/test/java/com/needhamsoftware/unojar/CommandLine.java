package com.needhamsoftware.unojar;

import junit.framework.TestCase;
import org.junit.Test;

public class CommandLine extends TestCase {

    @Test
    public void testUnoJarAntFromGradleSmoke1() throws Exception {
        Invoker.Result result = Invoker.run("java -jar build/testjar1.jar");
        assertEquals("Expected failure did not occur: " + result, 0, result.status);
        assertEquals(result.out.get(0),"System Out Success - main class");
        assertEquals(result.err.get(0),"System Err Success - library class");
    }
    @Test
    public void testUnoJarGradleTaskSmoke2() throws Exception {
        Invoker.Result result = Invoker.run("java -jar build/testJar2.jar");
        assertEquals("Expected failure did not occur: " + result, 0, result.status);
        assertEquals(result.out.get(0),"System Out Success - main class");
        assertEquals(result.err.get(0),"System Err Success - library class");
    }

}

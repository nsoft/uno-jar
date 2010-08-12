package com.simontuffs.onejar.test;

import junit.framework.TestCase;

import com.simontuffs.onejar.test.Invoker.Result;

public class CommandLine extends TestCase {

    public void testOneJarSmoke1() throws Exception {
        Result result = Invoker.run("java -jar dist/one-jar-boot-0.98.jar");
        assertFalse("Expected failure did not occur: " + result, result.status == 0);
    }
    
    public void testOneJarSmoke2() throws Exception {
        Result result = Invoker.run("java -jar dist/one-jar-example-0.98.jar");
        assertTrue("Expected pass did not occur: " + result, result.status == 0);
    }
    
    public void testOneJarMacroSmoke3() throws Exception {
        Result result = Invoker.run("java -jar dist/one-jar-example-macro-0.98.jar");
        assertTrue("Expected pass did not occur: " + result, result.status == 0);
    }
    
    public void testOneJarTaskSmoke4() throws Exception {
        Result result = Invoker.run("java -jar dist/one-jar-example-task-0.98.jar");
        assertTrue("Expected pass did not occur: " + result, result.status == 0);
    }

    public void testOneJarAlternate1() throws Exception {
        Result result = Invoker.run("java -classpath dist/one-jar-example-0.98.jar OneJar");
        assertTrue("Expected pass did not occur: " + result, result.status == 0);
    }
    
    public void testOneJarExt() throws Exception {
        Result result = Invoker.run("java -classpath dist/one-jar-example-ext-0.98.jar OneJar");
        assertTrue("Expected pass did not occur: " + result, result.status == 0);
    }
    
}

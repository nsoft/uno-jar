package com.needhamsoftware.unojar;

import junit.framework.TestCase;
import org.junit.Test;

public class CommandLine extends TestCase {

    @Test
    public void testOneJarAntFromGradleSmoke1() throws Exception {
        Invoker.Result result = Invoker.run("java -jar build/testjar1.jar");
        assertTrue("Expected failure did not occur: " + result, result.status == 0);
        assertEquals(result.out.get(0),"System Out Success - main class");
        assertEquals(result.err.get(0),"System Err Success - library class");
    }
    @Test
    public void testOneJarGradleTaskSmoke2() throws Exception {
        Invoker.Result result = Invoker.run("java -jar build/testJar2.jar");
        assertTrue("Expected failure did not occur: " + result, result.status == 0);
        assertEquals(result.out.get(0),"System Out Success - main class");
        assertEquals(result.err.get(0),"System Err Success - library class");
    }
//
//    public void testOneJarSmoke2() throws Exception {
//        Result result = Invoker.run("java -jar dist/one-jar-example-0.98.jar");
//        assertTrue("Expected pass did not occur: " + result, result.status == 0);
//    }
//
//    public void testOneJarMacroSmoke3() throws Exception {
//        Result result = Invoker.run("java -jar dist/one-jar-example-macro-0.98.jar");
//        assertTrue("Expected pass did not occur: " + result, result.status == 0);
//    }
//
//    public void testOneJarTaskSmoke4() throws Exception {
//        Result result = Invoker.run("java -jar dist/one-jar-example-task-0.98.jar");
//        assertTrue("Expected pass did not occur: " + result, result.status == 0);
//    }
//
//    public void testOneJarAlternate1() throws Exception {
//        Result result = Invoker.run("java -classpath dist/one-jar-example-0.98.jar OneJar");
//        assertTrue("Expected pass did not occur: " + result, result.status == 0);
//    }
//
//    public void testOneJarExt() throws Exception {
//        Result result = Invoker.run("java -classpath dist/one-jar-example-ext-0.98.jar OneJar");
//        assertTrue("Expected pass did not occur: " + result, result.status == 0);
//    }

}

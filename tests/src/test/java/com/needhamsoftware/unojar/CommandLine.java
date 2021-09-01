package com.needhamsoftware.unojar;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommandLine {
  // todo: probably should have some negative tests that use a different message.
  public static final String TEST_SHOULD_NOT_ERROR = "Unexpected Failure - return code not zero: ";

  /**
   * To debug, set the property below, and start the debugger **listening** for connection on 5005, then run
   * the single test you wish to debug.
   */
  public static final String JARGS = Boolean.getBoolean("unojar.test.debug") ?
      "-agentlib:jdwp=transport=dt_socket,server=n,address=localhost:5005,suspend=y -jar" : "-jar";

  /**
   * This will hit the jar file built with our hacked in groovy routine (that currently has much cut and paste
   * and needs to be parameterized) This verifies that our groovy version of packging is working properly.
   */
  @Test
  public void testUnoJarAntFromGradleSmoke1() throws Exception {
    Invoker.Result result = Invoker.run("java  " + JARGS  + " build/testjar1.jar");
    assertEquals(TEST_SHOULD_NOT_ERROR + result, 0, result.status);
    assertEquals( "System Out Success - main class", result.out.get(0));
    assertEquals("System Err Success - library class", result.err.get(0));
  }

  /**
   * This hits a jar built with the actual groovy task configuration. This verifies that the gradle ext config
   * form of packaging is working correctly
   */
  @Test
  public void testUnoJarGradleTaskSmoke2() throws Exception {
    Invoker.Result result = Invoker.run("java  " + JARGS  + " build/testJar2.jar");
    assertEquals(TEST_SHOULD_NOT_ERROR + result, 0, result.status);
    assertEquals( "System Out Success - main class", result.out.get(0));
    assertEquals("System Err Success - library class", result.err.get(0));
  }

  /**
   * This verifies that log4j will work properly when configured within an unojar.
   */
  @Test
  public void testUnoJarGradleTaskSmokeLog4j() throws Exception {
    Invoker.Result result = Invoker.run("java  " + JARGS  + " build/testLog4j.jar");
    assertEquals(TEST_SHOULD_NOT_ERROR + result, 0, result.status);
    assertEquals( "TEST:  ERROR [main] TestMainLog4jPlugin   - Log4j Success - main class", result.out.get(0));
    assertEquals("TEST:  ERROR [main] LibTestLog4jPlugin   - Log4J Success - library class", result.out.get(1));
  }

  /**
   * This verifies that our jars can load classes from the release specific region of an multi-release jar
   * (A. K. A. MRJar see https://openjdk.java.net/jeps/238). Modern versions of log4j are multi-release jars so
   * if this works, it validates MR jars. Without Multi-Release Jar file support the Java 8 version of
   * StackLocator will get used on java 11 and this will throw an UnsupportedOperationException
   */
  @Test
  public void testUnoJarGradleTaskSmokeMRJar() throws Exception {
    Invoker.Result result = Invoker.run("java  " + JARGS  + "  build/testMRJar.jar");
    assertEquals(TEST_SHOULD_NOT_ERROR + result, 0, result.status);
    assertEquals( "TEST:  ERROR [main] TestMainLog4jMRJar   - Log4j Success - main class", result.out.get(0));
    assertEquals("TEST:  ERROR [main] LibTestLog4jPlugin   - Log4J Success - library class", result.out.get(1));
  }

  /**
   * Test that we transparently locate directories with or without a trailing slash (see issue #22)
   */
  @Test
  public void testUnoJarGradleTaskDirWithSlash() throws Exception {
    Invoker.Result result = Invoker.run("java  " + JARGS  + "  build/testDirWithSlashJar.jar");
    assertEquals(TEST_SHOULD_NOT_ERROR + result, 0, result.status);
  }
}

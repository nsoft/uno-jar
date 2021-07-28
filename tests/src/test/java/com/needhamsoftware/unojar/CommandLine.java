package com.needhamsoftware.unojar;

import junit.framework.TestCase;
import org.junit.Test;

public class CommandLine extends TestCase {

  @Test
  public void testUnoJarAntFromGradleSmoke1() throws Exception {
    Invoker.Result result = Invoker.run("java -jar build/testjar1.jar");
    assertEquals("Expected failure did not occur: " + result, 0, result.status);
    assertEquals( "System Out Success - main class", result.out.get(0));
    assertEquals("System Err Success - library class", result.err.get(0));
  }

  @Test
  public void testUnoJarGradleTaskSmoke2() throws Exception {
    Invoker.Result result = Invoker.run("java -jar build/testJar2.jar");
    assertEquals("Expected failure did not occur: " + result, 0, result.status);
    assertEquals( "System Out Success - main class", result.out.get(0));
    assertEquals("System Err Success - library class", result.err.get(0));
  }

  @Test
  public void testUnoJarGradleTaskSmokeLog4j() throws Exception {
    Invoker.Result result = Invoker.run("java -jar build/testLog4j.jar");
    assertEquals("Expected failure did not occur: " + result, 0, result.status);
    assertEquals( "TEST:  ERROR [main] TestMainLog4jPlugin   - Log4j Success - main class", result.out.get(0));
    assertEquals("TEST:  ERROR [main] LibTestLog4jPlugin   - Log4J Success - library class", result.out.get(1));
  }

  @Test
  public void testUnoJarGradleTaskSmokeMRJar() throws Exception {
    Invoker.Result result = Invoker.run("java -jar  build/testMRJar.jar");
    assertEquals("Expected failure did not occur: " + result, 0, result.status);
    assertEquals( "TEST:  ERROR [main] TestMainLog4jMRJar   - Log4j Success - main class", result.out.get(0));
    assertEquals("TEST:  ERROR [main] LibTestLog4jPlugin   - Log4J Success - library class", result.out.get(1));
  }

  @Test
  public void testUnoJarGradleTaskDirWithSlash() throws Exception {
//    Invoker.Result result = Invoker.run("java -agentlib:jdwp=transport=dt_socket,server=n,address=ns-l1:5006,suspend=y -jar  build/testDirWithSlashJar.jar");
    Invoker.Result result = Invoker.run("java -jar  build/testDirWithSlashJar.jar");
    assertEquals("Expected failure did not occur: " + result, 0, result.status);
  }
}

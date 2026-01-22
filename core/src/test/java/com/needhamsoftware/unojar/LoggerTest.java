package com.needhamsoftware.unojar;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class LoggerTest {

  @Test
  public void testOutput() throws IOException, InterruptedException {
    URL exampleJar = getClass().getClassLoader().getResource("uno-jar-examples-unojar.jar");
    assert exampleJar != null;
    System.out.println(exampleJar.getPath());
    final AtomicInteger bootInfoCount = new AtomicInteger(0);
    final AtomicInteger jclDebugCount = new AtomicInteger(0);

    String javaHome = System.getProperty("unojar.jdk.8");
    System.out.println(javaHome);
    ProcessBuilder builder = new ProcessBuilder(javaHome+"/bin/java", "-Duno-jar.log.level=DEBUG", "-jar", exampleJar.getPath());
    //builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
    System.out.println(builder.command());
    Process start = builder.start();
    String output = new BufferedReader(
        new InputStreamReader(start.getInputStream(), StandardCharsets.UTF_8))
        .lines().filter(l -> {
          if (l.matches("^\\[Boot] INFO:.*")) {
            bootInfoCount.incrementAndGet();
          }
          if (l.matches("^\\[JarClassLoader] DEBUG:.*")) {
            jclDebugCount.incrementAndGet();
          }
          return true;
        })
        .collect(Collectors.joining("\n"));
    String error = new BufferedReader(
        new InputStreamReader(start.getErrorStream(), StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));
    start.waitFor();
    System.out.println("Output:"+output);
    System.out.println("Error:"+error);
    System.out.println("Exit:"+start.exitValue());
    assertEquals(2,bootInfoCount.get());
    assertEquals(92,jclDebugCount.get());
  }
}
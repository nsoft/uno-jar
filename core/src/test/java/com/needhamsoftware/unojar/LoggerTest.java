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
    final AtomicInteger bootInfoCount = new AtomicInteger(0);
    final AtomicInteger jclDebugCount = new AtomicInteger(0);

    String javaHome = System.getProperty("java.home");
    ProcessBuilder builder = new ProcessBuilder(javaHome+"/bin/java", "-Duno-jar.log.level=DEBUG", "-jar", exampleJar.getPath());
    Process start = builder.start();
    @SuppressWarnings("unused") // side effects of filter are required
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
    start.waitFor();
    assertEquals(2,bootInfoCount.get());
    // note, new classes or deleted classes cause this to fail, just change the number
    // we are not really concerned unless actual becomes zero or some fantastically large or small number
    assertEquals(82,jclDebugCount.get());
  }
}

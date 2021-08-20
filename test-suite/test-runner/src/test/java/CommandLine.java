import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;

public class CommandLine
    extends TestCase {

  @Test
  public void testMain()
      throws Exception {
    final File unoJarFile = new File("../test-main/build/libs/test-main-unojar.jar");
    assertTrue(unoJarFile.isFile());
    final Invoker.Result result = Invoker.run(String.format("java -jar %s", unoJarFile));
    assertEquals("Expected failure did not occur: " + result, 0, result.status);
    assertEquals("System Out Success - main class", result.out.get(0));
    assertEquals("System Err Success - library class", result.err.get(0));
  }

  @Test
  public void testLog4jPlugin()
      throws Exception {
    final File unoJarFile = new File("../test-log4j-plugin/build/libs/test-log4j-plugin-unojar.jar");
    assertTrue(unoJarFile.isFile());
    final Invoker.Result result = Invoker.run(String.format("java -jar %s", unoJarFile));
    assertEquals("Expected failure did not occur: " + result, 0, result.status);
    assertEquals("TEST:  ERROR [main] TestMainLog4jPlugin   - Log4j Success - main class", result.out.get(0));
    assertEquals("TEST:  ERROR [main] LibTestLog4jPlugin   - Log4J Success - library class", result.out.get(1));
  }

  @Test
  public void testLog4jMrJar()
      throws Exception {
    final File unoJarFile = new File("../test-log4j-mr-jar/build/libs/test-log4j-mr-jar-unojar.jar");
    assertTrue(unoJarFile.isFile());
    final Invoker.Result result = Invoker.run(String.format("java -jar %s", unoJarFile));
    assertEquals("Expected failure did not occur: " + result, 0, result.status);
    assertEquals("TEST:  ERROR [main] TestMainLog4jMRJar   - Log4j Success - main class", result.out.get(0));
    assertEquals("TEST:  ERROR [main] LibTestLog4jPlugin   - Log4J Success - library class", result.out.get(1));
  }

  @Test
  public void testMainDirWithSlash()
      throws Exception {
    final File unoJarFile = new File("../test-main-dir-with-slash/build/libs/test-main-dir-with-slash-unojar.jar");
    assertTrue(unoJarFile.isFile());
    final Invoker.Result result = Invoker.run(String.format("java -jar %s", unoJarFile));
    assertEquals("Expected failure did not occur: " + result, 0, result.status);
  }

  @Test
  public void testGroovyDsl()
      throws Exception {
    final File unoJarFile = new File("../groovy-dsl/build/libs/test-unojar.jar");
    assertTrue(unoJarFile.isFile());
    final Invoker.Result result = Invoker.run(String.format("java -jar %s", unoJarFile));
    assertEquals("Expected failure did not occur: " + result, 0, result.status);
    assertEquals("Hello, world!", result.out.get(0));
  }
}

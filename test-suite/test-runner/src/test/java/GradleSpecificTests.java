import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;

public class GradleSpecificTests
    extends TestCase {

  @Test
  public void testGroovyDsl()
      throws Exception {
    final File unoJarFile = new File("../groovy-dsl/build/libs/test-unojar.jar");
    assertTrue(unoJarFile.isFile());
    final Invoker.Result result = Invoker.run(String.format("java -jar %s", unoJarFile));
    assertEquals("Expected failure did not occur: " + result, 0, result.status);
    assertEquals("Hello, world!", result.out.get(0));
  }

  @Test
  public void testKotlinDsl()
      throws Exception {
    final File unoJarFile = new File("../kotlin-dsl/build/libs/test-unojar.jar");
    assertTrue(unoJarFile.isFile());
    final Invoker.Result result = Invoker.run(String.format("java -jar %s", unoJarFile));
    assertEquals("Expected failure did not occur: " + result, 0, result.status);
    assertEquals("Hello, world!", result.out.get(0));
  }
}

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.Assert.*;

public class GradleSpecificTests {

  @Test
  public void testGroovyDslUsingExtension()
      throws Exception {
    testUnoJar("../groovy-dsl/build/libs/groovy-dsl-unojar.jar", "Groovy-Extension");
  }

  @Test
  public void testGroovyDslUsingTasksRegister()
      throws Exception {
    testUnoJar("../groovy-dsl/build/libs/test-unojar.jar", "Groovy-Register");
  }

  @Test
  public void testKotlinDslExtension()
      throws Exception {
    testUnoJar("../kotlin-dsl/build/libs/kotlin-dsl-unojar.jar", "Kotlin-Extension");
  }

  @Test
  public void testKotlinDslRegister()
      throws Exception {
    testUnoJar("../kotlin-dsl/build/libs/test-unojar.jar", "Kotlin-Register");
  }

  public void testUnoJar(String pathname, String testValue)
      throws Exception {
    final File unoJarFile = new File(pathname);
    assertTrue(unoJarFile.isFile());
    final Invoker.Result result = Invoker.run(String.format("java -jar %s", unoJarFile));
    assertEquals("Expected failure did not occur: " + result, 0, result.status);
    assertEquals("Hello, world!", result.out.get(0));
    InputStream inputStream;
    try (ZipFile zf = new ZipFile(unoJarFile)) {
      ZipEntry entry = zf.getEntry("META-INF/MANIFEST.MF");
      assertNotNull(entry);
      inputStream = zf.getInputStream(entry);

      assertNotNull(inputStream);
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
      String line;
      boolean found = false;
      while (reader.ready() && !found) {
        line = reader.readLine();
        String[] split = line.split(":");
        if (split.length == 2) {
          if ("Test-Attribute".equals(split[0].trim())) {
            found = testValue.equals(split[1].trim());
          }
        }
      }

      assertTrue("Test-Attribute not found in Manifest", found);
    }
  }

}

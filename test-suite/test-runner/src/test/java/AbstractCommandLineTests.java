import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class AbstractCommandLineTests {

  private final String relativeDir;

  protected AbstractCommandLineTests(String relativeDir) {
    super();

    this.relativeDir = relativeDir;
  }

  @SuppressWarnings("SameParameterValue")
  private File newFile(String projectName, String version, String classifier) {
    final File projectDir = new File("..", projectName);
    final File libsDir = new File(projectDir, relativeDir);
    final List<String> parts = new ArrayList<>();
    Objects.requireNonNull(projectName);
    parts.add(projectName);
    if (version != null) {
      parts.add(version);
    }
    if (classifier != null) {
      parts.add(classifier);
    }
    final String filename = StringUtils.join(parts, "-") + ".jar";
    return new File(libsDir, filename);
  }

  @SuppressWarnings("SameParameterValue")
  private File newFile(String projectName, String version) {
    return newFile(projectName, version, "unojar");
  }

  private File newFile(String projectName) {
    return newFile(projectName, null);
  }


  @Test
  public void testMain()
      throws Exception {
    final File unoJarFile = newFile("test-main");
    assertTrue(unoJarFile.isFile());
    final Invoker.Result result = Invoker.run(String.format("java -jar %s", unoJarFile));
    assertEquals("Unexpected Failure: " + result, 0, result.status);
    assertEquals("System Out Success - main class", result.out.get(0));
    assertEquals("System Err Success - library class", result.err.get(0));
  }

  @Test
  public void testLog4jPlugin()
      throws Exception {
    final File unoJarFile = newFile("test-log4j-plugin");
    assertTrue(unoJarFile.isFile());
    final Invoker.Result result = Invoker.run(String.format("java -jar %s", unoJarFile));
    assertEquals("Unexpected Failure: " + result, 0, result.status);
    assertEquals("TEST:  ERROR [main] TestMainLog4jPlugin   - Log4j Success - main class", result.out.get(0));
    assertEquals("TEST:  ERROR [main] LibTestLog4jPlugin   - Log4J Success - library class", result.out.get(1));
  }

  @Test
  public void testLog4jMrJar()
      throws Exception {
    final File unoJarFile = newFile("test-log4j-mr-jar");
    assertTrue(unoJarFile.isFile());
    final Invoker.Result result = Invoker.run(String.format("java -jar %s", unoJarFile));
    assertEquals("Unexpected Failure: " + result, 0, result.status);
    assertEquals("TEST:  ERROR [main] TestMainLog4jMRJar   - Log4j Success - main class", result.out.get(0));
    assertEquals("TEST:  ERROR [main] LibTestLog4jPlugin   - Log4J Success - library class", result.out.get(1));
  }

  @Test
  public void testMainDirWithSlash()
      throws Exception {
    final File unoJarFile = newFile("test-main-dir-with-slash");
    assertTrue(unoJarFile.isFile());
    final Invoker.Result result = Invoker.run(String.format("java -jar %s", unoJarFile));
    assertEquals("Unexpected Failure: " + result, 0, result.status);
  }
}

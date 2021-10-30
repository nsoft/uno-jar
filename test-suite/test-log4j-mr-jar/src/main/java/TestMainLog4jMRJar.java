import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestMainLog4jMRJar {

  // Issue #5 without Multi-Release Jar file support the Java 8 version of StackLocator
  // will get used and this will throw an UnsupportedOperationException
  private static final Logger log = LogManager.getLogger();

  public static void main(String[] args) {
    log.error("Log4j Success - main class");
    new LibTestLog4jPlugin().test();
  }
}

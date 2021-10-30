import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestMainLog4jPlugin {
  private static final Logger log = LogManager.getLogger(TestMainLog4jPlugin.class);

  public static void main(String[] args) {
    // Issue #10 - this was producing "2020-01-03 10:59:40,552 main ERROR Error processing element TestConsole ([Appenders: null]): CLASS_NOT_FOUND"
    log.error("Log4j Success - main class");
    new LibTestLog4jPlugin().test();
  }

}

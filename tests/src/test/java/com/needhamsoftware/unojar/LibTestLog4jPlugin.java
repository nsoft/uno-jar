package com.needhamsoftware.unojar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LibTestLog4jPlugin {
  private static final Logger log = LogManager.getLogger(LibTestLog4jPlugin.class);

  public void test() {
    log.error("Log4J Success - library class");
  }
}

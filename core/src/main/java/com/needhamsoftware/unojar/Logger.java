/*
 * Copyright (C) 2012 by Netcetera AG.
 * Copyright (c) 2019=2020, Needham Software LLC
 * All rights reserved.
 *
 * See the full license at https://github.com/nsoft/uno-jar/blob/master/LICENSE.txt
 * See addition code licenses at: https://github.com/nsoft/uno-jar/blob/master/NOTICE.txt
 */
package com.needhamsoftware.unojar;

/**
 * Simple logger.
 */
public final class Logger {

  /**
   * Disabled logging.
   */
  public static final int LOGLEVEL_NONE = 0;

  /**
   * Only errors.
   */
  public static final int LOGLEVEL_SEVERE = 1;

  /**
   * Warning and higher severity.
   */
  public static final int LOGLEVEL_WARN = 2;

  /**
   * Info and higher severity.
   */
  public static final int LOGLEVEL_INFO = 3;

  /**
   * Verbose logging.
   */
  public static final int LOGLEVEL_VERBOSE = 5;

  // Loglevel for all loggers.
  private static int loglevel = LOGLEVEL_NONE;

  private final String prefix;

  /**
   * Creates a logger with the given name.
   *
   * @param name the name of the logger
   * @return a new logger
   */
  public static Logger getLogger(String name) {
    return new Logger(name);
  }

  private Logger(String name) {
    this.prefix = "[" + name + "] ";
  }

  /**
   * Log a SEVERE message.
   *
   * @param message the message to be logged
   */
  public void severe(String message) {
    if (loglevel >= LOGLEVEL_SEVERE) {
      System.err.println(this.prefix + "ERROR: " + message);
    }
  }

  /**
   * Log a WARNING message.
   *
   * @param message the message to be logged
   */
  public void warning(String message) {
    if (loglevel >= LOGLEVEL_WARN) {
      System.err.println(this.prefix + "WARN:  " + message);
    }
  }

  /**
   * Log a INFO message.
   *
   * @param message the message to be logged
   */
  public void info(String message) {
    if (loglevel >= LOGLEVEL_INFO) {
      System.out.println(this.prefix + "INFO:  " + message);
    }
  }

  /**
   * Log a FINE message.
   *
   * @param message the message to be logged
   */
  public void fine(String message) {
    if (loglevel >= LOGLEVEL_VERBOSE) {
      System.out.println(this.prefix + "FINE:  " + message);
    }
  }

  /**
   * Sets the logging level.
   *
   * @param level logging level
   */
  public static void setLevel(int level) {
    loglevel = level;
  }

}

/*
 * Copyright (C) 2012 by Netcetera AG.
 * Copyright (c) 2019=2020, Needham Software LLC
 * All rights reserved.
 *
 * See the full license at https://github.com/nsoft/uno-jar/blob/master/LICENSE.txt
 * See addition code licenses at: https://github.com/nsoft/uno-jar/blob/master/NOTICE.txt
 */
package com.needhamsoftware.unojar;

import java.util.function.Supplier;

import static com.needhamsoftware.unojar.JarClassLoader.PROPERTY_PREFIX;

/**
 * Simple logger. This is not a real logger. It's a glorified println. However, to avoid external
 * dependencies we won't be transitioning to SLF4J or similar. This should never get used
 * outside the unojar classes, and usage should avoid string concatenation in any methods that
 * could get used when loading classes for actual application code. There is also no protection
 * against multiple threads interleaving with each other's output. The expectation is that 99.9% of
 * time it will run NONE.
 */
final class UnoJarPrintlnLogger {

  public final static String P_LOG_LEVEL = PROPERTY_PREFIX + "log.level";

  public enum Level {
    DEBUG, INFO, WARN, ERROR, NONE
  }

  // Loglevel for all loggers.
  private static Level loglevel = Level.valueOf(System.getProperty(P_LOG_LEVEL, "NONE"));

  private final String prefix;

  /**
   * Creates a logger with the given name.
   *
   * @param name the name of the logger
   * @return a new logger
   */
  public static UnoJarPrintlnLogger getLogger(String name) {
    return new UnoJarPrintlnLogger(name);
  }

  private UnoJarPrintlnLogger(String name) {
    this.prefix = "[" + name + "] ";
  }

  private void logAt(Level level, String msg, Object... args) {
    if (loglevel.ordinal() <= level.ordinal()) {
      System.out.println(prefix + level + ":" + String.format(msg,args));
    }
  }

  /**
   * Log a SEVERE message.
   *
   * @param message the message to be logged
   */
  public void error(String message, Object... args) {
    logAt(Level.ERROR, message, args);
  }

  /**
   * Log a WARNING message.
   *
   * @param message the message to be logged
   */
  public void warning(String message, Object... args) {
    logAt(Level.WARN, message, args);
  }

  /**
   * Log a INFO message.
   *
   * @param message the message to be logged
   */
  public void info(String message, Object... args) {
    logAt(Level.INFO, message, args);
  }

  /**
   * Log a FINE message.
   *
   * @param message the message to be logged
   */
  public void debug(String message, Object... args) {
    logAt(Level.DEBUG, message, args);
  }

  public static DeferredLogValue defer(Supplier<String> supplier) {
    return new DeferredLogValue(supplier);
  }

  public static class DeferredLogValue {
    private final Supplier<String> supplier;

    public DeferredLogValue(Supplier<String> supplier) {
      this.supplier = supplier;
    }

    @Override
    public String toString() {
      return supplier.get();
    }
  }
}

package com.needhamsoftware.unojar;

public class Java11SpecificExtensions extends Java9SpecificExtensions {
  @Override
  public String pad(String indent, String string, int width) {
    return indent + string + " ".repeat(Math.max(0, width - string.length()));
  }
}

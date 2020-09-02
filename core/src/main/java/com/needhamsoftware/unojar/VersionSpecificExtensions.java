package com.needhamsoftware.unojar;

import java.util.Set;

@SuppressWarnings({"deprecation"})
public class VersionSpecificExtensions {
  public String getName(ClassLoader classLoader) {
    return null;
  }

  public Package getDefinedPackage(ClassLoader classLoader, String packageName) {
    return new ClassLoader(classLoader) {
      public Package f() {
        return this.getPackage(packageName);
      }
    }.f();
  }

  public String getCaller(Set<String> byteCodeClasses) {
    for (StackTraceElement stackTraceElement : new Throwable().getStackTrace()) {
      String caller = stackTraceElement.getClassName();
      String cls = getByteCodeName(caller);
      if (byteCodeClasses.contains(cls) && !caller.startsWith("com.needhamsoftware.unojar")) {
        return cls;
      }
    }

    return null;
  }

  public String pad(String indent, String string, int width) {
    StringBuilder res = new StringBuilder(indent + string);
    for (int i = 0; i < Math.max(0, width - string.length()); i++) {
      res.append(" ");
    }
    return res.toString();
  }

  protected static String getByteCodeName(String className) {
    return className.replace(".", "/") + ".class";
  }
}

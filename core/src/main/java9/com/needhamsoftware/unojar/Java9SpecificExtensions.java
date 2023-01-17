package com.needhamsoftware.unojar;

import java.util.Optional;
import java.util.Set;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

public class Java9SpecificExtensions extends VersionSpecificExtensions {
  @Override
  public String getName(ClassLoader classLoader) {
        return classLoader.getName();
    }

  @Override
  public Package getDefinedPackage(ClassLoader classLoader, String packageName) {
    return classLoader.getDefinedPackage(packageName);
  }

  @Override
  public String getCaller(Set<String> byteCodeClasses) {
    StackWalker walker = StackWalker.getInstance(RETAIN_CLASS_REFERENCE);
    Optional<StackWalker.StackFrame> firstByteCode = walker.walk(s -> s.filter(f -> {
      String caller = f.getClassName();
      String cls = getByteCodeName(caller);
      return byteCodeClasses.contains(cls) && !caller.startsWith("com.needhamsoftware.unojar");
    }).findFirst());
    return firstByteCode.map(stackFrame -> getByteCodeName(stackFrame.getClassName())).orElse(null);
  }

  private static String getByteCodeName(String className) {
        return className.replace(".", "/") + ".class";
    }
}

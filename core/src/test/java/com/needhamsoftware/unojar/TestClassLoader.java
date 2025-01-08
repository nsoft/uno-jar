package com.needhamsoftware.unojar;

@SuppressWarnings("unused") // used in sandbox, but needs to be here
public class TestClassLoader extends ClassLoader {
  static{
    System.out.println("TestClassLoader used");
  }

  public TestClassLoader(ClassLoader classLoader) {
    super(classLoader);
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    System.out.println("Loading class :" + name);
    return super.loadClass(name);
  }
}


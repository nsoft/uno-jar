package com.needhamsoftware.unojar.jdk8;
import java.lang.reflect.Method;

public class Main {
    public static void main(String[] args) throws NoSuchMethodException {
        Method getPackage = ClassLoader.class.getDeclaredMethod("getPackage", String.class);
        getPackage.setAccessible(true);
        if (getPackage.getDeclaredAnnotation(Deprecated.class) != null) {
            throw new RuntimeException("This is not java 8");
        }
        System.out.println("Success!");
    }
}

/*
 * Created on May 20, 2005
 *
 */
package com.simontuffs.onejar.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author simon
 *
 */
public class Invoker {
    
    public static Object get(Object obj, String field) throws Exception {
        Field f = obj.getClass().getField(field);
        return f.get(obj);
    }

    public static Object invoke(Class cls, String method) throws Exception {
        Method m = cls.getMethod(method, null);
        return m.invoke(null, null);
    }

    public static Object invoke(Class cls, String method, Class sig[], Object args[]) throws Exception {
        Method m = cls.getMethod(method, sig);
        return m.invoke(null, args);
    }
    
    public static Object invoke(Object obj, String method) throws Exception {
        return invoke(obj, method, null, null);
    }
    
    public static Object invoke(Object obj, String method, Class sig[], Object args[]) throws Exception {
        Method m = obj.getClass().getMethod(method, sig);
        return m.invoke(obj, args);
    }

}

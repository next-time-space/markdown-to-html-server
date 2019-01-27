package com.nexttimespace.markdownserver.docs.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestHelper {
    
    public static Object callPrivateMethod(Object object, String methodName, Object...objects) {
        try {
            Method targetMethod = object.getClass().getDeclaredMethod(methodName, null);
            targetMethod.setAccessible(true);
            return targetMethod.invoke(object, objects);
        } catch(Exception e) {
            return null;
        }
    }
    
    public static Object getField(Object object, String field) {
        try {
            Field targetField = object.getClass().getDeclaredField(field);
            targetField.setAccessible(true);
            return targetField.get(object);
        } catch(Exception e) {
            return null;
        }
    }
    
    public static void setField(Object object, String field, Object value) {
        try {
            Field targetField = object.getClass().getDeclaredField(field);
            targetField.setAccessible(true);
            targetField.set(object, value);
        } catch(Exception e) {
            // Do nothing
        }
    }

}

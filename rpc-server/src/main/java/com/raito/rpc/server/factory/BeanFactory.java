package com.raito.rpc.server.factory;

import java.lang.reflect.InvocationTargetException;

/**
 * @author raito
 * @since 2025/6/28
 */
public class BeanFactory {
    public static Object getBean(Class<?> originalClass) {
        try {
            return originalClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}

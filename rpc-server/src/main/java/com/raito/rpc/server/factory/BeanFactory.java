package com.raito.rpc.server.factory;

import com.raito.rpc.common.exception.BeanCreateException;

import java.lang.reflect.InvocationTargetException;

/**
 * @author raito
 * @since 2025/6/28
 */
public class BeanFactory {
    public static <T> T getBean(Class<T> originalClass) {
        try {
            try {
                if (SpringContextHolder.getContext() != null) {
                    return SpringContextHolder.getContext().getBean(originalClass);
                }
            } catch (Exception ignored) {
            }
            return originalClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new BeanCreateException(e);
        }
    }
}

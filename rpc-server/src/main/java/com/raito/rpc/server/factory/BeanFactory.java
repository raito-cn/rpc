package com.raito.rpc.server.factory;

import com.raito.rpc.common.exception.BeanCreateException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author raito
 * @since 2025/6/28
 */
public class BeanFactory {
    private final static Map<Class<?>, Object> instances = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> originalClass) {
        try {
            return (T) instances.computeIfAbsent(originalClass, clazz -> {
                try {
                    if (SpringContextHolder.getContext() != null) {
                        return SpringContextHolder.getContext().getBean(originalClass);
                    }
                } catch (Exception ignored) {
                }
                try {
                    return originalClass.getConstructor().newInstance();
                } catch (Exception e) {
                    throw new BeanCreateException(e);
                }
            });
        } catch (Exception e) {
            throw new BeanCreateException(e);
        }
    }
}

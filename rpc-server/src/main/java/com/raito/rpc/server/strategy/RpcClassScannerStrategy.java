package com.raito.rpc.server.strategy;

import java.lang.annotation.Annotation;

/**
 * @author cn
 * @since 2025/6/30 11:25
 * @version 1.0
 */
public interface RpcClassScannerStrategy<T extends Annotation> {
    void process(Class<?> clazz);

    Class<T> annotationType();
}

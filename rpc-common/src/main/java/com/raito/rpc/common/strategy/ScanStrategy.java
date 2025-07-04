package com.raito.rpc.common.strategy;

/**
 * @author cn
 * @since 2025/7/4 11:30
 * @version 1.0
 */
public abstract class ScanStrategy {
    public abstract void process(Class<?> clazz);

    public abstract Runnable after();
}

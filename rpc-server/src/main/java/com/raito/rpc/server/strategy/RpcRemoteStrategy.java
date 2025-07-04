package com.raito.rpc.server.strategy;

import com.raito.rpc.server.helper.InvokeHelper;
import com.raito.rpc.server.classloader.RpcServerClassloader;
import com.raito.rpc.server.classloader.RpcProxyClass;

/**
 * @author raito
 * @since 2025/6/29
 */
public abstract class RpcRemoteStrategy<U> {
    public final U getResult(InvokeHelper helper) {
        try {
            RpcProxyClass instance = RpcServerClassloader.getProxyInstance(helper.getServer());
            Object result = instance.invoke(helper.getMethodName(), helper.getArgs());
            return ok(result);
        } catch (Exception e) {
            return error("远程调用出错, 原因:%s".formatted(e.getMessage()));
        }
    }

    protected abstract U ok(Object result);

    protected abstract U error(String errorMessage);
}

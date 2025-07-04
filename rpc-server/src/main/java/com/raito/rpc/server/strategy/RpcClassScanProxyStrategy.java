package com.raito.rpc.server.strategy;

import com.raito.rpc.common.strategy.ScanStrategy;
import com.raito.rpc.server.annotation.RpcProxy;
import com.raito.rpc.server.annotation.RpcProxyMethod;
import com.raito.rpc.server.classloader.RpcServerClassloader;
import com.raito.rpc.server.helper.MethodHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author cn
 * @since 2025/6/30 11:32
 * @version 1.0
 */
@Slf4j
public class RpcClassScanProxyStrategy extends ScanStrategy {
    public static final List<MethodHelper> methodHelpers = Collections.synchronizedList(new LinkedList<>());

    @Override
    public void process(Class<?> clazz) {
        RpcProxy annotation = clazz.getAnnotation(RpcProxy.class);
        if (annotation == null) return;
        String baseServer = !StringUtils.hasText(annotation.server()) ? clazz.getSimpleName() : annotation.server();
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(RpcProxyMethod.class) && !annotation.all()) {
                continue;
            }
            String methodName = method.getName();
            String server = baseServer;

            RpcProxyMethod rpcProxyMethod = method.getAnnotation(RpcProxyMethod.class);
            if (rpcProxyMethod != null) {
                methodName = !StringUtils.hasText(rpcProxyMethod.methodName()) ? method.getName() : rpcProxyMethod.methodName();
                server = !StringUtils.hasText(rpcProxyMethod.server()) ? baseServer : rpcProxyMethod.server();
            }
            methodHelpers.add(new MethodHelper(server, methodName, method));
        }
    }

    @Override
    public Runnable after() {
        return () -> {
            List<MethodHelper> methodHelpers = RpcClassScanProxyStrategy.methodHelpers;
            RpcServerClassloader.register(methodHelpers);
            log.info("rpc-server端动态代理调用类完成!");
        };
    }
}

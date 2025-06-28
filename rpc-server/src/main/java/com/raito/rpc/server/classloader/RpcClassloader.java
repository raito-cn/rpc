package com.raito.rpc.server.classloader;

import com.raito.rpc.common.exception.ProxyException;
import com.raito.rpc.server.factory.BeanFactory;
import com.raito.rpc.server.factory.ProxyGenerator;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cn
 * @since 2025/6/27 16:55
 * @version 1.0
 */
public class RpcClassloader extends ClassLoader {
    private static final RpcClassloader CLASS_LOADER = new RpcClassloader();
    private static final Map<String, RpcProxyClass> PROXY_INSTANCES = new ConcurrentHashMap<>();

    /**
     * class中的每一个方法都整合为invoke if-else的形式的动态代理类
     * @param map 需要代理的类和方法信息
     */
    public static void register(Map<Class<?>, List<Method>> map) {
        try {
            for (var entry : map.entrySet()) {
                Class<?> originalClass = entry.getKey();
                List<Method> methods = entry.getValue();
                if (originalClass == null || methods == null || methods.isEmpty()) continue;

                byte[] proxyBytes = ProxyGenerator.generateProxyClass(originalClass, methods);
                Class<? extends RpcProxyClass> proxyClass = CLASS_LOADER.defineClass(ProxyGenerator.getClassProxyName(originalClass, false), proxyBytes);
                Object targetInstance = BeanFactory.getBean(originalClass);
                RpcProxyClass proxyInstance = proxyClass.getConstructor(Object.class).newInstance(targetInstance);
                PROXY_INSTANCES.put(originalClass.getName(), proxyInstance);
            }
        } catch (Exception e) {
            throw new ProxyException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public Class<? extends RpcProxyClass> defineClass(String name, byte[] b) {
        return (Class<? extends RpcProxyClass >) super.defineClass(name, b, 0, b.length);
    }

    public static RpcProxyClass getProxyInstance(String className) {
        return PROXY_INSTANCES.get(className);
    }
}

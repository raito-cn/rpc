package com.raito.rpc.server.test;

import com.raito.rpc.server.annotation.RpcProxy;
import com.raito.rpc.server.classloader.RpcClassloader;
import com.raito.rpc.server.classloader.RpcProxyClass;
import com.raito.rpc.server.scanner.ClassScanner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author raito
 * @since 2025/6/28
 */
public class Test {
    @RpcProxy
    public void test() {
        System.out.println("hello, proxy!");
    }

    @RpcProxy
    public Object test1(String s) {
        return "s" + "proxy";
    }
    public static void main(String[] args) {
        Map<Class<?>, List<Method>> scan = ClassScanner.scan("com.raito.rpc.server", RpcProxy.class);
        for (Map.Entry<Class<?>, List<Method>> entry : scan.entrySet()) {
            System.out.println(entry.getKey().getName() + " -> " + entry.getValue().toString());
        }
        RpcClassloader.register(scan);
        System.out.println("代理成功!");
        RpcProxyClass proxyInstance = RpcClassloader.getProxyInstance("com.raito.rpc.server.test.Test");
        proxyInstance.invoke("void", "test", null, null);
        Object invoke1 = proxyInstance.invoke(Object.class.getName(), "test1", new String[]{String.class.getName()}, new Object[]{"s"});
        System.out.println(invoke1);
    }
}

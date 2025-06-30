package com.raito.rpc.server.classloader;

import com.raito.rpc.common.exception.ProxyException;
import com.raito.rpc.server.event.ApplicationStartedListener;
import com.raito.rpc.server.factory.ProxyGenerator;
import com.raito.rpc.server.helper.MethodHelper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author cn
 * @version 1.0
 * @since 2025/6/27 16:55
 */
public class RpcClassloader extends ClassLoader {
    private static final RpcClassloader CLASS_LOADER = new RpcClassloader();
    private static final Map<String, RpcProxyClass> PROXY_INSTANCES = new ConcurrentHashMap<>();

    /**
     * class中的每一个方法都整合为invoke if-else的形式的动态代理类
     *
     * @param helpers 需要代理的类
     */
    public static void register(List<MethodHelper> helpers) {
        try (ExecutorService service = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>();

            Map<String, List<MethodHelper>> methodMap = helpers.
                    stream()
                    .collect(Collectors.groupingBy(MethodHelper::getServer));

            for (var entry : methodMap.entrySet()) {
                String server = entry.getKey();
                List<MethodHelper> methods = entry.getValue();
                if (server == null || methods == null || methods.isEmpty()) continue;
                futures.add(service.submit(() -> {
                    try {
                        byte[] proxyBytes = ProxyGenerator.generateProxyClass(server, methods);
                        String classFile = "proxy/" + ProxyGenerator.RPC_PROXY_CLASS_INTERNAL_NAME + server + "$Proxy" + ".class";
                        Path workingDir = ApplicationStartedListener.realUrl; // 当前 JVM 工作目录
                        Path path = Paths.get(workingDir.toString(), classFile);
                        Files.createDirectories(path.getParent());
                        Files.write(path, proxyBytes);

                        Class<? extends RpcProxyClass> proxyClass =
                                CLASS_LOADER.defineClass(ProxyGenerator.RPC_PROXY_CLASS_INTERNAL_JVM_NAME + server + "$Proxy", proxyBytes);
                        RpcProxyClass proxyInstance = proxyClass
                                .getConstructor()
                                .newInstance();
                        PROXY_INSTANCES.put(server, proxyInstance);
                    } catch (Exception e) {
                        throw new ProxyException(e);
                    }
                }));
            }

            for (Future<?> future : futures) {
                future.get(); // 阻塞直到完成
            }

        } catch (Exception e) {
            throw new ProxyException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public Class<? extends RpcProxyClass> defineClass(String name, byte[] b) {
        return (Class<? extends RpcProxyClass>) super.defineClass(name, b, 0, b.length);
    }

    public static RpcProxyClass getProxyInstance(String server) {
        return PROXY_INSTANCES.get(server);
    }
}

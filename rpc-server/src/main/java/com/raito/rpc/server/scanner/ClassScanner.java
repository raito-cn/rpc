package com.raito.rpc.server.scanner;

import com.raito.rpc.common.exception.ScannerException;
import com.raito.rpc.server.strategy.RpcClassScannerStrategy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author cn
 * @version 1.0
 * @since 2025/6/27 16:13
 */
public class ClassScanner {
    public static void scan(String basePackage, Collection<RpcClassScannerStrategy<?>> strategies) {
        String path = basePackage.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Enumeration<URL> resources = classLoader.getResources(path);
            List<Future<?>> futures = new ArrayList<>();

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                File file = new File(URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8));
                if (file.exists() && file.isDirectory()) {
                    scanDirectory(classLoader, basePackage, file, executor, futures, strategies);
                }
            }

            // 等待所有并发任务完成
            for (Future<?> f : futures) f.get();

        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new ScannerException(e);
        }
    }

    private static void scanDirectory(ClassLoader classLoader, String basePackage, File dir,
                                      ExecutorService executor,
                                      List<Future<?>> futures, Collection<RpcClassScannerStrategy<?>> strategies) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(classLoader, basePackage + "." + file.getName(), file, executor, futures, strategies);
            } else if (file.getName().endsWith(".class")) {
                try {
                    String className = basePackage + '.' + file.getName().substring(0, file.getName().length() - 6);
                    Class<?> clazz = classLoader.loadClass(className);
                    for (var strategy : strategies) {
                        if (!clazz.isAnnotationPresent(strategy.annotationType())) continue;
                        futures.add(executor.submit(() -> strategy.process(clazz)));
                    }

                } catch (ClassNotFoundException e) {
                    throw new ScannerException(e);
                }
            }
        }
    }
}

package com.raito.rpc.common.scanner;

import com.raito.rpc.common.exception.ScannerException;
import com.raito.rpc.common.strategy.ScanStrategy;
import com.raito.rpc.common.thread.AsyncThread;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author cn
 * @version 1.0
 * @since 2025/6/27 16:13
 */
@Slf4j
public class ClassScanner {
    public static void scan(String basePackage, Collection<ScanStrategy> strategies) {
        String path = basePackage.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Enumeration<URL> resources = classLoader.getResources(path);
            List<Future<?>> futures = new ArrayList<>();

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                File file = new File(URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8));
                if (file.exists() && file.isDirectory()) {
                    scanDirectory(classLoader, basePackage, file, futures, strategies);
                }
            }
            // 等待所有并发任务完成
            for (Future<?> f : futures) f.get();
            List<Runnable> runnableList = strategies.stream().map(ScanStrategy::after).filter(Objects::nonNull).toList();
            if (!runnableList.isEmpty()) {
                AsyncThread.syncAll(runnableList);
            }
            log.info("扫描回调函数执行完毕!");
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new ScannerException(e);
        }
    }

    private static void scanDirectory(ClassLoader classLoader, String basePackage, File dir,
                                      List<Future<?>> futures, Collection<ScanStrategy> strategies) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(classLoader, "%s.%s".formatted(basePackage, file.getName()), file, futures, strategies);
            } else if (file.getName().endsWith(".class")) {
                try {
                    String className = basePackage + '.' + file.getName().substring(0, file.getName().length() - 6);
                    Class<?> clazz = classLoader.loadClass(className);
                    for (var strategy : strategies) {
                        Future<?> future = AsyncThread.submit(() -> strategy.process(clazz));
                        futures.add(future);
                    }
                } catch (ClassNotFoundException e) {
                    throw new ScannerException(e);
                }
            }
        }
    }
}

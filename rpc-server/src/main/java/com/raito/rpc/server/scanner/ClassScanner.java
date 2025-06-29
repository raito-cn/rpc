package com.raito.rpc.server.scanner;

import com.raito.rpc.common.exception.ScannerException;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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
    public static Map<Class<?>, List<Method>> scan(String basePackage, Class<? extends Annotation> annotation) {
        Map<Class<?>, List<Method>> result = new ConcurrentHashMap<>();
        String path = basePackage.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Enumeration<URL> resources = classLoader.getResources(path);
            List<Future<?>> futures = new ArrayList<>();

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                File file = new File(URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8));
                if (file.exists() && file.isDirectory()) {
                    scanDirectory(classLoader, basePackage, file, annotation, result, executor, futures);
                }
            }

            // 等待所有并发任务完成
            for (Future<?> f : futures) f.get();
            return result;

        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new ScannerException(e);
        }
    }

    private static void scanDirectory(ClassLoader classLoader, String basePackage, File dir,
                                      Class<? extends Annotation> annotation,
                                      Map<Class<?>, List<Method>> result,
                                      ExecutorService executor,
                                      List<Future<?>> futures) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(classLoader, basePackage + "." + file.getName(), file, annotation, result, executor, futures);
            } else if (file.getName().endsWith(".class")) {
                String className = basePackage + '.' + file.getName().substring(0, file.getName().length() - 6);

                // 提交虚拟线程任务
                futures.add(executor.submit(() -> {
                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        List<Method> methodsToProxy = new ArrayList<>();
                        if (clazz.isAnnotationPresent(annotation)) {
                            methodsToProxy.addAll(Arrays.asList(clazz.getDeclaredMethods()));
                        } else {
                            for (Method method : clazz.getDeclaredMethods()) {
                                if (method.isAnnotationPresent(annotation)) {
                                    methodsToProxy.add(method);
                                }
                            }
                        }
                        if (!methodsToProxy.isEmpty()) {
                            result.put(clazz, methodsToProxy);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new ScannerException(e);
                    }
                }));
            }
        }
    }
}

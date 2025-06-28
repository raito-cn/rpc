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

/**
 * @author cn
 * @since 2025/6/27 16:13
 * @version 1.0
 */
public class ClassScanner {
    public static Map<Class<?>, List<Method>> scan(String basePackage, Class<? extends Annotation> annotation) {
        Map<Class<?>, List<Method>> result = new HashMap<>();
        String path = basePackage.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                File file = new File(URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8));
                if (file.exists() && file.isDirectory()) {
                    scanDirectory(classLoader, basePackage, file, annotation, result);
                }
            }
            return result;
        } catch (IOException e) {
            throw new ScannerException(e);
        }
    }

    private static void scanDirectory(ClassLoader classLoader, String basePackage, File dir, Class<? extends Annotation> annotation, Map<Class<?>, List<Method>> result) {
        if (dir == null || dir.listFiles() == null) return;
        for (var file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                // 子包名递归拼接
                scanDirectory(classLoader, basePackage + "." + file.getName(), file, annotation, result);
            } else if (file.getName().endsWith(".class")) {
                String className = basePackage + '.' + file.getName().substring(0, file.getName().length() - 6);
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
            }
        }
    }
}

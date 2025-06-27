package classloader;

import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
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
     * class中的每一个方法都整合为invoke switch的形式的动态代理类
     * @param map 需要代理的类和方法信息
     */
    public static void register(Map<Class<?>, List<Method>> map) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        for (var entry : map.entrySet()) {
            Class<?> originalClass = entry.getKey();
            List<Method> methods = entry.getValue();
            if (originalClass == null || CollectionUtils.isEmpty(methods)) continue;

            byte[] proxyBytes = ProxyGenerator.generateProxyClass(originalClass, methods);

            Class<? extends RpcProxyClass> proxyClass = CLASS_LOADER.defineClass(originalClass.getName() + "Proxy", proxyBytes);
            Object targetInstance = BeanFactory.getBean(originalClass);
            RpcProxyClass proxyInstance = proxyClass.getConstructor(Object.class).newInstance(targetInstance);
            PROXY_INSTANCES.put(originalClass.getName(), proxyInstance);
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

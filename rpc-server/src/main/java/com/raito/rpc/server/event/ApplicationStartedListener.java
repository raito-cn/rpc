package com.raito.rpc.server.event;

import com.raito.rpc.server.annotation.RpcProxy;
import com.raito.rpc.server.classloader.RpcClassloader;
import com.raito.rpc.server.helper.NettyStartHelper;
import com.raito.rpc.server.scanner.ClassScanner;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author raito
 * @since 2025/6/29
 */
@Data
@AllArgsConstructor
public class ApplicationStartedListener {
    private static final Logger log = LoggerFactory.getLogger(ApplicationStartedListener.class);
    private NettyStartHelper nettyStartHelper;

    @EventListener(ApplicationStartedEvent.class)
    public void onSpringApplicationEvent(ApplicationStartedEvent event) throws InterruptedException {
        // 启动netty server
        nettyStartHelper.getManager().start();

        // 扫描基础包 动态代理
        SpringApplication application = event.getSpringApplication();
        Class<?> mainAppClass = findMainApplicationClass(application);
        String basePackage = mainAppClass.getPackage().getName();
        Map<Class<?>, List<Method>> scan = ClassScanner.scan(basePackage, RpcProxy.class);
        RpcClassloader.register(scan);
        log.debug(scan.entrySet().stream()
                .map(entry -> entry.getKey().getSimpleName() + ": " +
                        entry.getValue().stream()
                                .map(Method::getName)
                                .collect(Collectors.joining(", ")))
                .collect(Collectors.joining(";\n"))
        );
    }

    private Class<?> findMainApplicationClass(SpringApplication application) {
        return application.getAllSources()
                .stream()
                .filter(Class.class::isInstance)
                .map(Class.class::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot determine main application class"));
    }
}

package com.raito.rpc.server.event;

import com.raito.rpc.server.classloader.RpcClassloader;
import com.raito.rpc.server.helper.MethodHelper;
import com.raito.rpc.server.helper.NettyStartHelper;
import com.raito.rpc.server.scanner.ClassScanner;
import com.raito.rpc.server.strategy.RpcClassScannerProxyStrategy;
import com.raito.rpc.server.strategy.RpcClassScannerStrategy;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author raito
 * @since 2025/6/29
 */
@Data
public class ApplicationStartedListener {
    private static final Logger log = LoggerFactory.getLogger(ApplicationStartedListener.class);

    private NettyStartHelper nettyStartHelper;

    private List<RpcClassScannerStrategy<?>> strategies;

    public static Path realUrl = Path.of("");

    @EventListener(ApplicationStartedEvent.class)
    public void onSpringApplicationEvent(ApplicationStartedEvent event) throws InterruptedException, URISyntaxException {
        // 启动netty server
        nettyStartHelper.getManager().start();

        // 扫描基础包 动态代理
        SpringApplication application = event.getSpringApplication();
        Class<?> mainAppClass = findMainApplicationClass(application);
        realUrl = Paths.get(mainAppClass.getProtectionDomain().getCodeSource().getLocation().toURI());
        String basePackage = mainAppClass.getPackage().getName();
        ClassScanner.scan(basePackage, strategies);
        List<MethodHelper> methodHelpers = RpcClassScannerProxyStrategy.methodHelpers;
        RpcClassloader.register(methodHelpers);
        log.info("扫描基包{} 动态代理类完成!", basePackage);
        log.debug("动态代理类:\n{}", methodHelpers.stream().map(MethodHelper::toString).collect(Collectors.joining("\n")));
    }

    private Class<?> findMainApplicationClass(SpringApplication application) {
        return application.getAllSources()
                .stream()
                .filter(Class.class::isInstance)
                .map(Class.class::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot determine main application class"));
    }

    @Autowired
    public void setNettyStartHelper(NettyStartHelper nettyStartHelper) {
        this.nettyStartHelper = nettyStartHelper;
    }

    @Autowired
    @Lazy
    public void setStrategies(List<RpcClassScannerStrategy<?>> strategies) {
        this.strategies = strategies;
    }
}

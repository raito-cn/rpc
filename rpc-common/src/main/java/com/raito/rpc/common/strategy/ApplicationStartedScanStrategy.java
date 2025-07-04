package com.raito.rpc.common.strategy;

import com.raito.rpc.common.event.ApplicationStartedListener;
import com.raito.rpc.common.scanner.ClassScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author cn
 * @since 2025/7/4 15:07
 * @version 1.0
 */
@Slf4j
public class ApplicationStartedScanStrategy extends ApplicationStartedStrategy {

    private List<ScanStrategy> strategies;
    public static Path realUrl = Path.of("");

    @Override
    public void run(ApplicationStartedListener listener, ApplicationStartedEvent event) {
        SpringApplication application = event.getSpringApplication();
        Class<?> mainAppClass = ApplicationStartedListener.findMainApplicationClass(application);
        try {
            realUrl = Paths.get(mainAppClass.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (Exception ignore) {

        }
        String basePackage = mainAppClass.getPackage().getName();
        ClassScanner.scan(basePackage, strategies);
        log.info("扫描基包{}完成!", basePackage);
    }

    @Autowired
    public void setStrategies(List<ScanStrategy> strategies) {
        this.strategies = strategies;
    }
}

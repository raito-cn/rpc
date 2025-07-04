package com.raito.rpc.common.event;

import com.raito.rpc.common.factory.SpringContextHolder;
import com.raito.rpc.common.strategy.ApplicationStartedStrategy;
import jakarta.annotation.Nonnull;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

import java.util.Comparator;
import java.util.List;

/**
 * @author raito
 * @since 2025/6/29
 */
@Data
public class ApplicationStartedListener implements BeanNameAware {
    private static final Logger log = LoggerFactory.getLogger(ApplicationStartedListener.class);

    private List<ApplicationStartedStrategy> strategies;

    private String beanName;

    @EventListener(ApplicationStartedEvent.class)
    public void onSpringApplicationEvent(ApplicationStartedEvent event) {
        strategies.stream().sorted(Comparator.comparing(ApplicationStartedStrategy::order))
                .forEach(strategy -> {
                    strategy.run(this, event);
                    strategy.destroy();
                });
        destroy();
    }

    public static Class<?> findMainApplicationClass(SpringApplication application) {
        return application.getAllSources()
                .stream()
                .filter(Class.class::isInstance)
                .map(Class.class::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot determine main application class"));
    }

    public void destroy() {
        SpringContextHolder.destroy(beanName);
        log.debug("ApplicationStartedListener destroy");
    }

    @Override
    public void setBeanName(@Nonnull String name) {
        this.beanName = name;
    }

    @Autowired
    public void setStrategies(List<ApplicationStartedStrategy> strategies) {
        this.strategies = strategies;
    }
}

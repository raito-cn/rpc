package com.raito.rpc.common.strategy;

import com.raito.rpc.common.event.ApplicationStartedListener;
import com.raito.rpc.common.factory.SpringContextHolder;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.context.event.ApplicationStartedEvent;

/**
 * @author cn
 * @since 2025/7/4 14:56
 * @version 1.0
 */
@Slf4j
public abstract class ApplicationStartedStrategy implements BeanNameAware {
    private String beanName;

    public abstract void run(ApplicationStartedListener listener, ApplicationStartedEvent event);

    public int order() {
        return 10;
    }

    public final void destroy() {
        SpringContextHolder.destroy(beanName);
        log.debug("destroy ApplicationStartedStrategy bean: {}", beanName);
    }

    @Override
    public void setBeanName(@Nonnull String name) {
        this.beanName = name;
    }
}

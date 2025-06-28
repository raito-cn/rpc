package com.raito.rpc.server.factory;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author raito
 * @since 2025/6/28
 */
public class SpringContextHolder implements ApplicationContextAware {

    @Getter
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) {
        SpringContextHolder.context = applicationContext;
    }

}
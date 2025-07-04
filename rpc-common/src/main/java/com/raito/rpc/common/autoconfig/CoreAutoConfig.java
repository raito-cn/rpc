package com.raito.rpc.common.autoconfig;

import com.raito.rpc.common.event.ApplicationStartedListener;
import com.raito.rpc.common.factory.SpringContextHolder;
import com.raito.rpc.common.strategy.ApplicationStartedScanStrategy;
import com.raito.rpc.common.thread.AsyncThread;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author cn
 * @since 2025/7/4 10:51
 * @version 1.0
 */
@AutoConfiguration
@EnableConfigurationProperties(AsyncThread.ThreadProperties.class)
public class CoreAutoConfig {
    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }

    @Bean
    public ApplicationStartedListener applicationStartedListener() {
        return new ApplicationStartedListener();
    }

    @Bean
    public ApplicationStartedScanStrategy applicationStartedScanStrategy() {
        return new ApplicationStartedScanStrategy();
    }
}

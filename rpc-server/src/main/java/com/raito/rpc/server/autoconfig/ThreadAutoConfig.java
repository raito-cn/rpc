package com.raito.rpc.server.autoconfig;

import com.raito.rpc.server.thread.AsyncThread;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author raito
 * @since 2025/6/29
 */
@AutoConfiguration
@EnableConfigurationProperties(AsyncThread.ThreadProperties.class)
public class ThreadAutoConfig {
}

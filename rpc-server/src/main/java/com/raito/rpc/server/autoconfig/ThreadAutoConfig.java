package com.raito.rpc.server.autoconfig;

import com.raito.rpc.server.config.NettyServerConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author raito
 * @since 2025/6/29
 */
@AutoConfiguration
@EnableConfigurationProperties(NettyServerConfig.class)
public class ThreadAutoConfig {
}

package com.raito.rpc.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author cn
 * @since 2025/7/4 15:46
 * @version 1.0
 */
@ConfigurationProperties(prefix = "netty.server")
@Data
public class NettyServerConfig {
    private int port = 9001;

    private int timeout = 10;
}

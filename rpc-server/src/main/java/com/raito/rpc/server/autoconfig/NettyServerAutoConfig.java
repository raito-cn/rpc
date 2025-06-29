package com.raito.rpc.server.autoconfig;

import com.raito.rpc.server.event.ApplicationStartedListener;
import com.raito.rpc.server.helper.NettyStartHelper;
import com.raito.rpc.server.manager.NettyServerManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author raito
 * @since 2025/6/29
 */
@AutoConfiguration
public class NettyServerAutoConfig {
    @Bean
    public NettyServerManager nettyServerManager() {
        return new NettyServerManager();
    }

    @Bean
    public NettyStartHelper nettyStartHelper(NettyServerManager manager) {
        return new NettyStartHelper(manager);
    }

    @Bean
    public ApplicationStartedListener applicationStartedListener(NettyStartHelper nettyStartHelper) {
        return new ApplicationStartedListener(nettyStartHelper);
    }
}

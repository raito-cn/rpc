package com.raito.rpc.server.autoconfig;

import com.raito.rpc.server.helper.NettyStartHelper;
import com.raito.rpc.server.manager.NettyServerManager;
import com.raito.rpc.server.strategy.RpcClassScanProxyStrategy;
import com.raito.rpc.server.strategy.StartNettyServer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author raito
 * @since 2025/6/29
 */
@AutoConfiguration
@RequiredArgsConstructor
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
    public StartNettyServer startNettyServer() {
        return new StartNettyServer();
    }

    @Bean
    public RpcClassScanProxyStrategy rpcClassScannerProxyStrategy() {
        return new RpcClassScanProxyStrategy();
    }
}

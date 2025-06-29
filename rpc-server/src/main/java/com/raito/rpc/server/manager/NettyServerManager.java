package com.raito.rpc.server.manager;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import com.raito.rpc.server.netty.NettyServer;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author cn
 * @version 1.0
 * @since 2025/6/27 14:01
 */
@SuppressWarnings("all")
@Slf4j
@Data
public class NettyServerManager {
    private static NettyServer NETTY_SERVER = new NettyServer();

    @Value("${netty.server.port:9001}")
    private int port = 9001;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NETTY_SERVER.jvmShutdown = true;
            log.info("JVM ShutdownHook 触发，开始关闭 Netty...");
            if (NETTY_SERVER == null) return;
            NETTY_SERVER.stop();
        }));
    }

    public void start() throws InterruptedException {
        NETTY_SERVER.start(port);
    }
}

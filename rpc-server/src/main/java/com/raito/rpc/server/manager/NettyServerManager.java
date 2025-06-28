package com.raito.rpc.server.manager;

import lombok.extern.slf4j.Slf4j;
import com.raito.rpc.server.netty.NettyServer;

import java.util.Scanner;

/**
 * @author cn
 * @since 2025/6/27 14:01
 * @version 1.0
 */
@SuppressWarnings("all")
@Slf4j
public class NettyServerManager {
    private static NettyServer NETTY_SERVER = new NettyServer();

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

    public static void main(String[] args) throws InterruptedException {
        new NettyServerManager().start();
        Scanner sc = new Scanner(System.in);
        sc.nextLine();
    }
}

package com.raito.rpc.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author cn
 * @version 1.0
 * @since 2025/6/26 10:28
 */
@SuppressWarnings("all")
@NoArgsConstructor
@Data
@Slf4j
public class NettyServer {
    private int port;
    private ServerBootstrap bootstrap;
    private EventLoopGroup boss;
    private EventLoopGroup worker;
    private ChannelFuture future;

    private volatile boolean stopped = false;
    private volatile boolean restarting = false;  // 标记是否正在重启，防止重复启动
    public static boolean jvmShutdown = false;

    public synchronized void stop() {
        if (stopped) {
            log.info("Netty 服务已停止，重复调用 stop() 忽略");
            return;
        }
        stopped = true;
        try {
            boolean isClosed = false;
            if (future != null && future.channel().isOpen()) {
                future.channel().close().sync();
                isClosed = true;
            }
            if (boss != null && !boss.isShuttingDown() && !boss.isShutdown()) {
                boss.shutdownGracefully().sync();
                isClosed = true;
            }
            if (worker != null && !worker.isShuttingDown() && !worker.isShutdown()) {
                worker.shutdownGracefully().sync();
                isClosed = true;
            }
            if (isClosed) {
                log.info("Netty旧资源已释放");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("关闭 Netty 服务时被中断", e);
        } finally {
            future = null;
            boss = null;
            worker = null;
        }
    }

    public synchronized void start(int listenPort) throws InterruptedException {
        if (restarting) {
            log.info("正在重启中，忽略重复启动请求");
            return;
        }
        stop(); // 先关闭旧服务

        if (bootstrap == null) {
            bootstrap = new ServerBootstrap();
        }

        if (boss == null || boss.isShuttingDown() || boss.isShutdown() || boss.isTerminated()) {
            boss = new NioEventLoopGroup(1);
        }
        if (worker == null || worker.isShuttingDown() || worker.isShutdown() || worker.isTerminated()) {
            int cores = Runtime.getRuntime().availableProcessors();
            worker = new NioEventLoopGroup(cores << 1);
        }

        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new NettyServerInitializer());

        port = listenPort;
        future = bootstrap.bind(port).sync();
        future.channel().closeFuture().addListener(cf -> {
            if (jvmShutdown || stopped) return;
            restart();
        });
        stopped = false;

        log.info("Netty 服务已启动，监听端口: {}", port);
    }

    // 自动重启方法
    private synchronized void restart() {
        if (restarting) {
            log.info("已经处于重启状态，忽略重复重启");
            return;
        }
        restarting = true;

        new Thread(() -> {
            try {
                log.info("开始自动重启 Netty 服务...");
                stop();  // 关闭旧服务
                start(port);  // 重新启动
                log.info("Netty 服务自动重启成功");
                stopped = false;
            } catch (InterruptedException e) {
                log.error("自动重启过程中被中断", e);
                Thread.currentThread().interrupt();
            } finally {
                restarting = false;
            }
        }, "Netty-AutoRestart-Thread").start();
    }
}

package com.raito.rpc.server.thread;

import com.raito.rpc.server.factory.BeanFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author raito
 * @since 2025/6/29
 */
public class AsyncThread {
    // 虚拟线程
    private static volatile ThreadPoolExecutor NETTY_ASYNC_THREAD = null;

    public static ThreadPoolExecutor getNettyAsyncThread() {
        if (NETTY_ASYNC_THREAD == null) {
            synchronized (AsyncThread.class) {
                if (NETTY_ASYNC_THREAD == null) {
                    ThreadProperties properties = BeanFactory.getBean(ThreadProperties.class);
                    NETTY_ASYNC_THREAD = getThreadPoolExecutor(properties);
                }
            }
        }
        return NETTY_ASYNC_THREAD;
    }

    private static ThreadPoolExecutor getThreadPoolExecutor(ThreadProperties threadProperties) {
        return new ThreadPoolExecutor(threadProperties.getParallelism(),
                threadProperties.getParallelism(),
                threadProperties.getKeepAliveTime(),
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(threadProperties.getQueueCapacity()),
                Thread.ofVirtual().factory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @ConfigurationProperties(prefix = "netty.async.thread")
    @Data
    public static class ThreadProperties {
        private String name = "netty-async-virtual-thread";

        private int parallelism = 200;
        // 任务队列最大长度
        private int queueCapacity = 1024;
        // 空闲线程最大存活时间 单位 min
        private long keepAliveTime = 60L;

        private int timeout = 60;
    }
}

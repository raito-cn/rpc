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
        return new ThreadPoolExecutor(threadProperties.getCorePoolSize(),
                threadProperties.getMaxPoolSize(),
                threadProperties.getKeepAliveTime(),
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(threadProperties.getQueueCapacity()),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @ConfigurationProperties(prefix = "netty.async.thread")
    @Data
    public static class ThreadProperties {
        private String name = "netty-async-thread";
        private int corePoolSize = Runtime.getRuntime().availableProcessors();
        private int maxPoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        // 任务队列最大长度
        private int queueCapacity = 1024;
        // 空闲线程最大存活时间 单位 min
        private long keepAliveTime = 60L;
    }
}

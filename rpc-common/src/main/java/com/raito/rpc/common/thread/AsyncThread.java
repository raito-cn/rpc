package com.raito.rpc.common.thread;

import com.raito.rpc.common.exception.ConcurrentException;
import com.raito.rpc.common.factory.BeanFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author raito
 * @since 2025/6/29
 */
public class AsyncThread {
    // 虚拟线程
    private static volatile ThreadPoolExecutor RPC_ASYNC_THREAD = null;

    public static ThreadPoolExecutor getRpcAsyncThread() {
        if (RPC_ASYNC_THREAD == null) {
            synchronized (AsyncThread.class) {
                if (RPC_ASYNC_THREAD == null) {
                    ThreadProperties properties = BeanFactory.getBean(ThreadProperties.class);
                    RPC_ASYNC_THREAD = getThreadPoolExecutor(properties);
                }
            }
        }
        return RPC_ASYNC_THREAD;
    }

    public static Future<?> submit(Runnable runnable) {
        return getRpcAsyncThread().submit(runnable);
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static void syncAll(Collection<Runnable> runnables) {
        List<Future<?>> futures = new ArrayList<>(runnables.size());
        for (Runnable runnable : runnables) {
            Future<?> future = submit(runnable);
            futures.add(future);
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                throw new ConcurrentException(e);
            }
        }
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

    @ConfigurationProperties(prefix = "rpc.async.thread")
    @Data
    public static class ThreadProperties {
        private String name = "rpc-async-virtual-thread";

        private int parallelism = 200;
        // 任务队列最大长度
        private int queueCapacity = 1024;
        // 空闲线程最大存活时间 单位 min
        private long keepAliveTime = 60L;
    }
}

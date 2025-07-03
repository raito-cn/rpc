package com.raito.rpc.common.context;

import java.util.concurrent.Callable;

/**
 * @author cn
 * @since 2025/7/3 18:03
 * @version 1.0
 */
@SuppressWarnings("all")
public class MessageScopedContext {
    public static final ScopedValue<Long> messageId = ScopedValue.newInstance();

    public static Long get() {
        return messageId.get();
    }

    public static <R> R set(Long messageId, Callable<? extends R> runnable) throws Exception {
        return ScopedValue.callWhere(MessageScopedContext.messageId, messageId, runnable);
    }
}

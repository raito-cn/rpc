package com.raito.rpc.common.exception;

/**
 * @author cn
 * @since 2025/7/4 13:48
 * @version 1.0
 */
@SuppressWarnings("unused")
public class ConcurrentException extends BusinessException {
    public ConcurrentException(String message) {
        super(message);
    }

    public ConcurrentException(String message, Object... args) {
        super(message, args);
    }

    public ConcurrentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConcurrentException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public ConcurrentException(Throwable cause) {
        super(cause);
    }
}

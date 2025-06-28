package com.raito.rpc.common.exception;

/**
 * @author raito
 * @since 2025/6/28
 */
@SuppressWarnings("all")
public class UnboxException extends BusinessException {
    public UnboxException(String message) {
        super(message);
    }

    public UnboxException(String message, Object... args) {
        super(message, args);
    }

    public UnboxException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnboxException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public UnboxException(Throwable cause) {
        super(cause);
    }
}

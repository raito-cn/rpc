package com.raito.rpc.common.exception;

/**
 * @author raito
 * @since 2025/6/28
 */
@SuppressWarnings("all")
public class BoxException extends BusinessException {
    public BoxException(String message) {
        super(message);
    }

    public BoxException(String message, Object... args) {
        super(message, args);
    }

    public BoxException(String message, Throwable cause) {
        super(message, cause);
    }

    public BoxException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public BoxException(Throwable cause) {
        super(cause);
    }
}

package com.raito.rpc.common.exception;

/**
 * @author cn
 * @since 2025/6/27 18:36
 * @version 1.0
 */
@SuppressWarnings("all")
public class KeyException extends BusinessException {
    public KeyException(String message) {
        super(message);
    }

    public KeyException(String message, Object... args) {
        super(message, args);
    }

    public KeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public KeyException(Throwable cause) {
        super(cause);
    }
}

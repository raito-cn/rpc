package com.raito.rpc.common.exception;

/**
 * @author raito
 * @since 2025/6/28
 */
@SuppressWarnings("all")
public class BeanCreateException extends BusinessException {
    public BeanCreateException(String message) {
        super(message);
    }

    public BeanCreateException(String message, Object... args) {
        super(message, args);
    }

    public BeanCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanCreateException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public BeanCreateException(Throwable cause) {
        super(cause);
    }
}

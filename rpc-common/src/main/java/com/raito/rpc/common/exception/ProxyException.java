package com.raito.rpc.common.exception;

/**
 * @author raito
 * @since 2025/6/28
 */
@SuppressWarnings("all")
public class ProxyException extends BusinessException {
    public ProxyException(String message) {
        super(message);
    }

    public ProxyException(String message, Object... args) {
        super(message, args);
    }

    public ProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxyException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public ProxyException(Throwable cause) {
        super(cause);
    }
}

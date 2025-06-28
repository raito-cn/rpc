package com.raito.rpc.common.exception;

/**
 * @author cn
 * @since 2025/6/25 16:34
 * @version 1.0
 */
@SuppressWarnings("all")
public class SerialException extends BusinessException {
    public SerialException(String message) {
        super(message);
    }

    public SerialException(String message, Object... args) {
        super(message, args);
    }

    public SerialException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerialException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public SerialException(Throwable cause) {
        super(cause);
    }
}

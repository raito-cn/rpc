package com.raito.rpc.exception;

/**
 * @author cn
 * @since 2025/6/25 13:43
 * @version 1.0
 */
@SuppressWarnings("unused")
public class DecompressException extends BusinessException {
    public DecompressException(String message) {
        super(message);
    }

    public DecompressException(String message, Object... args) {
        super(message, args);
    }

    public DecompressException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecompressException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public DecompressException(Throwable cause) {
        super(cause);
    }
}

package com.raito.rpc.exception;

/**
 * 压缩异常
 * @author cn
 * @since 2025/6/25 13:42
 * @version 1.0
 */
@SuppressWarnings("unused")
public class CompressException extends BusinessException {
    public CompressException(String message) {
        super(message);
    }

    public CompressException(String message, Object... args) {
        super(message, args);
    }

    public CompressException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompressException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public CompressException(Throwable cause) {
        super(cause);
    }
}

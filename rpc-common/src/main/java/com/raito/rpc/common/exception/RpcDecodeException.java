package com.raito.rpc.common.exception;

/**
 * @author cn
 * @since 2025/6/25 14:35
 * @version 1.0
 */
@SuppressWarnings("unused")
public class RpcDecodeException extends BusinessException {
    public RpcDecodeException(String message) {
        super(message);
    }

    public RpcDecodeException(String message, Object... args) {
        super(message, args);
    }

    public RpcDecodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcDecodeException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public RpcDecodeException(Throwable cause) {
        super(cause);
    }
}

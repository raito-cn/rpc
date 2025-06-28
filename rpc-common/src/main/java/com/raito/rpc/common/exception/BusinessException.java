package com.raito.rpc.common.exception;

import org.slf4j.helpers.MessageFormatter;

/**
 * @author cn
 * @since 2025/6/25 13:41
 * @version 1.0
 */
public abstract class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Object... args) {
        super(MessageFormatter.arrayFormat(message, args).getMessage());
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String message, Throwable cause, Object... args) {
        super(MessageFormatter.arrayFormat(message, args).getMessage(), cause);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }
}

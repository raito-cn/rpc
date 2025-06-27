package com.raito.rpc.exception;

/**
 * @author cn
 * @since 2025/6/27 16:21
 * @version 1.0
 */
@SuppressWarnings("all")
public class ScannerException extends BusinessException {
    public ScannerException(String message) {
        super(message);
    }

    public ScannerException(String message, Object... args) {
        super(message, args);
    }

    public ScannerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScannerException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public ScannerException(Throwable cause) {
        super(cause);
    }
}

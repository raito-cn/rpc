package com.raito.rpc.common.exception;

/**
 * @author raito
 * @since 2025/6/29
 */
@SuppressWarnings("all")
public class RemoteException extends BusinessException {
    public RemoteException(String message) {
        super(message);
    }

    public RemoteException(String message, Object... args) {
        super(message, args);
    }

    public RemoteException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteException(String message, Throwable cause, Object... args) {
        super(message, cause, args);
    }

    public RemoteException(Throwable cause) {
        super(cause);
    }
}

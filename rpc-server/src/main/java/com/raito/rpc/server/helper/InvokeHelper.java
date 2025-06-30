package com.raito.rpc.server.helper;

import lombok.Data;

/**
 * @author raito
 * @since 2025/6/29
 */
@Data
public class InvokeHelper {
    private String server;
    private String methodName;
    private Object[] args;

    public InvokeHelper(String server, String methodName, Object[] args) {
        this.server = server;
        this.methodName = methodName;
        this.args = args;
    }
}

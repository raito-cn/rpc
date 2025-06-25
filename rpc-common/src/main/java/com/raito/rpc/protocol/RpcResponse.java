package com.raito.rpc.protocol;

/**
 * @author cn
 * @since 2025/6/25 15:55
 * @version 1.0
 */
@SuppressWarnings("unused")
public class RpcResponse {
    private Object result;        // 调用结果
    private String errorMessage;  // 异常信息（如果有）
    private boolean success;      // 是否成功
}

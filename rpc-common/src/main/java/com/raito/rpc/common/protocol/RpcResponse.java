package com.raito.rpc.common.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cn
 * @since 2025/6/25 15:55
 * @version 1.0
 */
@SuppressWarnings("unused")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse {
    private Object result;        // 调用结果
    private String errorMessage;  // 异常信息（如果有）
    private boolean success;      // 是否成功

    @Override
    public String toString() {
        return "RpcResponse{" +
                "result=" + result +
                ", errorMessage='" + errorMessage + '\'' +
                ", success=" + success +
                '}';
    }
}

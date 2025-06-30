package com.raito.rpc.common.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

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
public class RpcRequest {
    private String server;     // 要调用的类名（接口名）
    private String methodName;    // 要调用的方法名
    private String[] args;      // 方法参数值

    @Override
    public String toString() {
        return "RpcRequest{" +
                "server='" + server + '\'' +
                ", methodName='" + methodName + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}

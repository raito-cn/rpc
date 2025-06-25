package com.raito.rpc.protocol;

import lombok.Builder;
import lombok.Data;

/**
 * @author cn
 * @since 2025/6/25 15:55
 * @version 1.0
 */
@SuppressWarnings("unused")
@Data
@Builder
public class RpcRequest {
    private String className;     // 要调用的类名（接口名）
    private String methodName;    // 要调用的方法名
    private Class<?>[] paramTypes;// 方法参数类型
    private Object[] args;      // 方法参数值
}

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
    private String className;     // 要调用的类名（接口名）
    private Class<?>[] returnType;// 方法返回类型
    private String methodName;    // 要调用的方法名
    private Class<?>[] paramTypes;// 方法参数类型
    private Object[] args;      // 方法参数值

    public String toString() {
        return "RpcRequest{" +
                "className='" + className + '\'' +
                ", returnType=" + Arrays.toString(returnType) +
                ", methodName='" + methodName + '\'' +
                ", paramTypes=" + Arrays.toString(paramTypes) +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}

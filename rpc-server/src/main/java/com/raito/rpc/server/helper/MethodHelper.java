package com.raito.rpc.server.helper;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author cn
 * @since 2025/6/30 10:37
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class MethodHelper {
    private String server;

    private String methodName;

    private Method method;
}

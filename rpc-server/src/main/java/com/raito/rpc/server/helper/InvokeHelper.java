package com.raito.rpc.server.helper;

import com.raito.rpc.common.exception.SerialException;
import com.raito.rpc.common.util.JsonUtils;
import lombok.Data;

/**
 * @author raito
 * @since 2025/6/29
 */
@Data
public class InvokeHelper {
    private String className;
    private String returnType;
    private String methodName;
    private String[] argTypes;
    private Object[] args;

    public InvokeHelper(String className, String returnType, String methodName, String[] argTypes, Object[] args) {
        this.className = className;
        this.returnType = returnType;
        this.methodName = methodName;
        this.argTypes = argTypes;
        this.args = new Object[args.length];
        for (int i = 0; i < argTypes.length; i++) {
            if (args[i] == null) {
                this.args[i] = null;
            }
            try {
                Class<?> clazz = Class.forName(argTypes[i]);
                // 特别处理：如果是字符串，不需要反序列化，直接处理转义
                if (clazz == String.class) {
                    this.args[i] = args[i].toString();
                } else {
                    this.args[i] = JsonUtils.fromJson(args[i].toString(), clazz);
                }
            } catch (Exception e) {
                throw new SerialException("反序列化失败", e);
            }
        }
    }
}

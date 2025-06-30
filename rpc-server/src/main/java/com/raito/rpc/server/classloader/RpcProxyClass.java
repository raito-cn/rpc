package com.raito.rpc.server.classloader;

import com.raito.rpc.common.exception.KeyException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @author cn
 * @version 1.0
 * @since 2025/6/27 18:06
 */
public abstract class RpcProxyClass {
    /**
     * 动态代理这个方法 method_name args做成一个 key 用switch调用真实的逻辑
     *
     * @param methodName 方法名
     * @param args       参数值
     * @return 返回结果
     */
    public abstract Object invoke(String methodName, Object[] args);


    public static String encode(String methodName) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(methodName.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash).substring(0, 16);
        } catch (Exception e) {
            throw new KeyException("摘要失败", e);
        }
    }

    protected static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

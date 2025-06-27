package classloader;

import com.raito.rpc.exception.KeyException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @author cn
 * @since 2025/6/27 18:06
 * @version 1.0
 */
public abstract class RpcProxyClass {
    protected final Object targetInstance;

    public RpcProxyClass(Object targetInstance) {
        this.targetInstance = targetInstance;
    }

    /**
     * 动态代理这个方法 method_name args做成一个 key 用switch调用真实的逻辑
     * @param returnType 方法返回类型
     * @param methodName 方法名
     * @param argTypes 方法参数
     * @param args 参数值
     * @return 返回结果
     */
    public abstract Object invoke(String returnType, String methodName, String[] argTypes, Object[] args);


    protected final String encode(String returnType, String methodName, String[] argTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append("returnType=").append(returnType)
                .append(",methodName=").append(methodName);
        for (var argType : argTypes) {
            sb.append(",argType=").append(argType);
        }
        String originalString = sb.toString();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(originalString.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new KeyException("摘要失败", e);
        }
    }

    protected final String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

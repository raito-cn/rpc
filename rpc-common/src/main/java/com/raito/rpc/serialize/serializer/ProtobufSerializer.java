package com.raito.rpc.serialize.serializer;

import com.google.protobuf.Message;
import com.raito.rpc.protocol.RpcRequest;
import com.raito.rpc.protocol.RpcResponse;

import java.lang.reflect.Method;

/**
 * @author cn
 * @since 2025/6/25 10:45
 * @version 1.0
 */
public class ProtobufSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        Message message;
        if (obj instanceof RpcRequest) {
            RpcRequest request = (RpcRequest) obj;

        } else if (obj instanceof RpcResponse) {

        } else {

        }
        return ((Message) obj).toByteArray();
    }

    @SuppressWarnings("all")
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            Method parseFrom = clazz.getMethod("parseFrom", byte[].class);
            return (T) parseFrom.invoke(null, bytes);
        } catch (Exception e) {
            throw new RuntimeException("Protobuf 反序列化失败: " + clazz.getName(), e);
        }
    }
}

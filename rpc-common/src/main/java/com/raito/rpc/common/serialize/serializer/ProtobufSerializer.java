package com.raito.rpc.common.serialize.serializer;

import com.google.protobuf.Message;
import com.raito.rpc.common.util.JsonUtils;
import com.raito.rpc.common.exception.SerialException;
import com.raito.rpc.common.protocol.RpcRequest;
import com.raito.rpc.common.protocol.RpcResponse;
import protocol.RpcRequestProto;
import protocol.RpcResponseProto;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cn
 * @version 1.0
 * @since 2025/6/25 10:45
 */
public class ProtobufSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        Message message;
        switch (obj) {
            case RpcRequest request -> {
                RpcRequestProto.RpcRequest.Builder builder = RpcRequestProto.RpcRequest.newBuilder()
                        .setClassName(request.getClassName())
                        .setMethodName(request.getMethodName())
                        .setReturnType(request.getReturnType());

                List<String> paramTypeNames;
                if (request.getArgs() != null) {
                    paramTypeNames = Arrays.stream(request.getParamTypes())
                            .collect(Collectors.toList());
                } else {
                    paramTypeNames = List.of();
                }
                builder.addAllParamTypes(paramTypeNames);

                List<String> argValues;
                if (request.getArgs() != null) {
                    argValues = Arrays.stream(request.getArgs())
                            .map(arg -> (arg instanceof String) ? (String) arg : JsonUtils.toJson(arg))
                            .collect(Collectors.toList());
                } else {
                    argValues = List.of();
                }
                builder.addAllArgs(argValues);
                message = builder.build();
            }
            case RpcResponse response -> message = RpcResponseProto.RpcResponse.newBuilder()
                    .setResult(JsonUtils.toJson(response.getResult()) == null ? "" : JsonUtils.toJson(response.getResult()))
                    .setErrorMessage(response.getErrorMessage() == null ? "" : response.getErrorMessage())
                    .setSuccess(response.isSuccess())
                    .build();
            case Message msg -> message = msg;
            case null -> throw new SerialException("对象为空");
            default -> throw new SerialException("不支持的对象类型: " + obj.getClass().getName());
        }
        return message.toByteArray();
    }

    @SuppressWarnings("all")
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (!Message.class.isAssignableFrom(clazz)) {
            throw new SerialException("反序列化的类型必须是 Protobuf Message 类型");
        }
        try {
            Method parseFrom = clazz.getMethod("parseFrom", byte[].class);
            return (T) parseFrom.invoke(null, bytes);
        } catch (Exception e) {
            throw new SerialException("Protobuf 反序列化失败: " + clazz.getName(), e);
        }
    }
}

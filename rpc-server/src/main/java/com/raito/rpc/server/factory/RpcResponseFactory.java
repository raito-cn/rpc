package com.raito.rpc.server.factory;

import com.raito.rpc.common.codec.RpcDecoderWrapper;
import com.raito.rpc.common.exception.SerialException;
import com.raito.rpc.common.protocol.RpcRequest;
import com.raito.rpc.server.helper.InvokeHelper;
import com.raito.rpc.server.strategy.RpcRemoteStrategy;
import protocol.RpcRequestProto;

/**
 * @author raito
 * @since 2025/6/26
 */
public class RpcResponseFactory {
    public static <U> Object createRpcResponse(RpcDecoderWrapper msg, RpcRemoteStrategy<U> strategy) {
        InvokeHelper invokeHelper = getInvokeHelper(msg);
        return strategy.getResult(invokeHelper);
    }

    private static InvokeHelper getInvokeHelper(RpcDecoderWrapper msg) {
        Object obj = msg.getBody();
        if (obj instanceof RpcRequestProto.RpcRequest request) {
            return new InvokeHelper(request.getClassName(), request.getReturnType(), request.getMethodName(), request.getParamTypesList().toArray(new String[0]),
                    request.getArgsList().toArray(new String[0]));
        } else if (obj instanceof RpcRequest request) {
            return new InvokeHelper(request.getClassName(), request.getReturnType(), request.getMethodName(), request.getParamTypes(), request.getArgs());
        } else {
            throw new SerialException("不支持的协议");
        }
    }
}

package com.raito.rpc.server.strategy;

import protocol.RpcResponseProto;

/**
 * @author raito
 * @since 2025/6/29
 */
public class RpcRequestProtoRemoteStrategy extends RpcRemoteStrategy<RpcResponseProto.RpcResponse> {
    @Override
    protected RpcResponseProto.RpcResponse ok(Object result) {
        return RpcResponseProto.RpcResponse.newBuilder()
                .setSuccess(true)
                .setResult(result == null ? "" : result.toString())
                .setCode(200)
                .build();
    }

    @Override
    protected RpcResponseProto.RpcResponse error(String errorMessage) {
        return RpcResponseProto.RpcResponse.newBuilder()
                .setSuccess(false)
                .setResult(errorMessage)
                .setCode(500)
                .build();
    }

}

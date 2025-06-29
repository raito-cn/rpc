package com.raito.rpc.server.strategy;

import com.raito.rpc.common.protocol.RpcResponse;

/**
 * @author raito
 * @since 2025/6/29
 */
@SuppressWarnings("unused")
public class RpcRequestRemoteStrategy extends RpcRemoteStrategy<RpcResponse> {
    @Override
    protected RpcResponse ok(Object result) {
        return RpcResponse.builder()
                .success(true)
                .result(result)
                .errorMessage(null)
                .code(200)
                .build();
    }

    @Override
    protected RpcResponse error(String errorMessage) {
        return RpcResponse.builder()
                .success(false)
                .result(null)
                .code(500)
                .errorMessage(errorMessage)
                .build();
    }

}

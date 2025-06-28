package com.raito.rpc.server.factory;

import com.raito.rpc.common.codec.RpcDecoderWrapper;
import com.raito.rpc.common.protocol.RpcResponse;

/**
 * @author raito
 * @since 2025/6/26
 */
public class RpcResponseFactory {
    // todo 通过调用本地方法返回消息
    public static RpcResponse createRpcResponse(RpcDecoderWrapper msg) {
        return new RpcResponse();
    }
}

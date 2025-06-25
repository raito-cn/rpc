package com.raito.rpc.codec;

import com.raito.rpc.protocol.RpcProtocol;
import lombok.Builder;
import lombok.Data;

/**
 * @author cn
 * @since 2025/6/25 14:50
 * @version 1.0
 */
@Data
@Builder
public class RpcDecoderWrapper {
    private RpcProtocol protocol; // 原始协议（含 payload）
    private Object body;

    @Override
    public String toString() {
        return "RpcDecoderWrapper{" +
                "protocol=" + protocol +
                ", body=" + body +
                '}';
    }
}

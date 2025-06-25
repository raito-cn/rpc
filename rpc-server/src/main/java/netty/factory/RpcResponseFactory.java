package netty.factory;

import com.raito.rpc.codec.RpcDecoderWrapper;
import com.raito.rpc.protocol.RpcResponse;

/**
 * @author raito
 * @since 2025/6/26
 */
public class RpcResponseFactory {
    public static RpcResponse createRpcResponse(RpcDecoderWrapper msg) {
        return new RpcResponse();
    }
}

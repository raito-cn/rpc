package factory;

import com.raito.rpc.codec.RpcDecoderWrapper;
import com.raito.rpc.protocol.RpcResponse;

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

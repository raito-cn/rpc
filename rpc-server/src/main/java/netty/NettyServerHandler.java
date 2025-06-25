package netty;

import com.raito.rpc.codec.RpcDecoderWrapper;
import com.raito.rpc.codec.RpcEncoderWrapper;
import com.raito.rpc.constant.RpcConstant;
import com.raito.rpc.protocol.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import netty.factory.RpcResponseFactory;

/**
 * @author raito
 * @since 2025/6/25
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcDecoderWrapper> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcDecoderWrapper msg) throws Exception {
        log.debug("[{}]: 收到消息:{}", ctx.channel().id(), msg.toString());
        beforeReceive(ctx);
        RpcResponse rpcResponse = RpcResponseFactory.createRpcResponse(msg);
        RpcEncoderWrapper wrapper = RpcEncoderWrapper.builder()
                .magic(RpcConstant.MAGIC)
                .version((byte) 1)
                .requestId(msg.getProtocol().getRequestId())
                .mesType((byte) 1)
                .codecType((byte) 1)
                .compressType((byte) 2)
                .body(rpcResponse)
                .build();
        ctx.writeAndFlush(wrapper);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("服务端连接激活：{}, channel: {}", ctx.channel().remoteAddress(), ctx.channel().id());
        super.channelActive(ctx);
        afterActive(ctx);
    }

    /**
     * 收到消息前 更新channel状态
     * @param ctx ctx
     */
    private void beforeReceive(ChannelHandlerContext ctx) {

    }

    /**
     * 持续管理channel 如30min未有新的消息 则销毁
     * @param ctx ctx
     */
    private void afterActive(ChannelHandlerContext ctx) {

    }
}

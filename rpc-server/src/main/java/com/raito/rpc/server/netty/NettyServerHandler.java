package com.raito.rpc.server.netty;

import com.raito.rpc.common.codec.RpcDecoderWrapper;
import com.raito.rpc.common.codec.RpcEncoderWrapper;
import com.raito.rpc.common.constant.RpcConstant;
import com.raito.rpc.server.strategy.RpcRemoteStrategy;
import com.raito.rpc.server.strategy.RpcRequestProtoRemoteStrategy;
import com.raito.rpc.server.thread.AsyncThread;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import com.raito.rpc.server.factory.RpcResponseFactory;
import protocol.RpcResponseProto;

/**
 * @author raito
 * @since 2025/6/25
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcDecoderWrapper> {
    public static final RpcRemoteStrategy<RpcResponseProto.RpcResponse> DEFAULT_RPC_REMOTE_STRATEGY = new RpcRequestProtoRemoteStrategy();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcDecoderWrapper msg) {
        Channel channel = ctx.channel();
        log.info("[{}]: 收到消息:{}", channel.id(), msg.toString());
        async(msg, channel);
    }

    private static void async(RpcDecoderWrapper msg, Channel channel) {
        AsyncThread.getNettyAsyncThread()
                .execute(() -> {
                    try {
                        Object rpcResponse = RpcResponseFactory.createRpcResponse(msg, DEFAULT_RPC_REMOTE_STRATEGY);
                        RpcEncoderWrapper wrapper = RpcEncoderWrapper.builder()
                                .magic(RpcConstant.MAGIC)
                                .version((byte) 1)
                                .requestId(msg.getProtocol().getRequestId())
                                .mesType((byte) 1)
                                .codecType((byte) 1)
                                .compressType((byte) 2)
                                .body(rpcResponse)
                                .build();
                        if (channel.isActive() && channel.isWritable()) {
                            channel.writeAndFlush(wrapper);
                            log.info("响应消息:{}", wrapper);
                        } else {
                            log.info("通道不可写或已关闭，不能写数据");
                        }
                    } catch (Exception e) {
                        log.error("异步处理请求失败: {}", msg, e);
                    }
                });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("服务端连接激活：{}, channel: {}", ctx.channel().remoteAddress(), ctx.channel().id());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("客户端连接断开: {}", ctx.channel().id());
        // 可清理上下文资源，比如请求上下文 ThreadLocal 等
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("客户端连接异常: {}", ctx.channel().id(), cause);
        ctx.close(); // 必须关闭，防止 channel 泄漏
    }
}

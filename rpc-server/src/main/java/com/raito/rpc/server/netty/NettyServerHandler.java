package com.raito.rpc.server.netty;

import com.raito.rpc.common.codec.RpcDecoderWrapper;
import com.raito.rpc.common.codec.RpcEncoderWrapper;
import com.raito.rpc.common.constant.RpcConstant;
import com.raito.rpc.common.util.MessageUtils;
import com.raito.rpc.server.factory.BeanFactory;
import com.raito.rpc.server.helper.SeedMessageHelper;
import com.raito.rpc.server.strategy.RpcRemoteStrategy;
import com.raito.rpc.server.strategy.RpcRequestProtoRemoteStrategy;
import com.raito.rpc.server.thread.AsyncThread;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import com.raito.rpc.server.factory.RpcResponseFactory;
import protocol.RpcResponseProto;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
        log.info("[{}]: 收到消息:{}", channel.id(), MessageUtils.decodeOctalEscapes(msg.getBody()));
        async(msg, channel);
    }

    private static void async(RpcDecoderWrapper msg, Channel channel) {
        // 异步执行 但是需要有一个超时兜底机制，如超过60s 直接返回响应超时，并且就算是用虚拟线程，也需要做netty限流，防止请求过多打爆JVM
        SeedMessageHelper helper = new SeedMessageHelper();
        Future<?> submit = AsyncThread.getNettyAsyncThread()
                .submit(() -> seedMessage(msg, channel, helper));
        // 超时兜底
        AsyncThread.getNettyAsyncThread().submit(() -> checkTimeout(msg, channel, submit, helper));
    }

    private static void checkTimeout(RpcDecoderWrapper msg, Channel channel, Future<?> submit, SeedMessageHelper helper) {
        try {
            submit.get(BeanFactory.getBean(AsyncThread.ThreadProperties.class).getTimeout(), TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            helper.setSeed((short) 1);
            submit.cancel(true); // 中断任务
            RpcEncoderWrapper timeoutResp = RpcEncoderWrapper.builder()
                    .magic(RpcConstant.MAGIC)
                    .version((byte) 1)
                    .requestId(msg.getProtocol().getRequestId())
                    .mesType((byte) 1)
                    .codecType((byte) 1)
                    .compressType((byte) 2)
                    .body(RpcResponseProto.RpcResponse.newBuilder()
                            .setCode(504)
                            .setSuccess(false)
                            .setResult("请求处理超时")
                            .build()
                    )
                    .build();
            if (channel.isActive() && channel.isWritable()) {
                channel.writeAndFlush(timeoutResp);
            }
            log.warn("请求处理超时，已发送超时响应");
        } catch (Exception e) {
            log.error("请求处理过程中发生异常", e);
        }
    }

    @SuppressWarnings("all")
    private static void seedMessage(RpcDecoderWrapper msg, Channel channel, SeedMessageHelper helper) {
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
                if (helper == null || helper.getSeed() == (short) 1) {
                    log.warn("消息已超时，不再响应!");
                } else {
                    channel.writeAndFlush(wrapper);
                    log.info("响应消息:{}", wrapper);
                }
            } else {
                log.info("通道不可写或已关闭，不能写数据");
            }
        } catch (Exception e) {
            log.error("异步处理请求失败: {}", msg, e);
        } finally {
            helper = null;
        }
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

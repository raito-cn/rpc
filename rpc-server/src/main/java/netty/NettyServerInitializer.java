package netty;

import com.raito.rpc.protocol.RpcDecoder;
import com.raito.rpc.protocol.RpcEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author cn
 * @since 2025/6/26 10:29
 * @version 1.0
 */
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        // 空闲检测 5 分钟
        p.addLast(new IdleStateHandler(60 * 5, 0, 0, TimeUnit.SECONDS))
                .addLast(new RpcDecoder())
                .addLast(new RpcEncoder())
                .addLast(new HeartbeatHandler())
                .addLast(new NettyServerHandler());
    }
}

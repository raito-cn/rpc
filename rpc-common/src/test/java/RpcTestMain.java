import com.raito.rpc.codec.RpcDecoderWrapper;
import com.raito.rpc.codec.RpcEncoderWrapper;
import com.raito.rpc.constant.RpcConstant;
import com.raito.rpc.protocol.RpcDecoder;
import com.raito.rpc.protocol.RpcEncoder;
import com.raito.rpc.protocol.RpcRequest;
import com.raito.rpc.protocol.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;

/**
 * @author cn
 * @since 2025/6/25 18:17
 * @version 1.0
 */
public class RpcTestMain {
    public static void main(String[] args) throws InterruptedException {
        int port = 9000;

        // 启动 Netty 服务端
        new Thread(() -> {
            try {
                startRpcServer(port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // 睡一秒等服务端启动完
        Thread.sleep(1000);

        // 启动 Netty 客户端并发送 RpcRequest
        startRpcClient("localhost", port);
    }

    public static void startRpcServer(int port) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new RpcDecoder()); // 你的解码器
                        ch.pipeline().addLast(new RpcEncoder()); // 你的编码器
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<RpcDecoderWrapper>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, RpcDecoderWrapper msg) {
                                String s = "服务端收到请求: " + msg;
                                System.out.println(s);
                                RpcResponse rpcResponse = RpcResponse
                                        .builder()
                                        .success(true)
                                        .result(s)
                                        .errorMessage(null)
                                        .build();
                                RpcEncoderWrapper wrapper = RpcEncoderWrapper.builder()
                                        .magic(RpcConstant.MAGIC)
                                        .version((byte) 1)
                                        .requestId(new Date().getTime())
                                        .mesType((byte) 1)
                                        .codecType((byte) 1)
                                        .compressType((byte) 2)
                                        .body(rpcResponse)
                                        .build();
                                ctx.writeAndFlush(wrapper);
                            }
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("服务端连接激活：" + ctx.channel().remoteAddress());
                                super.channelActive(ctx);
                            }
                        });
                    }
                });

        ChannelFuture f = bootstrap.bind(port).sync();
        System.out.println("服务端启动完成");
        f.channel().closeFuture().sync();
    }

    public static void startRpcClient(String host, int port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new RpcDecoder()); // 你的解码器
                        ch.pipeline().addLast(new RpcEncoder()); // 你的编码器
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<RpcDecoderWrapper>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, RpcDecoderWrapper msg) {
                                System.out.println("客户端收到响应: " + msg);
                            }

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) {
                                RpcRequest req = RpcRequest.builder()
                                        .className("TestService")
                                        .methodName("sayHello")
                                        .paramTypes(new Class<?>[]{String.class, Integer.class})
                                        .args(new Object[]{"hello", 123})
                                        .build();
                                RpcEncoderWrapper wrapper = RpcEncoderWrapper.builder()
                                        .magic(RpcConstant.MAGIC)
                                        .version((byte) 1)
                                        .requestId(new Date().getTime())
                                        .mesType((byte) 0)
                                        .codecType((byte) 1)
                                        .compressType((byte) 2)
                                        .body(req)
                                        .build();
                                ctx.writeAndFlush(wrapper);
                            }
                        });
                    }
                });
        ChannelFuture f = bootstrap.connect(host, port).sync();
        f.channel().closeFuture().sync();
    }
}

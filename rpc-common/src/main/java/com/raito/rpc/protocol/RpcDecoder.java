package com.raito.rpc.protocol;

import com.raito.rpc.codec.RpcDecoderWrapper;
import com.raito.rpc.constant.RpcConstant;
import com.raito.rpc.exception.RpcDecodeException;
import com.raito.rpc.serialize.compressor.Compressor;
import com.raito.rpc.serialize.enums.CodecType;
import com.raito.rpc.serialize.enums.CompressType;
import com.raito.rpc.serialize.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author cn
 * @since 2025/6/25 15:58
 * @version 1.0
 */
@Slf4j
public class RpcDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) {
        try {
            if (in.readableBytes() < RpcConstant.RPC_HEADER_LENGTH) {
                return;
            }

            in.markReaderIndex();
            short magic = in.readShort();
            if (magic != RpcConstant.MAGIC) {
                throw new RpcDecodeException("Magic number error");
            }
            byte version = in.readByte();
            byte codecType = in.readByte();
            byte compressType = in.readByte();
            byte messageType = in.readByte();
            long requestId = in.readLong();
            int payloadLength = in.readInt();

            if (in.readableBytes() < payloadLength) {
                in.resetReaderIndex();
                return;
            }
            byte[] payload = new byte[payloadLength];
            in.readBytes(payload);

            RpcProtocol protocol = RpcProtocol.builder()
                    .magic(magic)
                    .version(version)
                    .mesType(messageType)
                    .codecType(codecType)
                    .compressType(compressType)
                    .requestId(requestId)
                    .payloadLength(payloadLength)
                    .payload(payload)
                    .build();

            Compressor compressor = CompressType.valueOf(compressType).getCompressor();
            byte[] bytes = compressor.decompress(payload);
            Serializer serializer = CodecType.valueOf(codecType).getSerializer();
            Object body = serializer.deserialize(bytes, getBodyClass(messageType));
            RpcDecoderWrapper wrapper = RpcDecoderWrapper.builder()
                    .body(body)
                    .protocol(protocol)
                    .build();
            list.add(wrapper);
        } catch (Exception e) {
            ctx.close();
            log.error(e.getMessage(), e);
        }
    }

    private Class<?> getBodyClass(byte messageType) {
        return messageType == 0 ? RpcRequest.class : RpcResponse.class;
    }
}

package com.raito.rpc.common.protocol;

import com.raito.rpc.common.codec.RpcEncoderWrapper;
import com.raito.rpc.common.serialize.compressor.Compressor;
import com.raito.rpc.common.serialize.enums.CodecType;
import com.raito.rpc.common.serialize.enums.CompressType;
import com.raito.rpc.common.serialize.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author cn
 * @since 2025/6/25 16:07
 * @version 1.0
 */
@Slf4j
public class RpcEncoder extends MessageToByteEncoder<RpcEncoderWrapper> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcEncoderWrapper msg, ByteBuf out) {
        try {
            out.writeShort(msg.getMagic());
            out.writeByte(msg.getVersion());
            out.writeByte(msg.getCodecType());
            out.writeByte(msg.getCompressType());
            out.writeByte(msg.getMesType());
            out.writeLong(msg.getRequestId());

            Serializer serializer = CodecType.valueOf(msg.getCodecType()).getSerializer();
            byte[] bytes = serializer.serialize(msg.getBody());
            Compressor compressor = CompressType.valueOf(msg.getCompressType()).getCompressor();
            byte[] compress = compressor.compress(bytes);

            out.writeInt(compress.length);
            out.writeBytes(compress);
        } catch (Exception e) {
            log.error("encode error", e);
        }
    }
}

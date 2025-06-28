package com.raito.rpc.common.serialize.compressor;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import java.nio.ByteBuffer;

/**
 * @author cn
 * @since 2025/6/25 13:51
 * @version 1.0
 */
public class Lz4Compressor implements Compressor {
    private final LZ4Factory factory = LZ4Factory.fastestInstance();

    @Override
    public byte[] compress(byte[] data) {
        LZ4Compressor compressor = factory.fastCompressor();
        int maxLength = compressor.maxCompressedLength(data.length);
        byte[] compressed = new byte[maxLength];
        int compressedLength = compressor.compress(data, 0, data.length, compressed, 0, maxLength);

        // 构造带长度头的数组
        ByteBuffer buffer = ByteBuffer.allocate(4 + compressedLength);
        buffer.putInt(data.length); // 前4字节是原始长度
        buffer.put(compressed, 0, compressedLength);
        return buffer.array();
    }

    @Override
    public byte[] decompress(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int originalLength = buffer.getInt(); // 前4字节读取原始长度
        byte[] compressed = new byte[buffer.remaining()];
        buffer.get(compressed);

        byte[] restored = new byte[originalLength];
        LZ4FastDecompressor decompressor = factory.fastDecompressor();
        decompressor.decompress(compressed, 0, restored, 0, originalLength);
        return restored;
    }
}

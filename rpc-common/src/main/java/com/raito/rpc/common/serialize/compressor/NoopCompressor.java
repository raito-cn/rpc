package com.raito.rpc.common.serialize.compressor;

/**
 * @author cn
 * @since 2025/6/25 13:40
 * @version 1.0
 */
public class NoopCompressor implements Compressor {
    @Override
    public byte[] compress(byte[] data) {
        return data;
    }

    @Override
    public byte[] decompress(byte[] data) {
        return data;
    }
}

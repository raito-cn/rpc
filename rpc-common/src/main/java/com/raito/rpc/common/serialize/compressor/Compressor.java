package com.raito.rpc.common.serialize.compressor;

/**
 * @author cn
 * @since 2025/6/25 13:39
 * @version 1.0
 */
public interface Compressor {
    byte[] compress(byte[] data);

    byte[] decompress(byte[] data);
}

package com.raito.rpc.common.serialize.enums;

import com.raito.rpc.common.serialize.compressor.Compressor;
import com.raito.rpc.common.serialize.compressor.Lz4Compressor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.raito.rpc.common.serialize.compressor.GzipCompressor;
import com.raito.rpc.common.serialize.compressor.NoopCompressor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cn
 * @since 2025/6/25 13:47
 * @version 1.0
 */
@RequiredArgsConstructor
@Getter
public enum CompressType {
    NONE((byte) 0, new NoopCompressor()),
    GZIP((byte) 1, new GzipCompressor()),
    LZ4((byte) 2, new Lz4Compressor());

    private final byte code;
    private final Compressor compressor;

    private static final Map<Byte, CompressType> COMPRESS_TYPE_MAP = new HashMap<>();
    static {
        for (CompressType compressType : CompressType.values()) {
            COMPRESS_TYPE_MAP.put(compressType.getCode(), compressType);
        }
    }

    public static CompressType valueOf(byte code) {
        return COMPRESS_TYPE_MAP.get(code);
    }
}

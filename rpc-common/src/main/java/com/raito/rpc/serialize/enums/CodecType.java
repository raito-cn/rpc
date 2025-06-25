package com.raito.rpc.serialize.enums;

import com.raito.rpc.serialize.serializer.Serializer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.raito.rpc.serialize.serializer.JsonSerializer;
import com.raito.rpc.serialize.serializer.ProtobufSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cn
 * @since 2025/6/25 10:26
 * @version 1.0
 */
@RequiredArgsConstructor
@Getter
public enum CodecType {
    PROTOBUF((byte) 1, new ProtobufSerializer()),
    JSON((byte) 2, new JsonSerializer()),
    ;

    private final byte code;
    private final Serializer serializer;

    private static final Map<Byte, CodecType> CODEC_TYPE_MAP = new HashMap<>();
    static {
        for (CodecType codecType : CodecType.values()) {
            CODEC_TYPE_MAP.put(codecType.getCode(), codecType);
        }
    }

    public static CodecType valueOf(byte code) {
        return CODEC_TYPE_MAP.get(code);
    }
}

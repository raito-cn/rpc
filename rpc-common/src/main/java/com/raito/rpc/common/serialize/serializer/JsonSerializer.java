package com.raito.rpc.common.serialize.serializer;

import com.raito.rpc.common.util.JsonUtils;

/**
 * @author cn
 * @since 2025/6/25 10:45
 * @version 1.0
 */
public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        return JsonUtils.toBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return JsonUtils.fromBytes(bytes, clazz);
    }
}

package com.raito.rpc.serialize.serializer;

/**
 * @author cn
 * @since 2025/6/25 10:27
 * @version 1.0
 */
public interface Serializer {
    byte[] serialize(Object obj);

    <T> T deserialize(byte[] bytes, Class<T> clazz);
}

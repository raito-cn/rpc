package com.raito.rpc.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author cn
 * @since 2025/6/25 12:11
 * @version 1.0
 */
@Slf4j
@SuppressWarnings("unused")
public class JsonUtils {
    private static final ObjectMapper om = new ObjectMapper();
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        // 1. 配置 Long 转 String
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, ToStringSerializer.instance);
        om.registerModule(module);

        // 2. 配置 LocalDate 模块
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(LOCAL_DATE_TIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(LOCAL_DATE_TIME_FORMATTER));

        om.registerModule(javaTimeModule);

        // 3. 配置时间字段的格式化（例如：yyyy-MM-dd）
        om.setDateFormat(DATE_FORMAT);

        // 4. 配置解析时忽略未知属性
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // 将对象转换为 JSON 字符串
    public static String toJson(Object object) {
        try {
            return om.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    // 将 JSON 字符串转换为指定类型的对象
    public static <T> T fromJson(String json, Class<T> valueType) {
        try {
            return om.readValue(json, valueType);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> reference) {
        try {
            return om.readValue(json, reference);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    // 将对象转换为 Map
    public static Map<String, Object> toMap(Object object) {
        try {
            return om.convertValue(object, new TypeReference<>() {
            });
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static Map<String, Object> fromJsonToMap(String json) {
        try {
            return om.readValue(json, new TypeReference<>() {
            });
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static JsonNode fromJsonToNode(String json) {
        try {
            return om.readTree(json);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static byte[] toBytes(Object object) {
        try {
            return om.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static <T> T fromBytes(byte[] bytes, Class<T> valueType) {
        try {
            return om.readValue(bytes, valueType);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}

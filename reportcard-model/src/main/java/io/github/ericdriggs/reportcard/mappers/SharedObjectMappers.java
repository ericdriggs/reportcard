package io.github.ericdriggs.reportcard.mappers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum SharedObjectMappers {

    ;//static members only

    private final static Logger log = LoggerFactory.getLogger(SharedObjectMappers.class);

    public final static ObjectMapper sortedObjectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(new JavaTimeModule());

    public final static ObjectMapper simpleObjectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public final static ObjectMapper ignoreUnknownObjectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(new JavaTimeModule());

    public static final ObjectMapper permissiveObjectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .build();

    @SuppressWarnings("unused")
    @SneakyThrows(JsonProcessingException.class)
    public static <T> T readValue(String json, @SuppressWarnings("rawtypes") Class clazz) {
        //noinspection unchecked
        return (T) permissiveObjectMapper.readValue(json, clazz);
    }

    public static <T> T readValueOrDefault(String json, @SuppressWarnings("rawtypes") Class clazz, T tDefault) {
        try {//noinspection unchecked
            return (T) permissiveObjectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.warn("unable to parse json: {}, returning default: {}", json, tDefault, e);
            return tDefault;
        }
    }

    public static <T> String writeValueAsString(T obj) {
        return writeValueAsString(obj, true);
    }

    public static <T> String writeValueAsString(T obj, boolean shouldPrettyPrint) {

        if (obj == null) {
            return null;
        }
        try {
            if (shouldPrettyPrint) {
                return permissiveObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            } else {
                return permissiveObjectMapper.writeValueAsString(obj);
            }
        } catch (JsonProcessingException e) {
            log.error("Unable to write as json. obj: {} ", obj, e);
        }
        return "{}";
    }
}

package io.github.ericdriggs.reportcard.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import lombok.SneakyThrows;

public enum PrettyPrintUtil {
    ;//static methods only

    @SneakyThrows(JsonProcessingException.class)
    public static String sorted(String json) {
        ObjectMapper objectMapper = SharedObjectMappers.sortedObjectMapper;
        Object jsonObject = objectMapper.readValue(json, Object.class);
        return objectMapper.writeValueAsString(jsonObject);
    }

    @SneakyThrows(JsonProcessingException.class)
    public static String sortedPrettyPrint(String json) {
        ObjectMapper objectMapper = SharedObjectMappers.sortedObjectMapper;
        Object jsonObject = objectMapper.readValue(json, Object.class);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
    }

}

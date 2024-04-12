package io.github.ericdriggs.reportcard.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import lombok.SneakyThrows;

//TODO: remove this or refactor to pretty print in html
public enum PrettyPrintUtil {
    ;//static methods only

    @SneakyThrows(JsonProcessingException.class)
    public static String prettyPrint(String json) {
        ObjectMapper objectMapper = SharedObjectMappers.sortedObjectMapper;
        Object jsonObject = objectMapper.readValue(json, Object.class);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
    }


}

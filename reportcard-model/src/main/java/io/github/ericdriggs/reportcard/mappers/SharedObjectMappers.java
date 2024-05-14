package io.github.ericdriggs.reportcard.mappers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public enum SharedObjectMappers {
    ;//static members only
    public final static ObjectMapper sortedObjectMapper = new ObjectMapper()
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .registerModule(new JavaTimeModule());
    public final static ObjectMapper simpleObjectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    public final static ObjectMapper ignoreUnknownObjectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
            .registerModule(new JavaTimeModule());;
}

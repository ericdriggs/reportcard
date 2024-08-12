package io.github.ericdriggs.reportcard.util.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.jooq.Condition;
import org.jooq.Field;

import java.util.Map;

import static org.jooq.impl.DSL.condition;

public enum SqlJsonUtil {
    ;//static methods only
    private final static ObjectMapper mapper = SharedObjectMappers.simpleObjectMapper;

    public static String jobInfoEqualsJson(Map<String, String> jobInfo) {
        return jobInfoEqualsJson(toJson(jobInfo));
    }

    public static String jobInfoEqualsJson(String jobInfoJson) {
        return fieldEqualsJson("job_info", jobInfoJson);
    }

    public static String fieldEqualsJson(String fieldName, String json) {
        if (ObjectUtils.isEmpty(json)) {
            return "true";
        }
        return " " + fieldName + " = CAST('" + json + "' AS JSON ) ";
    }
    public static String fieldNotEqualsJson(String fieldName, String json) {
        if (ObjectUtils.isEmpty(json)) {
            return "true";
        }
        return " " + fieldName + " != CAST('" + json + "' AS JSON ) ";
    }

    @SneakyThrows(JsonProcessingException.class)
    protected static String toJson(Map<String, String> map) {
        return mapper.writeValueAsString(map);
    }

    public static Condition jsonNotEqualsCondition(Field<?> field, String json) {
        return condition(SqlJsonUtil.fieldNotEqualsJson(field.getName(), json));
    }
}

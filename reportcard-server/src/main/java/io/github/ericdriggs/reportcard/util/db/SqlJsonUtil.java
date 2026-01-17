package io.github.ericdriggs.reportcard.util.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.Condition;
import org.jooq.Field;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

import static io.github.ericdriggs.reportcard.gen.db.Tables.JOB;
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

    public static String jobInfoRequiredExcluded(Map<String, List<String>> required, Map<String, List<String>> excluded) {
        if (CollectionUtils.isEmpty(required) && CollectionUtils.isEmpty(excluded)) {
            return "true";
        }
        throw new UnsupportedOperationException("TODO");
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

    public static Condition jobInfoContainsKeyValue(String key, String value) {
        if (ObjectUtils.isEmpty(key) || ObjectUtils.isEmpty(value)) {
            return condition("true");
        }
        
        // Use MySQL JSON_EXTRACT for both exact match and wildcards
        if (value.contains("%")) {
            // Wildcard search - use JSON_EXTRACT with LIKE
            return condition("JSON_EXTRACT(job_info, '$." + key + "') LIKE '" + value + "'");
        } else {
            // Exact match - use JSON_EXTRACT with =
            return condition("JSON_EXTRACT(job_info, '$." + key + "') = '" + value + "'");
        }
    }

    public static Condition jsonNotEqualsCondition(Field<?> field, String json) {
        return condition(SqlJsonUtil.fieldNotEqualsJson(field.getName(), json));
    }
}

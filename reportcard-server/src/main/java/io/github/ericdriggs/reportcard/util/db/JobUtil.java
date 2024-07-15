package io.github.ericdriggs.reportcard.util.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Map;

public enum JobUtil {
    ;//static methods only
    private final static ObjectMapper mapper = SharedObjectMappers.simpleObjectMapper;

    public static String getJobInfoSqlClause(Map<String, String> jobInfo) {
        return getJobInfoSqlClause(toJson(jobInfo));
    }

    public static String getJobInfoSqlClause(String jobInfoJson) {
        if (ObjectUtils.isEmpty(jobInfoJson)) {
            return "true";
        }
        return " job_info = CAST('" + jobInfoJson + "' AS JSON ) ";
    }

    @SneakyThrows(JsonProcessingException.class)
    protected static String toJson(Map<String, String> map) {
        return mapper.writeValueAsString(map);
    }

}

package io.github.ericdriggs.reportcard.model.converter.karate;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses native Karate report json (*.karate-json.txt) with a structural allowlist:
 * only packageQualifiedName/relativePath/scenarioResults(name,line,startTime,endTime) are
 * ever read. Fields like stepResults/callResults/callArg/stepLog (which can carry
 * credentials passed as call arguments) are never bound and never surface.
 */
@Slf4j
public enum KarateNativeReportParser {
    ; //static methods only

    public static KarateNativeFeature parse(String nativeJson) {
        if (nativeJson == null || nativeJson.isBlank()) {
            return null;
        }
        try {
            JsonNode root = SharedObjectMappers.ignoreUnknownObjectMapper.readTree(nativeJson);
            List<KarateNativeScenario> scenarios = new ArrayList<>();
            for (JsonNode scenarioNode : root.path("scenarioResults")) {
                Long[] normalized = normalizeEpochPair(longOrNull(scenarioNode, "startTime"), longOrNull(scenarioNode, "endTime"));
                scenarios.add(KarateNativeScenario.builder()
                        .name(textOrNull(scenarioNode, "name"))
                        .line(intOrNull(scenarioNode, "line"))
                        .startTime(normalized[0])
                        .endTime(normalized[1])
                        .build());
            }
            return KarateNativeFeature.builder()
                    .packageQualifiedName(textOrNull(root, "packageQualifiedName"))
                    .relativePath(textOrNull(root, "relativePath"))
                    .scenarios(scenarios)
                    .build();
        } catch (Exception e) {
            log.warn("Failed to parse native karate json, skipping", e);
            return null;
        }
    }

    private static String textOrNull(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }

    private static Integer intOrNull(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asInt();
    }

    private static Long longOrNull(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asLong();
    }

    static Long[] normalizeEpochPair(Long start, Long end) {
        if (start == null || end == null) {
            return new Long[]{null, null};
        }
        if (start <= 0 || end <= 0) {
            return new Long[]{null, null};
        }
        if (end < start) {
            return new Long[]{null, null};
        }
        return new Long[]{start, end};
    }
}

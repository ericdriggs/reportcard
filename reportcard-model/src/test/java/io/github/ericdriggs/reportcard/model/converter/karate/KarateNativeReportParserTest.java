package io.github.ericdriggs.reportcard.model.converter.karate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KarateNativeReportParserTest {

    static final String SECRET = "SECRET-PASSWORD-XYZ-do-not-persist";

    static final String NATIVE_JSON = """
        {
          "packageQualifiedName": "synthetic-feature-one",
          "relativePath": "synthetic-feature-one.feature",
          "prefixedPath": "classpath:synthetic-feature-one.feature",
          "name": "synthetic feature title",
          "durationMillis": 0.0,
          "resultDate": "2026-03-18 12:50:52 PM",
          "scenarioResults": [
            {
              "name": "synthetic scenario one",
              "line": 3,
              "exampleIndex": -1,
              "sectionIndex": 0,
              "executorName": "pool-1-thread-1",
              "startTime": 1773863417611,
              "endTime": 1773863452232,
              "durationMillis": 0.0,
              "failed": true,
              "tags": ["env=synthetic-region-1"],
              "stepResults": [
                {
                  "step": {"text": "call read('classpath:helper.feature')"},
                  "stepLog": "%s",
                  "callResults": [{"callArg": {"password": "%s"}}]
                }
              ]
            }
          ]
        }
        """.formatted(SECRET, SECRET);

    @Test
    void parse_extractsAllowlistedFieldsOnly() throws Exception {
        KarateNativeFeature feature = KarateNativeReportParser.parse(NATIVE_JSON);

        assertEquals("synthetic-feature-one", feature.getPackageQualifiedName());
        assertEquals("synthetic-feature-one.feature", feature.getRelativePath());
        assertEquals(1, feature.getScenarios().size());

        KarateNativeScenario scenario = feature.getScenarios().get(0);
        assertEquals("synthetic scenario one", scenario.getName());
        assertEquals(3, scenario.getLine());
        assertEquals(1773863417611L, scenario.getStartTime());
        assertEquals(1773863452232L, scenario.getEndTime());
    }

    @Test
    void parse_neverSurfacesCallArgOrStepLog() throws Exception {
        KarateNativeFeature feature = KarateNativeReportParser.parse(NATIVE_JSON);

        String serialized = new ObjectMapper().writeValueAsString(feature);
        assertFalse(serialized.contains(SECRET),
                "credential-bearing callArg/stepLog content must never survive parsing");
        assertFalse(feature.toString().contains(SECRET));
    }

    @Test
    void parse_malformedJson_returnsNull() {
        assertNull(KarateNativeReportParser.parse("this is not json"));
        assertNull(KarateNativeReportParser.parse(null));
    }

    @Test
    void normalizeEpochPair_nullifiesSentinelAndReversedValues_passesThroughValid() {
        // valid pair passes through unchanged (plausible-magnitude epoch millis, not real captured values)
        assertArrayEquals(new Long[]{1700000000000L, 1700000060000L}, KarateNativeReportParser.normalizeEpochPair(1700000000000L, 1700000060000L));

        // either missing -> both null
        assertArrayEquals(new Long[]{null, null}, KarateNativeReportParser.normalizeEpochPair(null, 1700000060000L));
        assertArrayEquals(new Long[]{null, null}, KarateNativeReportParser.normalizeEpochPair(1700000000000L, null));
        assertArrayEquals(new Long[]{null, null}, KarateNativeReportParser.normalizeEpochPair(null, null));

        // zero sentinel (Karate's "never executed" marker) -> both null
        assertArrayEquals(new Long[]{null, null}, KarateNativeReportParser.normalizeEpochPair(0L, 1700000060000L));
        assertArrayEquals(new Long[]{null, null}, KarateNativeReportParser.normalizeEpochPair(1700000000000L, 0L));

        // negative -> both null
        assertArrayEquals(new Long[]{null, null}, KarateNativeReportParser.normalizeEpochPair(-5L, 1700000060000L));

        // reversed pair (both positive but end < start) -> both null
        assertArrayEquals(new Long[]{null, null}, KarateNativeReportParser.normalizeEpochPair(1700000060000L, 1700000000000L));

        // start == end (legitimate zero-duration scenario, not reversed) -> passes through unchanged
        assertArrayEquals(new Long[]{1700000000000L, 1700000000000L}, KarateNativeReportParser.normalizeEpochPair(1700000000000L, 1700000000000L));

        // both negative -> both null
        assertArrayEquals(new Long[]{null, null}, KarateNativeReportParser.normalizeEpochPair(-5L, -1L));
    }

    @Test
    void parse_wellFormedButWrongShapeJson_returnsEmptyRatherThanThrowing() {
        assertNull(KarateNativeReportParser.parse("[]").getPackageQualifiedName());
        assertTrue(KarateNativeReportParser.parse("[]").getScenarios().isEmpty());

        assertNull(KarateNativeReportParser.parse("\"a string\"").getPackageQualifiedName());
        assertTrue(KarateNativeReportParser.parse("\"a string\"").getScenarios().isEmpty());

        // well-formed object missing scenarioResults entirely
        KarateNativeFeature noScenarios = KarateNativeReportParser.parse("{\"packageQualifiedName\":\"pkg\"}");
        assertEquals("pkg", noScenarios.getPackageQualifiedName());
        assertTrue(noScenarios.getScenarios().isEmpty());
    }

    @Test
    void parse_normalizesSentinelTimestampsToNull() {
        String json = """
            {
              "packageQualifiedName": "pkg",
              "relativePath": "pkg.feature",
              "scenarioResults": [
                {"name": "never executed", "line": 1, "startTime": 0, "endTime": 0}
              ]
            }
            """;
        KarateNativeFeature feature = KarateNativeReportParser.parse(json);
        KarateNativeScenario scenario = feature.getScenarios().get(0);
        assertNull(scenario.getStartTime(), "startTime=0 sentinel must normalize to null, not epoch 1970");
        assertNull(scenario.getEndTime());
    }
}

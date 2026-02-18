package io.github.ericdriggs.reportcard.model.converter.karate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import io.github.ericdriggs.reportcard.model.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Converts Karate/Cucumber JSON to Reportcard model.
 * Maps Feature → TestSuiteModel, Scenario → TestCaseModel.
 */
@Slf4j
public class KarateCucumberConverter {

    private static final ObjectMapper mapper = SharedObjectMappers.ignoreUnknownObjectMapper;
    private static final KarateTagExtractor tagExtractor = new KarateTagExtractor();
    private static final BigDecimal NANOS_PER_SECOND = new BigDecimal("1000000000");

    /**
     * Parse Karate/Cucumber JSON array into list of TestSuiteModels.
     *
     * @param jsonContent JSON array of features
     * @return list of TestSuiteModels, one per feature
     */
    public static List<TestSuiteModel> fromCucumberJson(String jsonContent) {
        if (jsonContent == null || jsonContent.isBlank()) {
            return List.of();
        }

        try {
            JsonNode root = mapper.readTree(jsonContent);
            if (!root.isArray()) {
                log.warn("Cucumber JSON is not an array");
                return List.of();
            }

            List<TestSuiteModel> suites = new ArrayList<>();
            for (JsonNode feature : root) {
                suites.add(fromFeature(feature));
            }
            return suites;
        } catch (Exception e) {
            log.error("Failed to parse Cucumber JSON", e);
            return List.of();
        }
    }

    /**
     * Convert a single feature node to TestSuiteModel.
     */
    public static TestSuiteModel fromFeature(JsonNode feature) {
        String name = feature.path("name").asText("");
        List<String> tags = tagExtractor.extractTags(feature.path("tags"));

        List<TestCaseModel> testCases = new ArrayList<>();
        JsonNode elements = feature.path("elements");
        if (elements.isArray()) {
            for (JsonNode element : elements) {
                String type = element.path("type").asText("");
                // Only process scenarios and scenario outlines, skip backgrounds
                if ("scenario".equals(type) || "scenario_outline".equals(type)) {
                    testCases.add(fromScenario(element));
                }
            }
        }

        // Calculate aggregates
        int tests = testCases.size();
        int errors = 0, failures = 0, skipped = 0;
        BigDecimal totalTime = BigDecimal.ZERO;
        boolean isSuccess = true;

        for (TestCaseModel tc : testCases) {
            if (tc.getTime() != null) {
                totalTime = totalTime.add(tc.getTime());
            }
            TestStatus status = tc.getTestStatus();
            if (status == TestStatus.ERROR) {
                errors++;
                isSuccess = false;
            } else if (status == TestStatus.FAILURE) {
                failures++;
                isSuccess = false;
            } else if (status == TestStatus.SKIPPED) {
                skipped++;
            }
        }

        return TestSuiteModel.builder()
            .name(name)
            .tags(tags)
            .testCases(testCases)
            .tests(tests)
            .error(errors)
            .failure(failures)
            .skipped(skipped)
            .isSuccess(isSuccess && tests > 0)
            .hasSkip(skipped > 0 || tests == 0)
            .time(totalTime)
            .build();
    }

    /**
     * Convert a scenario element to TestCaseModel.
     */
    public static TestCaseModel fromScenario(JsonNode scenario) {
        String name = scenario.path("name").asText("");
        List<String> tags = tagExtractor.extractTags(scenario.path("tags"));

        // Determine status and time from steps
        TestStatus status = TestStatus.SUCCESS;
        BigDecimal totalNanos = BigDecimal.ZERO;
        List<TestCaseFaultModel> faults = new ArrayList<>();
        boolean hasSteps = false;

        JsonNode steps = scenario.path("steps");
        if (steps.isArray()) {
            for (JsonNode step : steps) {
                hasSteps = true;
                JsonNode result = step.path("result");
                String stepStatus = result.path("status").asText("skipped");
                long duration = result.path("duration").asLong(0);
                totalNanos = totalNanos.add(BigDecimal.valueOf(duration));

                // Status escalation: skipped < success < failure < error
                if ("failed".equals(stepStatus)) {
                    if (status != TestStatus.ERROR) {
                        status = TestStatus.FAILURE;
                    }
                    // Capture error message if present
                    String errorMessage = result.path("error_message").asText(null);
                    if (errorMessage != null) {
                        faults.add(TestCaseFaultModel.builder()
                            .faultContextFk(FaultContext.FAILURE.getFaultContextId())
                            .message(step.path("name").asText(""))
                            .value(errorMessage)
                            .build());
                    }
                } else if ("undefined".equals(stepStatus) || "pending".equals(stepStatus)) {
                    if (status == TestStatus.SUCCESS) {
                        status = TestStatus.SKIPPED;
                    }
                } else if ("skipped".equals(stepStatus)) {
                    if (status == TestStatus.SUCCESS) {
                        status = TestStatus.SKIPPED;
                    }
                }
                // "passed" keeps current status
            }
        }

        if (!hasSteps) {
            status = TestStatus.SKIPPED;
        }

        // Convert nanoseconds to seconds
        BigDecimal timeSeconds = totalNanos.divide(NANOS_PER_SECOND, 3, RoundingMode.HALF_UP);

        return TestCaseModel.builder()
            .name(name)
            .tags(tags)
            .testStatus(status)
            .testCaseFaults(faults)
            .time(timeSeconds)
            .build();
    }

    /**
     * Collect all tags from all suites and test cases (flattened, deduplicated).
     * Used for storing in test_result.tags column.
     */
    public static List<String> collectAllTags(List<TestSuiteModel> suites) {
        Set<String> allTags = new LinkedHashSet<>();
        for (TestSuiteModel suite : suites) {
            if (suite.getTags() != null) {
                allTags.addAll(suite.getTags());
            }
            for (TestCaseModel tc : suite.getTestCases()) {
                if (tc.getTags() != null) {
                    allTags.addAll(tc.getTags());
                }
            }
        }
        return new ArrayList<>(allTags);
    }
}

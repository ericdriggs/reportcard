package io.github.ericdriggs.reportcard.model.converter.karate;

import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.model.TestResultModel;
import io.github.ericdriggs.reportcard.model.TestSuiteModel;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enriches junit-derived {@link TestResultModel} structures with Karate-native
 * timestamps and tags. Never overwrites junit's own {@code time}, counts, or status.
 */
@Slf4j
public enum KarateEnricher {
    ; //static methods only

    public static List<TestSuiteModel> enrichTimestamps(TestResultModel model, List<KarateNativeFeature> nativeFeatures) {
        List<TestSuiteModel> nativeSuites = KarateNativeConverter.fromNativeFeatures(nativeFeatures);
        enrichTimestampsFromSuites(model, nativeSuites);
        return nativeSuites;
    }

    /**
     * Copies suite/case tags from Karate cucumber-derived suites onto the junit-derived model.
     * Matches suites by exact name and cases within a matched suite by name (ordinal on duplicates).
     * Never clobbers existing non-empty tags; unmatched suites/cases are skipped with a warning.
     */
    public static List<TestSuiteModel> enrichTags(TestResultModel model, String cucumberJson) {
        List<TestSuiteModel> cucumberSuites = KarateCucumberConverter.fromCucumberJson(cucumberJson);
        enrichTagsFromSuites(model, cucumberSuites);
        return cucumberSuites;
    }

    static void enrichTimestampsFromSuites(TestResultModel model, List<TestSuiteModel> nativeSuites) {
        Map<String, TestSuiteModel> byPackageName = new HashMap<>();
        Map<String, TestSuiteModel> byName = new HashMap<>();
        for (TestSuiteModel nativeSuite : nativeSuites) {
            putFirstWriteWins(byPackageName, nativeSuite.getPackageName(), nativeSuite, "native suite packageName");
            putFirstWriteWins(byName, nativeSuite.getName(), nativeSuite, "native suite name");
        }

        for (TestSuiteModel suite : model.getTestSuites()) {
            try {
                enrichSuiteTimestamps(suite, byPackageName, byName);
            } catch (Exception e) {
                log.warn("Failed to enrich timestamps for suite={}, continuing with other suites", suite.getName(), e);
            }
        }
    }

    static void enrichSuiteTimestamps(TestSuiteModel suite, Map<String, TestSuiteModel> byPackageName, Map<String, TestSuiteModel> byName) {
        Instant suiteStart = null;
        Instant suiteEnd = null;
        Map<String, Integer> consumedByName = new HashMap<>();
        for (TestCaseModel testCase : suite.getTestCases()) {
            TestSuiteModel nativeSuite = testCase.getClassName() != null
                    ? byPackageName.get(testCase.getClassName())
                    : byName.get(suite.getName());
            if (nativeSuite == null) {
                log.warn("no native suite found for className={} suite={}", testCase.getClassName(), suite.getName());
                continue;
            }
            TestCaseModel nativeCase = nextCaseByName(nativeSuite.getTestCases(), testCase.getName(), consumedByName);
            if (nativeCase == null) {
                log.warn("no native case found for suite={} case name={}", suite.getName(), testCase.getName());
                continue;
            }
            if (nativeCase.getStartTime() != null && nativeCase.getEndTime() != null) {
                testCase.setStartTime(nativeCase.getStartTime());
                testCase.setEndTime(nativeCase.getEndTime());
                if (suiteStart == null || nativeCase.getStartTime().isBefore(suiteStart)) {
                    suiteStart = nativeCase.getStartTime();
                }
                if (suiteEnd == null || nativeCase.getEndTime().isAfter(suiteEnd)) {
                    suiteEnd = nativeCase.getEndTime();
                }
            }
        }
        suite.setStartTime(suiteStart);
        suite.setEndTime(suiteEnd);
    }

    static void enrichTagsFromSuites(TestResultModel model, List<TestSuiteModel> cucumberSuites) {
        Map<String, TestSuiteModel> cucumberByName = new HashMap<>();
        for (TestSuiteModel cucumberSuite : cucumberSuites) {
            putFirstWriteWins(cucumberByName, cucumberSuite.getName(), cucumberSuite, "cucumber suite name");
        }

        for (TestSuiteModel suite : model.getTestSuites()) {
            try {
                enrichSuiteTags(suite, cucumberByName);
            } catch (Exception e) {
                log.warn("Failed to enrich tags for suite={}, continuing with other suites", suite.getName(), e);
            }
        }
    }

    static void enrichSuiteTags(TestSuiteModel suite, Map<String, TestSuiteModel> cucumberByName) {
        TestSuiteModel cucumberSuite = cucumberByName.get(suite.getName());
        if (cucumberSuite == null) {
            log.warn("no cucumber suite found for suite name={}", suite.getName());
            return;
        }
        if ((suite.getTags() == null || suite.getTags().isEmpty()) && cucumberSuite.getTags() != null) {
            suite.setTags(cucumberSuite.getTags());
        }

        Map<String, Integer> consumedByName = new HashMap<>();
        for (TestCaseModel testCase : suite.getTestCases()) {
            TestCaseModel cucumberCase = nextCaseByName(cucumberSuite.getTestCases(), testCase.getName(), consumedByName);
            if (cucumberCase == null) {
                log.warn("no cucumber case found for suite={} case name={}", suite.getName(), testCase.getName());
                continue;
            }
            if ((testCase.getTags() == null || testCase.getTags().isEmpty()) && cucumberCase.getTags() != null) {
                testCase.setTags(cucumberCase.getTags());
            }
        }
    }

    static TestCaseModel nextCaseByName(List<TestCaseModel> cases, String name, Map<String, Integer> consumedByName) {
        List<TestCaseModel> matches = new ArrayList<>();
        for (TestCaseModel testCase : cases) {
            if (testCase.getName() != null && testCase.getName().equals(name)) {
                matches.add(testCase);
            }
        }
        int ordinal = consumedByName.getOrDefault(name, 0);
        if (ordinal >= matches.size()) {
            return null;
        }
        consumedByName.put(name, ordinal + 1);
        return matches.get(ordinal);
    }

    static <T> void putFirstWriteWins(Map<String, T> map, String key, T value, String keyDescription) {
        if (key == null) {
            return;
        }
        if (map.containsKey(key)) {
            log.warn("duplicate {} '{}' encountered, keeping first value", keyDescription, key);
            return;
        }
        map.put(key, value);
    }
}

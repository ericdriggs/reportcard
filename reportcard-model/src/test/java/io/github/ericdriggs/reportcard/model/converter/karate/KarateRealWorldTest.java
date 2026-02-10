package io.github.ericdriggs.reportcard.model.converter.karate;

import io.github.ericdriggs.reportcard.model.TestSuiteModel;
import io.github.ericdriggs.reportcard.xml.ResourceReader;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests using real Karate JSON output.
 * Enable when real sample files are available.
 *
 * <p>To use this test class:
 * <ol>
 *   <li>Copy actual karate-json.txt files from CI runs to test resources</li>
 *   <li>Add specific assertions for expected feature/scenario counts</li>
 *   <li>Validate tag extraction from real data</li>
 *   <li>Remove @Disabled annotation from tests</li>
 * </ol>
 */
public class KarateRealWorldTest {

    @Test
    @Disabled("Enable when real Karate JSON is available at this path")
    void parseRealKarateOutput() {
        String json = ResourceReader.resourceAsString(
            "format-samples/cucumber-json/real-karate-output.json");

        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        // Add assertions based on known content of real file
        assertFalse(result.isEmpty(), "Should parse at least one feature");

        // Example assertions (adjust based on actual data):
        // assertEquals(5, result.size(), "Should have 5 features");
        // assertTrue(result.get(0).getTags().contains("smoke"));
    }

    @Test
    @Disabled("Enable when multi-feature Karate JSON is available")
    void parseMultiFeatureKarateOutput() {
        String json = ResourceReader.resourceAsString(
            "format-samples/cucumber-json/multi-feature-karate.json");

        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        // Validate multiple features parsed correctly
        assertTrue(result.size() > 1, "Should parse multiple features");

        // Check aggregates across all features
        int totalTests = result.stream().mapToInt(TestSuiteModel::getTests).sum();
        assertTrue(totalTests > 0, "Should have at least one test");
    }

    @Test
    @Disabled("Enable when Karate output with failures is available")
    void parseKarateOutputWithFailures() {
        String json = ResourceReader.resourceAsString(
            "format-samples/cucumber-json/karate-with-failures.json");

        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        // Find suite with failures
        int totalFailures = result.stream().mapToInt(TestSuiteModel::getFailure).sum();
        assertTrue(totalFailures > 0, "Should have at least one failure");

        // Verify fault details captured
        boolean hasFaults = result.stream()
            .flatMap(s -> s.getTestCases().stream())
            .anyMatch(tc -> !tc.getTestCaseFaults().isEmpty());
        assertTrue(hasFaults, "Should have fault details for failures");
    }

    @Test
    @Disabled("Enable when Karate output with tags is available")
    void parseKarateOutputWithTags() {
        String json = ResourceReader.resourceAsString(
            "format-samples/cucumber-json/karate-with-tags.json");

        List<TestSuiteModel> result = KarateCucumberConverter.fromCucumberJson(json);

        // Collect all tags
        List<String> allTags = KarateCucumberConverter.collectAllTags(result);
        assertFalse(allTags.isEmpty(), "Should have extracted tags");

        // Example: verify specific tags present
        // assertTrue(allTags.contains("smoke"));
        // assertTrue(allTags.contains("env=staging"));
    }
}

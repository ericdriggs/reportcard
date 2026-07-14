package io.github.ericdriggs.reportcard.model.converter.karate;

import io.github.ericdriggs.reportcard.model.TestCaseModel;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class KarateNativeConverterTest {

    @Test
    void doFromNativeToModelTestCase_mapsNameAndTimestamps() {
        KarateNativeScenario valid = KarateNativeScenario.builder()
                .name("synthetic scenario one").line(3).startTime(1700000000000L).endTime(1700000060000L).build();
        TestCaseModel result = KarateNativeConverter.doFromNativeToModelTestCase(valid);
        assertEquals("synthetic scenario one", result.getName());
        assertEquals(Instant.ofEpochMilli(1700000000000L), result.getStartTime());
        assertEquals(Instant.ofEpochMilli(1700000060000L), result.getEndTime());

        KarateNativeScenario noTiming = KarateNativeScenario.builder()
                .name("never enriched").line(9).startTime(null).endTime(null).build();
        TestCaseModel resultNoTiming = KarateNativeConverter.doFromNativeToModelTestCase(noTiming);
        assertEquals("never enriched", resultNoTiming.getName());
        assertNull(resultNoTiming.getStartTime());
        assertNull(resultNoTiming.getEndTime());
    }

    @Test
    void doFromNativeToModelTestCases_isNullAndEmptySafe_mapsEachScenario() {
        assertEquals(0, KarateNativeConverter.doFromNativeToModelTestCases(null).size());
        assertEquals(0, KarateNativeConverter.doFromNativeToModelTestCases(java.util.List.of()).size());

        java.util.List<KarateNativeScenario> scenarios = java.util.List.of(
                KarateNativeScenario.builder().name("a").startTime(1700000000000L).endTime(1700000060000L).build(),
                KarateNativeScenario.builder().name("b").startTime(1700000120000L).endTime(1700000180000L).build());
        java.util.List<TestCaseModel> result = KarateNativeConverter.doFromNativeToModelTestCases(scenarios);
        assertEquals(2, result.size());
        assertEquals("a", result.get(0).getName());
        assertEquals(Instant.ofEpochMilli(1700000000000L), result.get(0).getStartTime());
        assertEquals(Instant.ofEpochMilli(1700000060000L), result.get(0).getEndTime());
        assertEquals("b", result.get(1).getName());
        assertEquals(Instant.ofEpochMilli(1700000120000L), result.get(1).getStartTime());
        assertEquals(Instant.ofEpochMilli(1700000180000L), result.get(1).getEndTime());
    }

    @Test
    void doFromNativeToModelTestSuite_mapsPackageNameAndName_handlesNullScenarios() {
        KarateNativeFeature feature = KarateNativeFeature.builder()
                .packageQualifiedName("synthetic-feature-one")
                .relativePath("synthetic-feature-one.feature")
                .scenarios(java.util.List.of(KarateNativeScenario.builder().name("s1").startTime(1700000000000L).endTime(1700000060000L).build()))
                .build();
        io.github.ericdriggs.reportcard.model.TestSuiteModel suite = KarateNativeConverter.doFromNativeToModelTestSuite(feature);
        assertEquals("synthetic-feature-one", suite.getPackageName());
        assertEquals("synthetic-feature-one.feature", suite.getName());
        assertEquals(1, suite.getTestCases().size());
        assertEquals("s1", suite.getTestCases().get(0).getName());
        assertEquals(Instant.ofEpochMilli(1700000000000L), suite.getTestCases().get(0).getStartTime(),
                "suite-level mapping must not lose case-level timing set by doFromNativeToModelTestCases");
        assertEquals(Instant.ofEpochMilli(1700000060000L), suite.getTestCases().get(0).getEndTime());

        KarateNativeFeature nullScenarios = KarateNativeFeature.builder()
                .packageQualifiedName("pkg").relativePath("pkg.feature").scenarios(null).build();
        io.github.ericdriggs.reportcard.model.TestSuiteModel suiteFromNullScenarios = assertDoesNotThrow(
                () -> KarateNativeConverter.doFromNativeToModelTestSuite(nullScenarios));
        assertNotNull(suiteFromNullScenarios.getTestCases());
        assertEquals(0, suiteFromNullScenarios.getTestCases().size());
    }

    @Test
    void fromNativeFeatures_isNullAndEmptySafe_mapsEachFeature() {
        assertEquals(0, KarateNativeConverter.fromNativeFeatures(null).size());
        assertEquals(0, KarateNativeConverter.fromNativeFeatures(java.util.List.of()).size());

        KarateNativeFeature f1 = KarateNativeFeature.builder()
                .packageQualifiedName("pkg-a").relativePath("pkg-a.feature")
                .scenarios(java.util.List.of(KarateNativeScenario.builder().name("s1").startTime(1700000000000L).endTime(1700000060000L).build()))
                .build();
        KarateNativeFeature f2 = KarateNativeFeature.builder()
                .packageQualifiedName("pkg-b").relativePath("pkg-b.feature").scenarios(null).build();
        java.util.List<io.github.ericdriggs.reportcard.model.TestSuiteModel> result =
                KarateNativeConverter.fromNativeFeatures(java.util.List.of(f1, f2));
        assertEquals(2, result.size());

        assertEquals("pkg-a", result.get(0).getPackageName());
        assertEquals("pkg-a.feature", result.get(0).getName());
        assertEquals(1, result.get(0).getTestCases().size());
        assertEquals("s1", result.get(0).getTestCases().get(0).getName());
        assertEquals(Instant.ofEpochMilli(1700000000000L), result.get(0).getTestCases().get(0).getStartTime());
        assertEquals(Instant.ofEpochMilli(1700000060000L), result.get(0).getTestCases().get(0).getEndTime());

        assertEquals("pkg-b", result.get(1).getPackageName());
        assertEquals("pkg-b.feature", result.get(1).getName());
        assertEquals(0, result.get(1).getTestCases().size(), "null scenarios must map to empty list, not throw");
    }

    @Test
    void modelMapper_mapDispatchesToRegisteredConverter() {
        KarateNativeFeature feature = KarateNativeFeature.builder()
                .packageQualifiedName("pkg-a").relativePath("pkg-a.feature")
                .scenarios(java.util.List.of(KarateNativeScenario.builder().name("s1").startTime(1700000000000L).endTime(1700000060000L).build()))
                .build();
        io.github.ericdriggs.reportcard.model.TestSuiteModel mapped =
                KarateNativeConverter.modelMapper.map(feature, io.github.ericdriggs.reportcard.model.TestSuiteModel.class);
        assertEquals("pkg-a", mapped.getPackageName());
        assertEquals("pkg-a.feature", mapped.getName());
        assertEquals(1, mapped.getTestCases().size());
        assertEquals("s1", mapped.getTestCases().get(0).getName());
        assertEquals(Instant.ofEpochMilli(1700000000000L), mapped.getTestCases().get(0).getStartTime(),
                "ModelMapper dispatch must produce the same field mapping as calling doFromNativeToModelTestSuite directly");
        assertEquals(Instant.ofEpochMilli(1700000060000L), mapped.getTestCases().get(0).getEndTime());
    }
}

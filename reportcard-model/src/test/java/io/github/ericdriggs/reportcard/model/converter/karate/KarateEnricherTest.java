package io.github.ericdriggs.reportcard.model.converter.karate;

import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.model.TestResultModel;
import io.github.ericdriggs.reportcard.model.TestSuiteModel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KarateEnricherTest {

    static TestCaseModel testCase(String className, String name, String time) {
        TestCaseModel tc = new TestCaseModel();
        tc.setClassName(className);
        tc.setName(name);
        tc.setTime(new BigDecimal(time));
        return tc;
    }

    static TestResultModel junitModel(TestSuiteModel... suites) {
        TestResultModel model = new TestResultModel();
        model.setTestSuites(new ArrayList<>(List.of(suites)));
        return model;
    }

    static TestSuiteModel suite(String name, TestCaseModel... cases) {
        TestSuiteModel s = new TestSuiteModel();
        s.setName(name);
        s.setTestCases(new ArrayList<>(List.of(cases)));
        return s;
    }

    static final String CUCUMBER_JSON = """
        [
          {
            "keyword": "Feature",
            "name": "feature-a.feature",
            "tags": [{"name": "@feature-tag"}],
            "elements": [
              {
                "type": "scenario",
                "name": "scenario one",
                "tags": [{"name": "@jira=SYNTH-1234"}],
                "steps": [{"name": "step", "result": {"status": "passed", "duration": 1000}}]
              }
            ]
          }
        ]
        """;

    @Test
    void enrichTags_copiesSuiteAndCaseTagsFromCucumber() {
        TestCaseModel tc = testCase("feature-a", "scenario one", "1.0");
        TestSuiteModel s = suite("feature-a.feature", tc);
        TestResultModel model = junitModel(s);

        List<TestSuiteModel> cucumberSuites = KarateCucumberConverter.fromCucumberJson(CUCUMBER_JSON);
        KarateEnricher.enrichTagsFromSuites(model, cucumberSuites);

        assertNotNull(s.getTags());
        assertTrue(s.getTags().contains("feature-tag"), "suite tags from cucumber feature: " + s.getTags());
        assertNotNull(tc.getTags());
        assertTrue(tc.getTags().contains("jira=SYNTH-1234"), "case tags from cucumber scenario: " + tc.getTags());
    }

    static TestSuiteModel poisonSuite(String name) {
        TestSuiteModel s = new TestSuiteModel();
        s.setName(name);
        s.setTestCases(new ArrayList<>(List.of(testCase("x", "x", "1.0"))) {
            @Override
            public java.util.Iterator<TestCaseModel> iterator() {
                throw new RuntimeException("boom - simulated suite processing failure");
            }
        });
        return s;
    }

    @Test
    void enrichTimestampsFromSuites_setsCaseStartEnd_keepsJunitTime() {
        TestCaseModel tc = testCase("synthetic-feature-one", "synthetic scenario one", "1.5");
        TestResultModel model = junitModel(suite("synthetic-feature-one.feature", tc));

        KarateEnricher.enrichTimestampsFromSuites(model, List.of(
                nativeSuite("synthetic-feature-one", "synthetic-feature-one.feature",
                        nativeCase("synthetic scenario one", 1700000000000L, 1700000060000L))));

        assertEquals(Instant.ofEpochMilli(1700000000000L), tc.getStartTime());
        assertEquals(Instant.ofEpochMilli(1700000060000L), tc.getEndTime());
        assertEquals(new BigDecimal("1.5"), tc.getTime(), "junit execution time must not be overwritten");
    }

    @Test
    void enrichTimestampsFromSuites_duplicateNames_matchOrdinally() {
        TestCaseModel first = testCase("synthetic-feature-two", "synthetic scenario two", "0.5");
        TestCaseModel second = testCase("synthetic-feature-two", "synthetic scenario two", "0.5");
        TestResultModel model = junitModel(suite("synthetic-feature-two.feature", first, second));

        KarateEnricher.enrichTimestampsFromSuites(model, List.of(
                nativeSuite("synthetic-feature-two", "synthetic-feature-two.feature",
                        nativeCase("synthetic scenario two", 1700000000000L, 1700000060000L),
                        nativeCase("synthetic scenario two", 1700000000000L, 1700000120000L))));

        assertEquals(Instant.ofEpochMilli(1700000060000L), first.getEndTime(), "first junit case pairs with first native case");
        assertEquals(Instant.ofEpochMilli(1700000120000L), second.getEndTime(), "second junit case pairs with second native case");
    }

    @Test
    void enrichTimestampsFromSuites_unmatchedCase_skipsWithoutThrowing() {
        TestCaseModel matched = testCase("feature-a", "matched scenario", "1.0");
        TestCaseModel unmatched = testCase("feature-a", "renamed scenario", "1.0");
        TestCaseModel noFeature = testCase("feature-unknown", "some scenario", "1.0");
        TestResultModel model = junitModel(suite("feature-a.feature", matched, unmatched),
                suite("feature-unknown.feature", noFeature));

        assertDoesNotThrow(() -> KarateEnricher.enrichTimestampsFromSuites(model, List.of(
                nativeSuite("feature-a", "feature-a.feature", nativeCase("matched scenario", 1700000000000L, 1700000060000L)))));

        assertEquals(Instant.ofEpochMilli(1700000000000L), matched.getStartTime());
        assertNull(unmatched.getStartTime(), "unmatched case must remain unenriched");
        assertNull(unmatched.getEndTime());
        assertNull(noFeature.getStartTime(), "case in unmatched feature must remain unenriched");
    }

    @Test
    void enrichTimestampsFromSuites_suiteGetsMinMaxOfCases() {
        TestCaseModel early = testCase("feature-a", "early scenario", "1.0");
        TestCaseModel late = testCase("feature-a", "late scenario", "1.0");
        TestSuiteModel s = suite("feature-a.feature", early, late);
        TestResultModel model = junitModel(s);

        KarateEnricher.enrichTimestampsFromSuites(model, List.of(
                nativeSuite("feature-a", "feature-a.feature",
                        nativeCase("early scenario", 1700000000000L, 1700000240000L),
                        nativeCase("late scenario", 1700000060000L, 1700000480000L))));

        assertEquals(Instant.ofEpochMilli(1700000000000L), s.getStartTime(), "suite start = min case start");
        assertEquals(Instant.ofEpochMilli(1700000480000L), s.getEndTime(), "suite end = max case end");
    }

    @Test
    void enrichTimestampsFromSuites_karateOnlyStructure_matchesViaRelativePath() {
        TestCaseModel tc = testCase(null, "synthetic scenario one", "2.5");
        TestResultModel model = junitModel(suite("synthetic-feature-one.feature", tc));

        KarateEnricher.enrichTimestampsFromSuites(model, List.of(
                nativeSuite("synthetic-feature-one", "synthetic-feature-one.feature",
                        nativeCase("synthetic scenario one", 1700000000000L, 1700000060000L))));

        assertEquals(Instant.ofEpochMilli(1700000000000L), tc.getStartTime(),
                "className-null cases must correlate via suite.name == native suite.name");
        assertEquals(Instant.ofEpochMilli(1700000060000L), tc.getEndTime());
    }

    @Test
    void enrichTimestampsFromSuites_duplicatePackageName_firstMatchWinsWithWarning() {
        TestCaseModel tc = testCase("dup-pkg", "case", "1.0");
        TestResultModel model = junitModel(suite("suite-x.feature", tc));

        TestSuiteModel firstNative = nativeSuite("dup-pkg", "suite-x.feature", nativeCase("case", 1700000000000L, 1700000060000L));
        TestSuiteModel secondNative = nativeSuite("dup-pkg", "suite-y.feature", nativeCase("case", 1700000600000L, 1700000660000L));

        KarateEnricher.enrichTimestampsFromSuites(model, List.of(firstNative, secondNative));

        assertEquals(Instant.ofEpochMilli(1700000000000L), tc.getStartTime(),
                "first native suite with a duplicate packageName must win, not the last");
    }

    @Test
    void enrichTimestampsFromSuites_oneSuiteThrows_othersStillEnriched() {
        TestCaseModel goodCase = testCase("feature-a", "scenario", "1.0");
        TestSuiteModel goodSuite = suite("feature-a.feature", goodCase);
        TestSuiteModel poison = poisonSuite("poison.feature");
        // Fixture note: junitModel(poison, goodSuite) would trigger the poisoned iterator during
        // TestResultModel.setTestSuites' eager updateTotalsFromTestSuites()/getResultCount() pass over
        // every suite, before enrichTimestampsFromSuites is ever called - unrelated to the property under
        // test. Appending poison to the live list after construction avoids that eager recompute so the
        // poisoned iterator only fires when KarateEnricher itself iterates the suite's cases.
        TestResultModel model = junitModel(goodSuite);
        model.getTestSuites().add(poison);

        assertDoesNotThrow(() -> KarateEnricher.enrichTimestampsFromSuites(model, List.of(
                nativeSuite("feature-a", "feature-a.feature", nativeCase("scenario", 1700000000000L, 1700000060000L)))));

        assertEquals(Instant.ofEpochMilli(1700000000000L), goodCase.getStartTime(),
                "a suite that throws during matching must not prevent other suites from being enriched");
    }

    static List<TestSuiteModel> poisonSuiteList(TestSuiteModel... suites) {
        return new ArrayList<>(List.of(suites)) {
            @Override
            public java.util.Iterator<TestSuiteModel> iterator() {
                throw new RuntimeException("boom - simulated phase-level failure");
            }
        };
    }

    @Test
    void enrichTimestampsFromSuites_poisonedNativeSuitesList_throwsPastPerSuiteCatch() {
        // Proves enrichTimestampsFromSuites does NOT swallow every exception itself - a genuine failure
        // while indexing the native suites (before the per-suite try/catch loop) propagates out. This is
        // what makes JunitController.runPhaseNonBlocking's wrapping of this call meaningful, not a no-op:
        // see JunitControllerPhaseHelperTest for proof that wrapper catches whatever reaches it.
        TestCaseModel tc = testCase("feature-a", "scenario", "1.0");
        TestResultModel model = junitModel(suite("feature-a.feature", tc));

        List<TestSuiteModel> poisonNativeSuites = poisonSuiteList(
                nativeSuite("feature-a", "feature-a.feature", nativeCase("scenario", 1700000000000L, 1700000060000L)));

        assertThrows(RuntimeException.class,
                () -> KarateEnricher.enrichTimestampsFromSuites(model, poisonNativeSuites));
    }

    @Test
    void enrichTagsFromSuites_poisonedCucumberSuitesList_throwsPastPerSuiteCatch() {
        // Same property as above, for the cucumber-tag phase's real call site.
        TestCaseModel tc = testCase("feature-a", "scenario one", "1.0");
        TestSuiteModel s = suite("feature-a.feature", tc);
        TestResultModel model = junitModel(s);

        TestSuiteModel cucumberSuite = new TestSuiteModel();
        cucumberSuite.setName("feature-a.feature");
        cucumberSuite.setTags(List.of("feature-tag"));
        cucumberSuite.setTestCases(new ArrayList<>());

        List<TestSuiteModel> poisonCucumberSuites = poisonSuiteList(cucumberSuite);

        assertThrows(RuntimeException.class,
                () -> KarateEnricher.enrichTagsFromSuites(model, poisonCucumberSuites));
    }

    @Test
    void legacyTestSuitesJson_withoutTimestampFields_stillParses() {
        String legacyJson = """
            [{"name":"old-suite","tests":1,"skipped":0,"error":0,"failure":0,
              "testCases":[{"name":"old case","className":"old-suite","time":1.5}]}]
            """;
        List<TestSuiteModel> suites =
                io.github.ericdriggs.reportcard.model.TestSuiteModel.fromJson(legacyJson);
        assertEquals(1, suites.size());
        assertNull(suites.get(0).getStartTime(), "legacy rows have no suite timestamps");
        assertNull(suites.get(0).getTestCases().get(0).getStartTime(), "legacy rows have no case timestamps");
        assertEquals("old case", suites.get(0).getTestCases().get(0).getName());
    }

    @Test
    void putFirstWriteWins_keepsFirstValueOnDuplicateKey() {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        KarateEnricher.putFirstWriteWins(map, "dup", "first", "test key");
        KarateEnricher.putFirstWriteWins(map, "dup", "second", "test key");
        assertEquals("first", map.get("dup"), "second write with same key must not overwrite the first");

        KarateEnricher.putFirstWriteWins(map, null, "ignored", "test key");
        assertFalse(map.containsKey(null), "null key must be ignored, not inserted");

        KarateEnricher.putFirstWriteWins(map, "unique", "value", "test key");
        assertEquals("value", map.get("unique"));
    }

    @Test
    void nextCaseByName_pairsDuplicateNamesOrdinally_thenReturnsNullWhenExhausted() {
        TestCaseModel first = testCase(null, "dup", "0.001");
        TestCaseModel second = testCase(null, "dup", "0.001");
        List<TestCaseModel> cases = List.of(first, second);
        java.util.Map<String, Integer> consumed = new java.util.HashMap<>();

        assertSame(first, KarateEnricher.nextCaseByName(cases, "dup", consumed));
        assertSame(second, KarateEnricher.nextCaseByName(cases, "dup", consumed));
        assertNull(KarateEnricher.nextCaseByName(cases, "dup", consumed), "third call with same name must return null, not wrap around");
        assertNull(KarateEnricher.nextCaseByName(cases, "does-not-exist", consumed));
    }

    static io.github.ericdriggs.reportcard.model.TestSuiteModel nativeSuite(String packageName, String name, TestCaseModel... cases) {
        io.github.ericdriggs.reportcard.model.TestSuiteModel s = new io.github.ericdriggs.reportcard.model.TestSuiteModel();
        s.setPackageName(packageName);
        s.setName(name);
        s.setTestCases(new ArrayList<>(List.of(cases)));
        return s;
    }

    static TestCaseModel nativeCase(String name, long start, long end) {
        TestCaseModel tc = new TestCaseModel();
        tc.setName(name);
        tc.setStartTime(Instant.ofEpochMilli(start));
        tc.setEndTime(Instant.ofEpochMilli(end));
        return tc;
    }

    @Test
    void enrichSuiteTimestamps_matchesByPackageNameOrByName_setsCaseAndSuiteMinMax() {
        TestCaseModel byPackageName = testCase("pkg-a", "early", "1.0");
        TestCaseModel byName = testCase(null, "late", "1.0");
        TestCaseModel unmatched = testCase("pkg-a", "no-native-match", "1.0");
        TestSuiteModel s = suite("pkg-a.feature", byPackageName, byName, unmatched);

        TestSuiteModel native1 = nativeSuite("pkg-a", "pkg-a.feature",
                nativeCase("early", 1700000000000L, 1700000240000L), nativeCase("late", 1700000060000L, 1700000480000L));

        Map<String, TestSuiteModel> byPackageNameMap = Map.of("pkg-a", native1);
        Map<String, TestSuiteModel> byNameMap = Map.of("pkg-a.feature", native1);

        KarateEnricher.enrichSuiteTimestamps(s, byPackageNameMap, byNameMap);

        assertEquals(Instant.ofEpochMilli(1700000000000L), byPackageName.getStartTime());
        assertEquals(Instant.ofEpochMilli(1700000240000L), byPackageName.getEndTime());
        assertEquals(Instant.ofEpochMilli(1700000060000L), byName.getStartTime(), "className-null case must match via suite name fallback");
        assertNull(unmatched.getStartTime(), "unmatched case must remain unenriched, no throw");
        assertEquals(Instant.ofEpochMilli(1700000000000L), s.getStartTime(), "suite start = min of enriched cases");
        assertEquals(Instant.ofEpochMilli(1700000480000L), s.getEndTime(), "suite end = max of enriched cases");
    }

    @Test
    void enrichSuiteTags_copiesSuiteAndCaseTagsOnlyWhenEmpty() {
        TestCaseModel tc = testCase("feature-a", "scenario one", "1.0");
        TestSuiteModel s = suite("feature-a.feature", tc);

        TestSuiteModel cucumberSuite = new TestSuiteModel();
        cucumberSuite.setName("feature-a.feature");
        cucumberSuite.setTags(List.of("feature-tag"));
        TestCaseModel cucumberCase = new TestCaseModel();
        cucumberCase.setName("scenario one");
        cucumberCase.setTags(List.of("jira=SYNTH-1234"));
        cucumberSuite.setTestCases(new ArrayList<>(List.of(cucumberCase)));

        Map<String, TestSuiteModel> cucumberByName = Map.of("feature-a.feature", cucumberSuite);
        KarateEnricher.enrichSuiteTags(s, cucumberByName);

        assertEquals(List.of("feature-tag"), s.getTags());
        assertEquals(List.of("jira=SYNTH-1234"), tc.getTags());

        // never clobbers existing non-empty tags
        TestCaseModel alreadyTagged = testCase("feature-a", "scenario one", "1.0");
        alreadyTagged.setTags(List.of("existing-tag"));
        TestSuiteModel suiteAlreadyTagged = suite("feature-a.feature", alreadyTagged);
        suiteAlreadyTagged.setTags(List.of("existing-suite-tag"));
        KarateEnricher.enrichSuiteTags(suiteAlreadyTagged, cucumberByName);
        assertEquals(List.of("existing-suite-tag"), suiteAlreadyTagged.getTags(), "must not clobber existing suite tags");
        assertEquals(List.of("existing-tag"), alreadyTagged.getTags(), "must not clobber existing case tags");
    }

    @Test
    void enrichTimestamps_convertsNativeFeaturesThenEnriches_returnsAdaptedSuites() {
        TestCaseModel tc = testCase("synthetic-feature-one", "synthetic scenario one", "1.5");
        TestResultModel model = junitModel(suite("synthetic-feature-one.feature", tc));

        KarateNativeFeature feature = KarateNativeFeature.builder()
                .packageQualifiedName("synthetic-feature-one")
                .relativePath("synthetic-feature-one.feature")
                .scenarios(List.of(KarateNativeScenario.builder()
                        .name("synthetic scenario one").startTime(1700000000000L).endTime(1700000060000L).build()))
                .build();

        List<TestSuiteModel> returned = KarateEnricher.enrichTimestamps(model, List.of(feature));

        assertEquals(Instant.ofEpochMilli(1700000000000L), tc.getStartTime(),
                "public entry point must convert then enrich, same outcome as calling the converter and enrichTimestampsFromSuites separately");
        assertEquals(Instant.ofEpochMilli(1700000060000L), tc.getEndTime());

        assertEquals(1, returned.size());
        assertEquals("synthetic-feature-one", returned.get(0).getPackageName());
        assertEquals("synthetic-feature-one.feature", returned.get(0).getName());
        assertEquals(1, returned.get(0).getTestCases().size());
        assertEquals("synthetic scenario one", returned.get(0).getTestCases().get(0).getName());
    }

    @Test
    void enrichTimestamps_nullFeatures_returnsEmptyListAndEnrichesNothing() {
        TestCaseModel tc = testCase("synthetic-feature-one", "synthetic scenario one", "1.5");
        TestResultModel model = junitModel(suite("synthetic-feature-one.feature", tc));

        List<TestSuiteModel> returned = assertDoesNotThrow(() -> KarateEnricher.enrichTimestamps(model, null));

        assertEquals(0, returned.size());
        assertNull(tc.getStartTime(), "no native features means nothing to enrich");
    }

    @Test
    void enrichTagsFromSuites_duplicateSuiteName_firstMatchWinsWithWarning() {
        TestCaseModel tc = testCase("feature-a", "scenario one", "1.0");
        TestSuiteModel s = suite("feature-a.feature", tc);
        TestResultModel model = junitModel(s);

        TestSuiteModel firstCucumber = new TestSuiteModel();
        firstCucumber.setName("feature-a.feature");
        firstCucumber.setTags(List.of("first-tag"));
        firstCucumber.setTestCases(new ArrayList<>());

        TestSuiteModel secondCucumber = new TestSuiteModel();
        secondCucumber.setName("feature-a.feature");
        secondCucumber.setTags(List.of("second-tag"));
        secondCucumber.setTestCases(new ArrayList<>());

        KarateEnricher.enrichTagsFromSuites(model, List.of(firstCucumber, secondCucumber));

        assertEquals(List.of("first-tag"), s.getTags(), "first cucumber suite with a duplicate name must win, not the last");
    }

    @Test
    void enrichTagsFromSuites_oneSuiteThrows_othersStillEnriched() {
        TestCaseModel goodCase = testCase("feature-a", "scenario one", "1.0");
        TestSuiteModel goodSuite = suite("feature-a.feature", goodCase);
        TestSuiteModel poison = poisonSuite("poison.feature");
        TestResultModel model = junitModel(goodSuite);
        model.getTestSuites().add(poison);

        TestSuiteModel cucumberSuite = new TestSuiteModel();
        cucumberSuite.setName("feature-a.feature");
        cucumberSuite.setTags(List.of("feature-tag"));
        cucumberSuite.setTestCases(new ArrayList<>());

        assertDoesNotThrow(() -> KarateEnricher.enrichTagsFromSuites(model, List.of(cucumberSuite)));
        assertEquals(List.of("feature-tag"), goodSuite.getTags(),
                "a suite that throws during tag matching must not prevent other suites from being enriched");
    }

    @Test
    void enrichTags_convertsCucumberJsonThenEnriches_returnsAdaptedSuites() {
        TestCaseModel tc = testCase("feature-a", "scenario one", "1.0");
        TestSuiteModel s = suite("feature-a.feature", tc);
        TestResultModel model = junitModel(s);

        List<TestSuiteModel> returned = KarateEnricher.enrichTags(model, CUCUMBER_JSON);

        assertNotNull(s.getTags());
        assertTrue(s.getTags().contains("feature-tag"),
                "public entry point must convert then enrich, same outcome as calling the converter and enrichTagsFromSuites separately");
        assertNotNull(tc.getTags());
        assertTrue(tc.getTags().contains("jira=SYNTH-1234"));

        assertEquals(1, returned.size());
        assertEquals("feature-a.feature", returned.get(0).getName());
    }

    @Test
    void enrichTags_nullCucumberJson_returnsEmptyListAndEnrichesNothing() {
        TestCaseModel tc = testCase("feature-a", "scenario one", "1.0");
        TestSuiteModel s = suite("feature-a.feature", tc);
        TestResultModel model = junitModel(s);

        List<TestSuiteModel> returned = assertDoesNotThrow(() -> KarateEnricher.enrichTags(model, null));

        assertEquals(0, returned.size());
        assertNull(s.getTags(), "no cucumber json means nothing to enrich");
    }
}

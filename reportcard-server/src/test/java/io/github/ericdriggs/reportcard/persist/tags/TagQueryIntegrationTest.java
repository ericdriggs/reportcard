package io.github.ericdriggs.reportcard.persist.tags;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.gen.db.tables.records.TestResultRecord;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.persist.TestResultPersistService;
import io.github.ericdriggs.reportcard.model.TagQueryResponse;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.*;

import static io.github.ericdriggs.reportcard.gen.db.Tables.TEST_RESULT;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for tag insertion and query using Testcontainers MySQL.
 * Verifies end-to-end flow: insert test_result with tags -> query via MEMBER OF.
 */
@SpringBootTest(classes = ReportcardApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class TagQueryIntegrationTest {

    private final TestResultPersistService testResultPersistService;
    private final TagQueryService tagQueryService;
    private final DSLContext dsl;

    private static final Random random = new Random();

    @Autowired
    public TagQueryIntegrationTest(TestResultPersistService testResultPersistService,
                                   TagQueryService tagQueryService,
                                   DSLContext dsl) {
        this.testResultPersistService = testResultPersistService;
        this.tagQueryService = tagQueryService;
        this.dsl = dsl;
    }

    @Test
    void insertTestResultWithTags_thenQueryByTag_returnsResult() {
        // Given: unique tag to avoid collision (max 25 chars for index)
        String uniqueTag = "tag" + random.nextInt(1000000);
        List<String> tags = List.of(uniqueTag, "smoke", "env=staging");

        TestResultModel testResult = createTestResultModel();
        StageDetails stageDetails = createUniqueStageDetails();

        // When: insert test result with tags
        StagePathTestResult inserted = testResultPersistService.insertTestResult(stageDetails, testResult, tags);

        // Then: verify insert succeeded
        assertNotNull(inserted);
        assertNotNull(inserted.getTestResult().getTestResultId());
        Long insertedId = inserted.getTestResult().getTestResultId();

        // When: query by the unique tag
        List<TestResultRecord> results = tagQueryService.findByTagExpression(uniqueTag);

        // Then: verify the inserted record is found
        assertFalse(results.isEmpty(), "Should find at least one result for tag: " + uniqueTag);

        boolean found = results.stream()
                .anyMatch(r -> r.getTestResultId().equals(insertedId));
        assertTrue(found, "Should find the inserted test result by tag query");
    }

    @Test
    void insertTestResultWithTags_thenQueryByAndExpression_returnsResult() {
        // Given: unique tags (max 25 chars for index)
        String uniqueTag1 = "andtag1-" + random.nextInt(1000000);
        String uniqueTag2 = "andtag2-" + random.nextInt(1000000);
        List<String> tags = List.of(uniqueTag1, uniqueTag2);

        TestResultModel testResult = createTestResultModel();
        StageDetails stageDetails = createUniqueStageDetails();

        // When: insert
        StagePathTestResult inserted = testResultPersistService.insertTestResult(stageDetails, testResult, tags);
        Long insertedId = inserted.getTestResult().getTestResultId();

        // When: query with AND expression
        String andExpression = uniqueTag1 + " AND " + uniqueTag2;
        List<TestResultRecord> results = tagQueryService.findByTagExpression(andExpression);

        // Then: should find the record
        assertFalse(results.isEmpty(), "Should find result for AND expression: " + andExpression);
        boolean found = results.stream()
                .anyMatch(r -> r.getTestResultId().equals(insertedId));
        assertTrue(found, "Should find the inserted test result by AND query");
    }

    @Test
    void insertTestResultWithTags_thenQueryByOrExpression_returnsResult() {
        // Given: unique tags (max 25 chars for index)
        String uniqueTag1 = "ortag1-" + random.nextInt(1000000);
        String nonExistentTag = "noexist-" + random.nextInt(1000000);
        List<String> tags = List.of(uniqueTag1);

        TestResultModel testResult = createTestResultModel();
        StageDetails stageDetails = createUniqueStageDetails();

        // When: insert
        StagePathTestResult inserted = testResultPersistService.insertTestResult(stageDetails, testResult, tags);
        Long insertedId = inserted.getTestResult().getTestResultId();

        // When: query with OR expression (one tag exists, one doesn't)
        String orExpression = uniqueTag1 + " OR " + nonExistentTag;
        List<TestResultRecord> results = tagQueryService.findByTagExpression(orExpression);

        // Then: should find the record via the existing tag
        assertFalse(results.isEmpty(), "Should find result for OR expression: " + orExpression);
        boolean found = results.stream()
                .anyMatch(r -> r.getTestResultId().equals(insertedId));
        assertTrue(found, "Should find the inserted test result by OR query");
    }

    @Test
    void insertTestResultWithKeyValueTag_thenQueryByKeyValue_returnsResult() {
        // Given: key=value tag (max 25 chars for index)
        String uniqueKeyValue = "env=t" + random.nextInt(1000000);
        List<String> tags = List.of(uniqueKeyValue);

        TestResultModel testResult = createTestResultModel();
        StageDetails stageDetails = createUniqueStageDetails();

        // When: insert
        StagePathTestResult inserted = testResultPersistService.insertTestResult(stageDetails, testResult, tags);
        Long insertedId = inserted.getTestResult().getTestResultId();

        // When: query by key=value
        List<TestResultRecord> results = tagQueryService.findByTagExpression(uniqueKeyValue);

        // Then: should find the record
        assertFalse(results.isEmpty(), "Should find result for key=value tag: " + uniqueKeyValue);
        boolean found = results.stream()
                .anyMatch(r -> r.getTestResultId().equals(insertedId));
        assertTrue(found, "Should find the inserted test result by key=value query");
    }

    @Test
    void insertTestResultWithLongTag_thenQueryByLongTag_returnsTruncatedMatch() {
        // Given: a tag longer than 25 characters
        int uniqueId = random.nextInt(1000000);
        String longTag = "long-tag-that-exceeds-25-chars-" + uniqueId;
        assertTrue(longTag.length() > 25, "Tag should be > 25 chars for this test");

        List<String> tags = List.of(longTag);

        TestResultModel testResult = createTestResultModel();
        StageDetails stageDetails = createUniqueStageDetails();

        // When: insert with long tag (should be truncated on storage)
        StagePathTestResult inserted = testResultPersistService.insertTestResult(stageDetails, testResult, tags);
        Long insertedId = inserted.getTestResult().getTestResultId();

        // When: query with the same long tag (should be truncated on query)
        List<TestResultRecord> results = tagQueryService.findByTagExpression(longTag);

        // Then: should find the record because both are truncated to same value
        assertFalse(results.isEmpty(),
                "Should find result when both insert and query truncate to same value");
        boolean found = results.stream()
                .anyMatch(r -> r.getTestResultId().equals(insertedId));
        assertTrue(found, "Should find the inserted test result via truncated tag match");
    }

    @Test
    void insertTestResultWithLongTag_thenQueryByTruncatedTag_returnsResult() {
        // Given: a tag longer than 25 characters
        int uniqueId = random.nextInt(1000000);
        String longTag = "long-tag-exceeds-25-" + uniqueId;
        String truncatedTag = longTag.substring(0, 25); // manually truncate

        List<String> tags = List.of(longTag);

        TestResultModel testResult = createTestResultModel();
        StageDetails stageDetails = createUniqueStageDetails();

        // When: insert with long tag
        StagePathTestResult inserted = testResultPersistService.insertTestResult(stageDetails, testResult, tags);
        Long insertedId = inserted.getTestResult().getTestResultId();

        // When: query with the truncated version
        List<TestResultRecord> results = tagQueryService.findByTagExpression(truncatedTag);

        // Then: should find the record
        assertFalse(results.isEmpty(),
                "Should find result when querying with truncated tag: " + truncatedTag);
        boolean found = results.stream()
                .anyMatch(r -> r.getTestResultId().equals(insertedId));
        assertTrue(found, "Should find the inserted test result via truncated tag query");
    }

    @Test
    void findByTagExpressionByPath_nonexistentCompany_returnsEmptyResponse() {
        // Given: a company that doesn't exist
        String nonexistentCompany = "nonexistent-company-" + UUID.randomUUID();

        // When: query with nonexistent company
        var results = tagQueryService.findByTagExpressionByPath(
                "smoke", nonexistentCompany, null, null, null, null);

        // Then: should return empty response, not throw
        assertNotNull(results);
        assertTrue(hasNoResults(results), "Should return empty response for nonexistent company");
    }

    @Test
    void findByTagExpressionByPath_existingCompanyNonexistentOrg_returnsEmptyResponse() {
        // Given: insert a test result to create a company
        TestResultModel testResult = createTestResultModel();
        StageDetails stageDetails = createUniqueStageDetails();
        testResultPersistService.insertTestResult(stageDetails, testResult, List.of("smoke"));

        // When: query with valid company but nonexistent org
        String nonexistentOrg = "nonexistent-org-" + UUID.randomUUID();
        var results = tagQueryService.findByTagExpressionByPath(
                "smoke", stageDetails.getCompany(), nonexistentOrg, null, null, null);

        // Then: should return empty response, not throw
        assertNotNull(results);
        assertTrue(hasNoResults(results), "Should return empty response for nonexistent org");
    }

    @Test
    void findByTagExpressionByPath_existingCompanyOrgNonexistentRepo_returnsEmptyResponse() {
        // Given: insert a test result to create company/org
        TestResultModel testResult = createTestResultModel();
        StageDetails stageDetails = createUniqueStageDetails();
        testResultPersistService.insertTestResult(stageDetails, testResult, List.of("smoke"));

        // When: query with valid company/org but nonexistent repo
        String nonexistentRepo = "nonexistent-repo-" + UUID.randomUUID();
        var results = tagQueryService.findByTagExpressionByPath(
                "smoke", stageDetails.getCompany(), stageDetails.getOrg(), nonexistentRepo, null, null);

        // Then: should return empty response, not throw
        assertNotNull(results);
        assertTrue(hasNoResults(results), "Should return empty response for nonexistent repo");
    }

    @Test
    void queryWithUnexpectedJsonStructure_handlesGracefully() {
        // Given: insert a test result with tags
        String uniqueTag = "malform-" + random.nextInt(1000000);
        List<String> tags = List.of(uniqueTag);

        TestResultModel testResult = createTestResultModel();
        StageDetails stageDetails = createUniqueStageDetails();

        StagePathTestResult inserted = testResultPersistService.insertTestResult(stageDetails, testResult, tags);
        Long insertedId = inserted.getTestResult().getTestResultId();

        // When: replace test_suites_json with valid JSON that doesn't match expected structure
        // Expected: array of objects with testCases array containing objects with name
        // Actual: object (not array) - this is valid JSON but wrong structure
        dsl.update(TEST_RESULT)
                .set(TEST_RESULT.TEST_SUITES_JSON, "{\"unexpected\": \"structure\"}")
                .where(TEST_RESULT.TEST_RESULT_ID.eq(insertedId))
                .execute();

        // Then: query should not throw
        var results = tagQueryService.findByTagExpressionByPath(
                uniqueTag, stageDetails.getCompany(), stageDetails.getOrg(),
                stageDetails.getRepo(), stageDetails.getBranch(), null);

        assertNotNull(results, "Should not throw on unexpected JSON structure");
        // Malformed JSON (not an array) cannot extract any tests → pruned
        assertTrue(hasNoResults(results), "Malformed JSON (not array) cannot extract tests");
    }

    @Test
    void queryWithNullTestCases_handlesGracefully() {
        // Given: insert a test result with tags
        String uniqueTag = "nulltc-" + random.nextInt(1000000);
        List<String> tags = List.of(uniqueTag);

        TestResultModel testResult = createTestResultModel();
        StageDetails stageDetails = createUniqueStageDetails();

        StagePathTestResult inserted = testResultPersistService.insertTestResult(stageDetails, testResult, tags);
        Long insertedId = inserted.getTestResult().getTestResultId();

        // When: replace test_suites_json with array containing object without testCases
        dsl.update(TEST_RESULT)
                .set(TEST_RESULT.TEST_SUITES_JSON, "[{\"name\": \"suite\"}]")
                .where(TEST_RESULT.TEST_RESULT_ID.eq(insertedId))
                .execute();

        // Then: query should not throw
        var results = tagQueryService.findByTagExpressionByPath(
                uniqueTag, stageDetails.getCompany(), stageDetails.getOrg(),
                stageDetails.getRepo(), stageDetails.getBranch(), null);

        assertNotNull(results, "Should not throw on missing testCases");
        // With empty container pruning, suite without testCases results in empty response
        // (no tests to extract → stage empty → pruned)
        assertTrue(hasNoResults(results), "Suite without testCases should be pruned");
    }

    @Test
    void insertTestResultWithoutTags_thenQueryByTag_doesNotReturnResult() {
        // Given: test result without tags (max 25 chars for index)
        String uniqueTag = "nomatch-" + random.nextInt(1000000);

        TestResultModel testResult = createTestResultModel();
        StageDetails stageDetails = createUniqueStageDetails();

        // When: insert WITHOUT tags
        StagePathTestResult inserted = testResultPersistService.insertTestResult(stageDetails, testResult);
        Long insertedId = inserted.getTestResult().getTestResultId();

        // When: query by the unique tag
        List<TestResultRecord> results = tagQueryService.findByTagExpression(uniqueTag);

        // Then: should NOT find the inserted record
        boolean found = results.stream()
                .anyMatch(r -> r.getTestResultId().equals(insertedId));
        assertFalse(found, "Should NOT find test result without matching tags");
    }

    // Helper methods

    private TestResultModel createTestResultModel() {
        TestResultModel testResult = new TestResultModel();
        testResult.setTests(1);
        testResult.setError(0);
        testResult.setFailure(0);
        testResult.setSkipped(0);
        testResult.setTime(new BigDecimal("1.0"));

        // Add a test suite with a test case
        TestSuiteModel testSuite = TestSuiteModel.builder()
                .name("TestSuite-" + UUID.randomUUID())
                .tests(1)
                .error(0)
                .failure(0)
                .skipped(0)
                .time(new BigDecimal("1.0"))
                .build();

        TestCaseModel testCase = TestCaseModel.builder()
                .name("testCase-" + UUID.randomUUID())
                .className("TestClass")
                .time(new BigDecimal("1.0"))
                .testStatus(TestStatus.SUCCESS)
                .testStatusFk(TestStatus.SUCCESS.getStatusId())
                .build();

        testSuite.setTestCases(List.of(testCase));
        testResult.setTestSuites(List.of(testSuite));

        return testResult;
    }

    private StageDetails createUniqueStageDetails() {
        long uniqueId = random.nextLong();
        TreeMap<String, String> jobInfo = new TreeMap<>();
        jobInfo.put("host", "test-host");
        jobInfo.put("pipeline", "test-pipeline-" + uniqueId);

        return StageDetails.builder()
                .company("company-" + uniqueId)
                .org("org-" + uniqueId)
                .repo("repo-" + uniqueId)
                .branch("branch-" + uniqueId)
                .sha("sha-" + uniqueId)
                .jobInfo(jobInfo)
                .runReference(UUID.randomUUID())
                .stage("stage-" + uniqueId)
                .build();
    }

    private boolean hasNoResults(TagQueryResponse response) {
        return (response.getOrgs() == null || response.getOrgs().isEmpty())
            && (response.getRepos() == null || response.getRepos().isEmpty())
            && (response.getBranches() == null || response.getBranches().isEmpty())
            && (response.getJobs() == null || response.getJobs().isEmpty());
    }

    @Test
    void insertTestResultWithTagsInJson_tagsPersistedInTestSuitesJson() {
        // Given: test result with tags embedded in test suite and test case
        String suiteTag = "suite-tag-" + random.nextInt(1000000);
        String testCaseTag = "tc-tag-" + random.nextInt(1000000);

        TestCaseModel testCase = TestCaseModel.builder()
                .name("testWithTags")
                .className("TestClass")
                .time(new BigDecimal("1.0"))
                .testStatus(TestStatus.SUCCESS)
                .testStatusFk(TestStatus.SUCCESS.getStatusId())
                .tags(List.of(testCaseTag))
                .build();

        TestSuiteModel testSuite = TestSuiteModel.builder()
                .name("SuiteWithTags")
                .tests(1)
                .error(0)
                .failure(0)
                .skipped(0)
                .time(new BigDecimal("1.0"))
                .tags(List.of(suiteTag))
                .testCases(List.of(testCase))
                .build();

        TestResultModel testResult = new TestResultModel();
        testResult.setTests(1);
        testResult.setError(0);
        testResult.setFailure(0);
        testResult.setSkipped(0);
        testResult.setTime(new BigDecimal("1.0"));
        testResult.setTestSuites(List.of(testSuite));

        // Flattened tags for test_result.tags column
        List<String> allTags = List.of(suiteTag, testCaseTag);

        StageDetails stageDetails = createUniqueStageDetails();

        // When: insert test result
        StagePathTestResult inserted = testResultPersistService.insertTestResult(stageDetails, testResult, allTags);
        Long insertedId = inserted.getTestResult().getTestResultId();

        // Then: verify test_suites_json contains the tags
        String storedJson = dsl.select(TEST_RESULT.TEST_SUITES_JSON)
                .from(TEST_RESULT)
                .where(TEST_RESULT.TEST_RESULT_ID.eq(insertedId))
                .fetchOne(TEST_RESULT.TEST_SUITES_JSON);

        assertNotNull(storedJson, "test_suites_json should not be null");

        // Verify suite tag is in the JSON
        assertTrue(storedJson.contains(suiteTag),
                "test_suites_json should contain suite tag: " + suiteTag + "\nActual JSON: " + storedJson);

        // Verify test case tag is in the JSON
        assertTrue(storedJson.contains(testCaseTag),
                "test_suites_json should contain test case tag: " + testCaseTag + "\nActual JSON: " + storedJson);
    }

    @Test
    void insertTestResultWithTagsInJson_queryFiltersCorrectly() {
        // Given: test result with different tags at suite and test case level
        String suiteTag = "stag-" + random.nextInt(1000000);
        String testCaseTag = "tctag-" + random.nextInt(1000000);

        TestCaseModel testCase = TestCaseModel.builder()
                .name("taggedTest")
                .className("TaggedClass")
                .time(new BigDecimal("1.0"))
                .testStatus(TestStatus.SUCCESS)
                .testStatusFk(TestStatus.SUCCESS.getStatusId())
                .tags(List.of(testCaseTag))
                .build();

        TestSuiteModel testSuite = TestSuiteModel.builder()
                .name("TaggedSuite")
                .tests(1)
                .error(0)
                .failure(0)
                .skipped(0)
                .time(new BigDecimal("1.0"))
                .tags(List.of(suiteTag))
                .testCases(List.of(testCase))
                .build();

        TestResultModel testResult = new TestResultModel();
        testResult.setTests(1);
        testResult.setError(0);
        testResult.setFailure(0);
        testResult.setSkipped(0);
        testResult.setTime(new BigDecimal("1.0"));
        testResult.setTestSuites(List.of(testSuite));

        List<String> allTags = List.of(suiteTag, testCaseTag);

        StageDetails stageDetails = createUniqueStageDetails();
        testResultPersistService.insertTestResult(stageDetails, testResult, allTags);

        // When: query by suite tag
        var results = tagQueryService.findByTagExpressionByPath(
                suiteTag, stageDetails.getCompany(), stageDetails.getOrg(),
                stageDetails.getRepo(), stageDetails.getBranch(), null);

        // Then: should find the result with the tagged test
        assertFalse(hasNoResults(results), "Should find results when querying by suite tag");

        // Verify the test is included (suite tag means all tests in suite match)
        var jobs = results.getJobs();
        assertNotNull(jobs);
        assertFalse(jobs.isEmpty());
        var tests = jobs.get(0).getRuns().get(0).getStages().get(0).getTests();
        assertTrue(tests.stream().anyMatch(t -> "taggedTest".equals(t.getTestName())),
                "Should include test from suite with matching tag");
    }
}

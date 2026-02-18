package io.github.ericdriggs.reportcard.persist.tags;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.gen.db.tables.records.TestResultRecord;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.persist.TestResultPersistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.*;

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

    private static final Random random = new Random();

    @Autowired
    public TagQueryIntegrationTest(TestResultPersistService testResultPersistService,
                                   TagQueryService tagQueryService) {
        this.testResultPersistService = testResultPersistService;
        this.tagQueryService = tagQueryService;
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
}

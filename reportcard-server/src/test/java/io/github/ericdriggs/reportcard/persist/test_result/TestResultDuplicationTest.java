package io.github.ericdriggs.reportcard.persist.test_result;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.model.StageDetails;
import io.github.ericdriggs.reportcard.model.StagePathTestResult;
import io.github.ericdriggs.reportcard.model.TestResultModel;
import io.github.ericdriggs.reportcard.model.converter.JunitSurefireXmlParseUtil;
import io.github.ericdriggs.reportcard.persist.TestResultPersistService;
import io.github.ericdriggs.reportcard.xml.ResourceReaderComponent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ReportcardApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class TestResultDuplicationTest {

    @Autowired
    TestResultPersistService testResultPersistService;

    @Autowired
    ResourceReaderComponent resourceReader;

    private StageDetails stageDetails(String stageName) {
        return StageDetails.builder()
                .company(TestData.company)
                .org(TestData.org)
                .repo(TestData.repo)
                .branch(TestData.branch)
                .sha(TestData.sha)
                .jobInfo(TestData.jobInfo)
                .runReference(UUID.randomUUID())
                .stage(stageName)
                .build();
    }

    private TestResultModel parsedJunit() {
        String xml = resourceReader.resourceAsString(TestResultPersistServiceTest.junitXmlPath);
        return JunitSurefireXmlParseUtil.parseTestXml(List.of(xml));
    }

    @Test
    void sameCountsDifferentTime_returnsExistingRow() {
        StageDetails stage = stageDetails("dupTimeStage-" + UUID.randomUUID());

        TestResultModel first = parsedJunit();
        StagePathTestResult inserted = testResultPersistService.insertTestResult(stage, first, null);
        Long firstId = inserted.getTestResult().getTestResultId();
        assertNotNull(firstId);

        TestResultModel second = parsedJunit();
        second.getTestSuites().get(0).setTime(new BigDecimal("999.000"));
        second.getTestSuites().get(0).getTestCases().get(0).setTime(new BigDecimal("999.000"));
        second.updateTotalsFromTestSuites();

        // precondition: rounded time really differs, counts do not
        assertNotEquals(first.getResultCount().getTime().intValue(),
                second.getResultCount().getTime().intValue(), "fixture must differ in time");
        assertEquals(first.getResultCount().getTests(), second.getResultCount().getTests());

        StagePathTestResult retried = assertDoesNotThrow(
                () -> testResultPersistService.insertTestResult(stage, second, null),
                "time-only difference must not fail the publish");
        assertEquals(firstId, retried.getTestResult().getTestResultId(),
                "must return the already-inserted row");
    }

    @Test
    void duplicateRetry_preservesOriginalTimingNotNewRequestTiming() {
        StageDetails stage = stageDetails("dupTimingStage-" + UUID.randomUUID());

        TestResultModel first = parsedJunit();
        Instant firstStart = Instant.ofEpochMilli(1700000000000L);
        Instant firstEnd = Instant.ofEpochMilli(1700000060000L);
        first.setStartTime(firstStart);
        first.setEndTime(firstEnd);
        StagePathTestResult inserted = testResultPersistService.insertTestResult(stage, first, null);
        Long firstId = inserted.getTestResult().getTestResultId();
        assertNotNull(firstId);

        // simulate a later republish without Karate timing (e.g. a JUnit-only retry)
        TestResultModel second = parsedJunit();
        second.setStartTime(null);
        second.setEndTime(null);

        StagePathTestResult retried = testResultPersistService.insertTestResult(stage, second, null);
        assertEquals(firstId, retried.getTestResult().getTestResultId(), "must return the already-inserted row");
        assertEquals(firstStart, retried.getTestResult().getStartTime(),
                "a duplicate retry must not erase timing established by the original publish");
        assertEquals(firstEnd, retried.getTestResult().getEndTime());
    }

    @Test
    void differentCounts_throwsWithReadableDiffs() {
        StageDetails stage = stageDetails("dupCountStage-" + UUID.randomUUID());

        TestResultModel first = parsedJunit();
        testResultPersistService.insertTestResult(stage, first, null);

        TestResultModel second = parsedJunit();
        second.getTestSuites().get(0).getTestCases().remove(0);
        second.getTestSuites().get(0).setTests(second.getTestSuites().get(0).getTestCases().size());
        second.updateTotalsFromTestSuites();
        assertNotEquals(first.getResultCount().getTests(), second.getResultCount().getTests(),
                "fixture must differ in test count");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> testResultPersistService.insertTestResult(stage, second, null));
        assertTrue(ex.getMessage().contains("tests"),
                "message must include the differing field: " + ex.getMessage());
        assertFalse(ex.getMessage().contains("{}"),
                "message must not contain a stray logger placeholder: " + ex.getMessage());
    }
}

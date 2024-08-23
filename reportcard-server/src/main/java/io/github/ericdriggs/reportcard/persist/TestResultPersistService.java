package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.gen.db.tables.records.*;
import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.model.TestResultModel;
import io.github.ericdriggs.reportcard.model.TestSuiteModel;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.model.converter.JunitSurefireXmlParseUtil;
import io.github.ericdriggs.reportcard.model.StagePathTestResult;
import io.github.ericdriggs.reportcard.xml.ResultCount;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

import static io.github.ericdriggs.reportcard.gen.db.Tables.*;

/**
 * Main db service class.
 * For every method which returns a single object, if <code>NULL</code> will throw
 * <code>ResponseStatusException(HttpStatus.NOT_FOUND)</code>
 */

@Service
@Slf4j
@SuppressWarnings({"unused", "ConstantConditions"})
public class TestResultPersistService extends StagePathPersistService {

    //protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TestResultPersistService(DSLContext dsl) {
        super(dsl);

    }

    public StagePathTestResult doPostXml(StageDetails stageDetails, MultipartFile file) {
        final String xmlString = fileToString(file);
        return doPostXmlString(stageDetails, xmlString);
    }

    public StagePathTestResult doPostXml(Long runId, String stageName, MultipartFile file) {

        String xmlString = fileToString(file);

        return doPostXmlString(runId, stageName, xmlString);
    }

    public StagePathTestResult doPostXmlString(StageDetails stageDetails, String xmlString) {
        TestResultModel testResult = JunitSurefireXmlParseUtil.parseTestXml(List.of(xmlString));
        testResult.setExternalLinks(stageDetails.getExternalLinksJson());
        return insertTestResult(stageDetails, testResult);
    }

    public StagePathTestResult doPostXmlString(Long runId, String stageName, String xmlString) {
        StagePath stagePath = getOrInsertStage(runId, stageName);

        Map<StagePath, TestResultModel> stagePathTestResultMap = null;
        TestResultModel testResult = JunitSurefireXmlParseUtil.parseTestXml(List.of(xmlString));
        return insertTestResult(stagePath, testResult);
    }

    public StagePathTestResult doPostXmlString(Long runId, String stageName, Path xmlPath) {
        StagePath stagePath = getOrInsertStage(runId, stageName);

        Map<StagePath, TestResultModel> stagePathTestResultMap = null;
        TestResultModel testResult = fromXmlPath(xmlPath);
        return insertTestResult(stagePath, testResult);
    }

    @SneakyThrows(IOException.class)
    public TestResultModel fromXmlPath(Path xmlPath)  {

        final String testXml =Files.readString(xmlPath);
        return  JunitSurefireXmlParseUtil.parseTestXml(List.of(testXml));
    }

    public StagePathTestResult insertTestResult(StageDetails reportMetaData, TestResultModel testResult) {
        StagePath stagePath = getUpsertedStagePath(reportMetaData);
        return insertTestResult(stagePath, testResult);
    }

    public StagePathTestResult insertTestResult(StagePath stagePath, TestResultModel testResult) {
        testResult.setStageFk(stagePath.getStage().getStageId());
        TestResultModel inserted = insertTestResult(testResult);

        //TODO: ensure StagePath job.lastRun is not stale and then refactor to update lastRun from job instead of now
        //this current approach results in time differences between lastRun in stagePath and stageDetails
        Instant lastRun = updateLastRunToNow(stagePath);
        stagePath.updateLastRun(lastRun);
        setIsRunSuccess(stagePath);
        return StagePathTestResult.builder().stagePath(stagePath).testResult(inserted).build();
    }

    public static String fileToString(MultipartFile file) {
        try {
            return new String(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TestResultModel getTestResult(Long testResultId) {

        Result<Record> recordResult = dsl.select()
                                         .from(TEST_RESULT)
                                         .where(TEST_RESULT.TEST_RESULT_ID.eq(testResultId))
                                         .fetch();

        return testResultFromRecords(recordResult);

    }

    public Set<TestResultModel> getTestResults(Long stageId) {

        Result<Record> recordResult = dsl.select()
                                         .from(TEST_RESULT)
                                         .where(TEST_RESULT.STAGE_FK.eq(stageId))
                                         .orderBy(TEST_RESULT.TEST_RESULT_ID.asc())
                                         .fetch();

        return testResultsFromRecords(recordResult);

    }

    public static TestResultModel testResultFromRecords(Result<Record> recordResult) {
        Set<TestResultModel> testResultModels = testResultsFromRecords(recordResult);
        if (testResultModels.isEmpty()) {
            return null;
        } else if (testResultModels.size() == 1) {
            return testResultModels.iterator().next();
        } else {
            throw new IllegalStateException("expected 0 or 1 testResultModels, actual size: " + testResultModels.size());
        }
    }

    public static Set<TestResultModel> testResultsFromRecords(Result<Record> recordResult) {
        Set<TestResultModel> testResults = new LinkedHashSet<>();
        TestResultModel prevTestResult = null;
        TestSuiteModel prevTestSuite = null;
        TestCaseModel prevTestCase = null;
        TestCaseFaultModel prevTestCaseFault = null;

        for(Record record : recordResult) {

            TestResultRecord testResultRecord = record.into(TestResultRecord.class);
            TestResultModel testResult = testResultRecord.into(TestResultModel.class);

            testResult.setTestSuitesJson(testResultRecord.getTestSuitesJson());
            TestSuiteModel testSuite = record.into(TestSuiteRecord.class).into(TestSuiteModel.class);
            List<TestSuiteModel> testSuiteModels = TestSuiteModel.fromJson(testResultRecord.getTestSuitesJson());
            testResult.setTestSuites(testSuiteModels);
            testResults.add(testResult);
        }

        for (TestResultModel testResult : testResults) {
            testResult.updateTotalsFromTestSuites();
        }
        return testResults;
    }

    public TestResultModel insertTestResult(TestResultModel testResult) {

        if (testResult.getStageFk() == null) {
            throw new NullPointerException("testResult.getStageFk()");
        }

        Set<TestResultModel> dbTestResults = getTestResults(testResult.getStageFk());
        if (!dbTestResults.isEmpty()) {
            for (TestResultModel dbTestResult : dbTestResults) {
                if (dbTestResult.getResultCount().compareTo(testResult.getResultCount()) == 0) {
                    log.info("testResult matches dbTestResult. Returning dbTestResult");
                    return dbTestResult;
                } else {
                    final String diffs = String.join(", ", ResultCount.diff(testResult.getResultCount(), dbTestResult.getResultCount()));
                    final String errorString = "Test result already inserted does not match request. diffs: {}" + diffs;
                    //noinspection LoggingPlaceholderCountMatchesArgumentCount //false positive
                    log.error(errorString);
                    throw new IllegalArgumentException(errorString);
                }
            }
        }

        if (testResult.getTestResultId() == null) {
            List<TestSuiteModel> testSuites = testResult.getTestSuites();
            TestResultRecord testResultRecord = dsl.newRecord(TEST_RESULT);

            testResultRecord.setStageFk(testResult.getStageFk())
                    .setError(testResult.getError())
                    .setFailure(testResult.getFailure())
                    .setSkipped(testResult.getSkipped())
                    .setTests(testResult.getTests())
                    .setTime(testResult.getTime())
                    .setExternalLinks(testResult.getExternalLinks())
                    .setTestSuitesJson(TestSuiteModel.asJsonWithTruncatedErrorMessages(testSuites))
                    .store();

            testResult = testResultRecord.into(TestResultModel.class);

            //need select for generated values
            testResult = dsl.select().from(TEST_RESULT)
                    .where(TEST_RESULT.TEST_RESULT_ID.eq(testResult.getTestResultId()))
                    .fetchOne()
                    .into(TestResultRecord.class).into(TestResultModel.class);
            testResult.setTestSuites(testSuites);
        }

        if (testResult.getTestSuites().isEmpty()) {
            log.warn("testSuites.isEmpty()");
        }

        return testResult;
    }

    public Map<Byte, String> getTestStatusMap() {
        List<TestStatusRecord> testStatusRecords = dsl.
                select(TEST_STATUS.fields())
                .from(TEST_STATUS)
                .fetchInto(TestStatusRecord.class);

        Map<Byte, String> testStatusMap = new TreeMap<>();
        for (TestStatusRecord testStatusRecord : testStatusRecords) {
            testStatusMap.put(testStatusRecord.getTestStatusId(), testStatusRecord.getTestStatusName());
        }

        return testStatusMap;
    }

}
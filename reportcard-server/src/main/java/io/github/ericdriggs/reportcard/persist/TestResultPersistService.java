package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.gen.db.tables.records.*;
import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.model.TestResultModel;
import io.github.ericdriggs.reportcard.model.TestSuiteModel;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.model.converter.JunitSurefireXmlParseUtil;
import io.github.ericdriggs.reportcard.model.StagePathTestResult;
import io.github.ericdriggs.reportcard.util.truncate.TruncateUtils;
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
                                         .leftJoin(TEST_SUITE).on(TEST_SUITE.TEST_RESULT_FK.eq(TEST_RESULT.TEST_RESULT_ID))
                                         .leftJoin(TEST_CASE).on(TEST_CASE.TEST_SUITE_FK.eq(TEST_SUITE.TEST_SUITE_ID))
                                         .leftJoin(TEST_CASE_FAULT).on(TEST_CASE_FAULT.TEST_CASE_FK.eq(TEST_CASE.TEST_CASE_ID))
                                         .where(TEST_RESULT.TEST_RESULT_ID.eq(testResultId))
                                         .orderBy(TEST_RESULT.TEST_RESULT_ID, TEST_SUITE.TEST_SUITE_ID, TEST_CASE.TEST_CASE_ID, TEST_CASE_FAULT.TEST_CASE_FAULT_ID)
                                         .fetch();

        return testResultFromRecords(recordResult);

    }

    public Set<TestResultModel> getTestResults(Long stageId) {

        Result<Record> recordResult = dsl.select()
                                         .from(TEST_RESULT)
                                         .leftJoin(TEST_SUITE).on(TEST_SUITE.TEST_RESULT_FK.eq(TEST_RESULT.TEST_RESULT_ID))
                                         .leftJoin(TEST_CASE).on(TEST_CASE.TEST_SUITE_FK.eq(TEST_SUITE.TEST_SUITE_ID))
                                         .leftJoin(TEST_CASE_FAULT).on(TEST_CASE_FAULT.TEST_CASE_FK.eq(TEST_CASE.TEST_CASE_ID))
                                         .where(TEST_RESULT.STAGE_FK.eq(stageId))
                                         .orderBy(TEST_RESULT.TEST_RESULT_ID, TEST_SUITE.TEST_SUITE_ID, TEST_CASE.TEST_CASE_ID, TEST_CASE_FAULT.TEST_CASE_FAULT_ID)
                                         .fetch();

        return testResultsFromRecords(recordResult);

    }

    public static TestResultModel testResultFromRecords(Result<Record> recordResult) {
        Set<TestResultModel> testResultModels = testResultsFromRecords(recordResult);
        if (testResultModels.size() == 0) {
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

            TestResultModel testResult = record.into(TestResultRecord.class).into(TestResultModel.class);
            TestSuiteModel testSuite = record.into(TestSuiteRecord.class).into(TestSuiteModel.class);
            TestCaseModel testCase = record.into(TestCaseRecord.class).into(TestCaseModel.class);
            TestCaseFaultModel testCaseFault = record.into(TestCaseFaultRecord.class).into(TestCaseFaultModel.class);

            if (testResult.getTestResultId() == null || testSuite.getTestSuiteId() == null || testCase.getTestCaseId() == null) {
                log.warn("null ids skipping record testResult.getTestResultId(): {}, testCase.getTestCaseId() : {}, testCase.getTestCaseId(): {}",
                        testResult.getTestResultId(), testCase.getTestCaseId(), testCase.getTestCaseId());
                continue;
            }

            if (prevTestResult == null || !prevTestResult.getTestResultId().equals(testResult.getTestResultId())) {
                testResults.add(testResult);
                prevTestResult = testResult;
            } else {
                testResult = prevTestResult;
            }

            if (prevTestSuite == null || !prevTestSuite.getTestSuiteId().equals(testSuite.getTestSuiteId())) {
                testResult.addTestSuite(testSuite);
                prevTestSuite = testSuite;
            } else {
                testSuite = prevTestSuite;
            }

            //always add test case to current test suite
            if (prevTestCase == null || !prevTestCase.getTestCaseId().equals(testCase.getTestCaseId())) {
                testSuite.addTestCase(testCase);
                prevTestCase = testCase;

            } else {
                testCase = prevTestCase;
            }

            if (testCaseFault.getTestCaseFaultId() != null) {
                //always add test case to current test suite
                if (prevTestCaseFault == null || !prevTestCaseFault.getTestCaseFaultId().equals(testCaseFault.getTestCaseFaultId())) {
                    testCase.addTestCaseFault(testCaseFault);
                    prevTestCaseFault = testCaseFault;
                } else {
                    testCaseFault = prevTestCaseFault;
                }
            }
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

        List<TestSuiteModel> testSuites = new ArrayList<>();
        for (TestSuiteModel testSuite : testResult.getTestSuites()) {
            if (testSuite.getTestSuiteId() == null) {
                List<TestCaseModel> testCases = testSuite.getTestCases();
                TestSuiteRecord testSuiteRecord = dsl.newRecord(TEST_SUITE);
                testSuiteRecord.setTestResultFk(testResult.getTestResultId())
                        .setName(TruncateUtils.truncateBytes(testSuite.getName(), 1024))
                        .setError(testSuite.getError())
                        .setFailure(testSuite.getFailure())
                        .setSkipped(testSuite.getSkipped())
                        .setTests(testSuite.getTests())
                        .setTime(testSuite.getTime())
                        .setPackageName(TruncateUtils.truncateBytes(testSuite.getPackageName(), 1024))
                        .setGroup(TruncateUtils.truncateBytes(testSuite.getGroup(), 1024))
                        .store();

                //need select for generated values
                testSuite = dsl.select().from(TEST_SUITE)
                        .where(TEST_SUITE.TEST_SUITE_ID.eq(testSuiteRecord.getTestSuiteId()))
                        .fetchOne()
                        .into(TestSuiteRecord.class).into(TestSuiteModel.class);

                testSuite.setTestCases(testCases);
                testSuites.add(testSuite);
            }

            if (testSuite.getTestCases().isEmpty()) {
                log.warn("testCases.isEmpty()");
            }

            final List<TestCaseModel> testCases = new ArrayList<>();
            for (TestCaseModel testCase : testSuite.getTestCases()) {
                if (testCase.getTestCaseId() == null) {
                    TestCaseRecord testCaseRecord = dsl.newRecord(TEST_CASE);
                    testCaseRecord.setTestSuiteFk(testSuite.getTestSuiteId())
                            .setTestStatusFk(testCase.getTestStatusFk())
                            .setClassName(TruncateUtils.truncateBytes(testCase.getClassName(), 1024))
                            .setName(TruncateUtils.truncateBytes(testCase.getName(), 1024))
                            .setTime(testCase.getTime())
                            .setTestCaseId(testCase.getTestCaseId()) //redundant? shouldn't store set the id?
                            .store();

                    List<TestCaseFaultModel> testCaseFaults = new ArrayList<>();
                    for (TestCaseFaultModel testCaseFault : testCase.getTestCaseFaults()) {
                        if (testCaseFault.getTestCaseFaultId() == null) {
                            TestCaseFaultRecord testCaseFaultRecord = dsl.newRecord(TEST_CASE_FAULT);
                            final String message = TruncateUtils.truncateBytes(testCaseFault.getMessage(), 1024);
                            final String value;
                            if (testCaseFault.getValue() == null) {
                                value = testCaseFault.getMessage();
                            } else {
                                value = testCaseFault.getValue();
                            }
                            testCaseFaultRecord
                                    .setMessage(message)
                                    .setTestCaseFk(testCaseRecord.getTestCaseId())
                                    .setType(TruncateUtils.truncateBytes(testCaseFault.getType(), 1024))
                                    .setValue(value)
                                    .setFaultContextFk(testCaseFault.getFaultContextFk())
                                    .store();
                            testCaseFault.setTestCaseFaultId(testCaseFaultRecord.getTestCaseFaultId());
                            testCaseFaults.add(testCaseFaultRecord.into(TestCaseFaultModel.class));
                        }
                    }
                    testCase = testCaseRecord.into(TestCaseModel.class);
                    testCase.setTestCaseFaults(testCaseFaults);
                    testCases.add(testCase);
                }
            }
            testSuite.setTestCases(testCases);
        }
        testResult.setTestSuites(testSuites);

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
package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.gen.db.tables.records.*;
import io.github.ericdriggs.reportcard.model.TestCaseModel;
import io.github.ericdriggs.reportcard.model.TestResultModel;
import io.github.ericdriggs.reportcard.model.TestSuiteModel;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.model.converter.junit.JunitConvertersUtil;
import io.github.ericdriggs.reportcard.model.StagePathTestResult;
import io.github.ericdriggs.reportcard.xml.XmlUtil;
import io.github.ericdriggs.reportcard.xml.junit.JunitParserUtil;
import io.github.ericdriggs.reportcard.xml.junit.Testsuites;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

import static io.github.ericdriggs.reportcard.gen.db.Tables.*;

/**
 * Main db service class.
 * For every method which returns a single object, if <code>NULL</code> will throw
 * <code>ResponseStatusException(HttpStatus.NOT_FOUND)</code>
 */

@Service
@SuppressWarnings({"unused", "ConstantConditions"})
public class TestResultPersistService extends StagePathPersistService {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

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
        TestResultModel testResult = fromXmlString(xmlString);
        testResult.setExternalLinks(stageDetails.getExternalLinksJson());
        return insertTestResult(stageDetails, testResult);
    }

    public StagePathTestResult doPostXmlString(Long runId, String stageName, String xmlString) {
        StagePath stagePath = getOrInsertStage(runId, stageName);

        Map<StagePath, TestResultModel> stagePathTestResultMap = null;
        TestResultModel testResult = fromXmlString(xmlString);
        return insertTestResult(stagePath, testResult);
    }

    public StagePathTestResult doPostXmlString(Long runId, String stageName, Path xmlPath) {
        StagePath stagePath = getOrInsertStage(runId, stageName);

        Map<StagePath, TestResultModel> stagePathTestResultMap = null;
        TestResultModel testResult = fromXmlPath(xmlPath);
        return insertTestResult(stagePath, testResult);
    }

    public TestResultModel fromXmlString(String xmlString) {

        if (StringUtils.isEmpty(xmlString)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing file");
        }

        //TODO: check all strings -- handle mixed case of testsuite and testsuites
        String rootElementName = XmlUtil.getXmlRootElementName(xmlString);
        if ("testsuite".equals(rootElementName)) {
            return fromTestSuiteList(xmlString);
        } else if ("testsuites".equals(rootElementName)) {
            return fromTestSuites(xmlString);
        }
        throw new IllegalArgumentException("not list of junit xml");
    }

    @SneakyThrows(IOException.class)
    public TestResultModel fromXmlPath(Path xmlPath)  {

        return fromXmlString(Files.readString(xmlPath));
    }

    public TestResultModel fromTestSuites(String xmlString) {
        Testsuites testsuites = JunitParserUtil.parseTestSuites(xmlString);
        return JunitConvertersUtil.modelMapper.map(testsuites, TestResultModel.class);
    }

    public TestResultModel fromTestSuiteList(String xmlString) {
        Testsuites testsuites = JunitParserUtil.parseTestSuites(xmlString);
        return JunitConvertersUtil.doFromJunitToModelTestResult(testsuites);
    }

    public StagePathTestResult insertTestResult(StageDetails reportMetatData, TestResultModel testResult) {
        StagePath stagePath = getUpsertedStagePath(reportMetatData);
        return insertTestResult(stagePath, testResult);
    }

    public StagePathTestResult insertTestResult(StagePath stagePath, TestResultModel testResult) {
        testResult.setStageFk(stagePath.getStage().getStageId());
        TestResultModel inserted = insertTestResult(testResult);

        LocalDateTime lastRun = updateLastRunToNow(stagePath);
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

        TestResultModel testResult = dsl.
                select(TEST_RESULT.fields())
                .from(TEST_RESULT)
                .where(TEST_RESULT.TEST_RESULT_ID.eq(testResultId))
                .fetchOne().into(TestResultModel.class);

        List<TestSuiteModel> testSuites = dsl.
                select(TEST_SUITE.fields())
                .from(TEST_RESULT
                        .join(TEST_SUITE).on(TEST_SUITE.TEST_RESULT_FK.eq(TEST_RESULT.TEST_RESULT_ID))
                ).where(TEST_RESULT.TEST_RESULT_ID.eq(testResult.getTestResultId()))
                .fetchInto(TestSuiteModel.class);

        testResult.setTestSuites(testSuites);

        for (TestSuiteModel testSuite : testResult.getTestSuites()) {
            List<TestCaseModel> testCases = dsl.
                    select(TEST_CASE.fields())
                    .from(TEST_CASE
                            .join(TEST_SUITE).on(TEST_CASE.TEST_SUITE_FK.eq(TEST_SUITE.TEST_SUITE_ID))
                    ).where(TEST_SUITE.TEST_SUITE_ID.eq(testSuite.getTestSuiteId()))
                    .fetchInto(TestCaseModel.class);

            testSuite.setTestCases(testCases);
        }
        return testResult;
    }

    public Set<TestResultModel> getTestResults(Long stageId) {

        Set<TestResultModel> testResults = new TreeSet<>(ModelComparators.TEST_RESULT_MODEL_CASE_INSENSITIVE_ORDER);
        testResults.addAll(
                dsl.
                        select(TEST_RESULT.fields())
                        .from(STAGE
                                .join(TEST_RESULT).on(TEST_RESULT.STAGE_FK.eq(STAGE.STAGE_ID))
                        ).where(STAGE.STAGE_ID.eq(stageId))
                        .fetchInto(TestResultModel.class));

        for (TestResultModel testResult : testResults) {

            List<TestSuiteModel> testSuites = dsl.
                    select(TEST_SUITE.fields())
                    .from(TEST_RESULT
                            .join(TEST_SUITE).on(TEST_SUITE.TEST_RESULT_FK.eq(TEST_RESULT.TEST_RESULT_ID))
                    ).where(TEST_RESULT.TEST_RESULT_ID.eq(testResult.getTestResultId()))
                    .fetchInto(TestSuiteModel.class);

            testResult.setTestSuites(testSuites);

            for (TestSuiteModel testSuite : testResult.getTestSuites()) {
                List<TestCaseModel> testCases = dsl.
                        select(TEST_CASE.fields())
                        .from(TEST_CASE
                                .join(TEST_SUITE).on(TEST_CASE.TEST_SUITE_FK.eq(TEST_SUITE.TEST_SUITE_ID))
                        ).where(TEST_SUITE.TEST_SUITE_ID.eq(testSuite.getTestSuiteId()))
                        .fetchInto(TestCaseModel.class);

                testSuite.setTestCases(testCases);
            }

        }
        return testResults;
    }

    public TestResultModel insertTestResult(TestResultModel testResult) {

        if (testResult.getStageFk() == null) {
            throw new NullPointerException("testResult.getStageFk()");
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
                        .setName(testSuite.getName())
                        .setError(testSuite.getError())
                        .setFailure(testSuite.getFailure())
                        .setSkipped(testSuite.getSkipped())
                        .setTests(testSuite.getTests())
                        .setTime(testSuite.getTime())
                        .setPackageName(testSuite.getPackageName())

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
                            .setClassName(testCase.getClassName())
                            .setName(testCase.getName())
                            .setTime(testCase.getTime())
                            .setTestCaseId(testCase.getTestCaseId())
                            .store();

                    testCase = testCaseRecord.into(TestCaseModel.class);
                    testCases.add(testCase);

                    for (TestCaseFaultModel testCaseFault : testCase.getTestCaseFaults()) {
                        if (testCaseFault.getTestCaseFaultId() == null) {
                            TestCaseFaultRecord testCaseFaultRecord = dsl.newRecord(TEST_CASE_FAULT);
                            testCaseFault.setTestCaseFaultId(testCaseFaultRecord.getTestCaseFaultId());
                            testCase.getTestCaseFaults().add(testCaseFault);
                        }
                    }
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
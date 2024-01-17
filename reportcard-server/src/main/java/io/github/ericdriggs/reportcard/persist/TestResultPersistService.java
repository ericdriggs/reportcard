package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.gen.db.tables.records.*;
import io.github.ericdriggs.reportcard.model.TestCase;
import io.github.ericdriggs.reportcard.model.TestResult;
import io.github.ericdriggs.reportcard.model.TestSuite;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.model.converter.junit.JunitConvertersUtil;
import io.github.ericdriggs.reportcard.xml.XmlUtil;
import io.github.ericdriggs.reportcard.xml.junit.JunitParserUtil;
import io.github.ericdriggs.reportcard.xml.junit.Testsuites;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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

    public Map<StagePath, TestResult> doPostXml(StageDetails reportMetatData, MultipartFile[] files) {
        List<String> xmlStrings = new ArrayList<>();
        Arrays.stream(files)
                .forEach(file -> xmlStrings.add(fileToString(file)));

        return doPostXmlStrings(reportMetatData, xmlStrings);
    }

    public Map<StagePath, TestResult> doPostXml(Long runId, String stageName, MultipartFile[] files) {

        List<String> xmlStrings = new ArrayList<>();
        Arrays.stream(files)
                .forEach(file -> xmlStrings.add(fileToString(file)));

        return doPostXmlStrings(runId, stageName, xmlStrings);
    }

    public Map<StagePath, TestResult> doPostXmlStrings(StageDetails stageDetails, List<String> xmlStrings) {
        stageDetails.validateAndSetDefaults();

        Map<StagePath, TestResult> stagePathTestResultMap = null;
        TestResult testResult = fromXmlStrings(xmlStrings);
        testResult.setExternalLinks(stageDetails.getExternalLinksJson());
        return insertTestResult(stageDetails, testResult);
    }

    public Map<StagePath, TestResult> doPostXmlStrings(Long runId, String stageName, List<String> xmlStrings) {
        StagePath stagePath = getOrInsertStage(runId, stageName);

        Map<StagePath, TestResult> stagePathTestResultMap = null;
        TestResult testResult = fromXmlStrings(xmlStrings);
        return insertTestResult(stagePath, testResult);
    }

    public TestResult fromXmlStrings(List<String> xmlStrings) {

        if (xmlStrings == null || xmlStrings.size() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing files");
        } else if (xmlStrings.size() > 1) {
            fromTestSuiteList(xmlStrings);
        } else { //xmlString.size == 1
            //TODO: check all strings -- handle mixed case of testsuite and testsuites
            String xmlString = xmlStrings.get(0);
            String rootElementName = XmlUtil.getXmlRootElementName(xmlString);
            if ("testsuite".equals(rootElementName)) {
                return fromTestSuiteList(xmlStrings);
            } else if ("testsuites".equals(rootElementName)) {
                return fromTestSuites(xmlString);
            }
        }
        throw new IllegalArgumentException("not list of junit xml");
    }

    public TestResult fromTestSuites(String xmlString) {
        Testsuites testsuites = JunitParserUtil.parseTestSuites(xmlString);
        return JunitConvertersUtil.modelMapper.map(testsuites, TestResult.class);
    }

    public TestResult fromTestSuiteList(List<String> xmlStrings) {
        Testsuites testsuites = JunitParserUtil.parseTestSuiteList(xmlStrings);
        return JunitConvertersUtil.doFromJunitToModelTestResult(testsuites);
    }

    public Map<StagePath, TestResult> insertTestResult(StageDetails reportMetatData, TestResult testResult) {
        StagePath stagePath = getUpsertedStagePath(reportMetatData);
        return insertTestResult(stagePath, testResult);
    }

    public Map<StagePath, TestResult> insertTestResult(StagePath stagePath, TestResult testResult) {
        testResult.setStageFk(stagePath.getStage().getStageId());
        TestResult inserted = insertTestResult(testResult);
        updateLastRunToNow(stagePath);
        return Collections.singletonMap(stagePath, inserted);
    }

    public static String fileToString(MultipartFile file) {
        try {
            return new String(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public TestResult getTestResult(Long testResultId) {

        TestResult testResult = dsl.
                select(TEST_RESULT.fields())
                .from(TEST_RESULT)
                .where(TEST_RESULT.TEST_RESULT_ID.eq(testResultId))
                .fetchOne().into(TestResult.class);

        List<TestSuite> testSuites = dsl.
                select(TEST_SUITE.fields())
                .from(TEST_RESULT
                        .join(TEST_SUITE).on(TEST_SUITE.TEST_RESULT_FK.eq(TEST_RESULT.TEST_RESULT_ID))
                ).where(TEST_RESULT.TEST_RESULT_ID.eq(testResult.getTestResultId()))
                .fetchInto(TestSuite.class);

        testResult.setTestSuites(testSuites);

        for (TestSuite testSuite : testResult.getTestSuites()) {
            List<TestCase> testCases = dsl.
                    select(TEST_CASE.fields())
                    .from(TEST_CASE
                            .join(TEST_SUITE).on(TEST_CASE.TEST_SUITE_FK.eq(TEST_SUITE.TEST_SUITE_ID))
                    ).where(TEST_SUITE.TEST_SUITE_ID.eq(testSuite.getTestSuiteId()))
                    .fetchInto(TestCase.class);

            testSuite.setTestCases(testCases);
        }
        return testResult;
    }

    public Set<TestResult> getTestResults(Long stageId) {

        Set<TestResult> testResults = new TreeSet<>(ModelComparators.TEST_RESULT_MODEL_CASE_INSENSITIVE_ORDER);
        testResults.addAll(
                dsl.
                        select(TEST_RESULT.fields())
                        .from(STAGE
                                .join(TEST_RESULT).on(TEST_RESULT.STAGE_FK.eq(STAGE.STAGE_ID))
                        ).where(STAGE.STAGE_ID.eq(stageId))
                        .fetchInto(TestResult.class));

        for (TestResult testResult : testResults) {

            List<TestSuite> testSuites = dsl.
                    select(TEST_SUITE.fields())
                    .from(TEST_RESULT
                            .join(TEST_SUITE).on(TEST_SUITE.TEST_RESULT_FK.eq(TEST_RESULT.TEST_RESULT_ID))
                    ).where(TEST_RESULT.TEST_RESULT_ID.eq(testResult.getTestResultId()))
                    .fetchInto(TestSuite.class);

            testResult.setTestSuites(testSuites);

            for (TestSuite testSuite : testResult.getTestSuites()) {
                List<TestCase> testCases = dsl.
                        select(TEST_CASE.fields())
                        .from(TEST_CASE
                                .join(TEST_SUITE).on(TEST_CASE.TEST_SUITE_FK.eq(TEST_SUITE.TEST_SUITE_ID))
                        ).where(TEST_SUITE.TEST_SUITE_ID.eq(testSuite.getTestSuiteId()))
                        .fetchInto(TestCase.class);

                testSuite.setTestCases(testCases);
            }

        }
        return testResults;
    }

    public TestResult insertTestResult(TestResult testResult) {

        if (testResult.getStageFk() == null) {
            throw new NullPointerException("testResult.getStageFk()");
        }

        if (testResult.getTestResultId() == null) {
            List<TestSuite> testSuites = testResult.getTestSuites();
            TestResultRecord testResultRecord = dsl.newRecord(TEST_RESULT);
            testResultRecord.setStageFk(testResult.getStageFk())
                    .setError(testResult.getError())
                    .setFailure(testResult.getFailure())
                    .setSkipped(testResult.getSkipped())
                    .setTests(testResult.getTests())
                    .setTime(testResult.getTime())
                    .setExternalLinks(testResult.getExternalLinks())
                    .store();

            testResult = testResultRecord.into(TestResult.class);

            //need select for generated values
            testResult = dsl.select().from(TEST_RESULT)
                    .where(TEST_RESULT.TEST_RESULT_ID.eq(testResult.getTestResultId()))
                    .fetchOne()
                    .into(TestResultRecord.class).into(TestResult.class);
            testResult.setTestSuites(testSuites);
        }

        if (testResult.getTestSuites().isEmpty()) {
            log.warn("testSuites.isEmpty()");
        }

        List<TestSuite> testSuites = new ArrayList<>();
        for (TestSuite testSuite : testResult.getTestSuites()) {
            if (testSuite.getTestSuiteId() == null) {
                List<TestCase> testCases = testSuite.getTestCases();
                TestSuiteRecord testSuiteRecord = dsl.newRecord(TEST_SUITE);
                testSuiteRecord.setTestResultFk(testResult.getTestResultId())
                        .setName(testSuite.getName())
                        .setError(testSuite.getError())
                        .setFailure(testSuite.getFailure())
                        .setSkipped(testSuite.getSkipped())
                        .setTests(testSuite.getTests())
                        .setTime(testSuite.getTime())
                        .setPackage(testSuite.getPackage())

                        .store();

                //need select for generated values
                testSuite = dsl.select().from(TEST_SUITE)
                        .where(TEST_SUITE.TEST_SUITE_ID.eq(testSuiteRecord.getTestSuiteId()))
                        .fetchOne()
                        .into(TestSuiteRecord.class).into(TestSuite.class);

                testSuite.setTestCases(testCases);
                testSuites.add(testSuite);
            }

            if (testSuite.getTestCases().isEmpty()) {
                log.warn("testCases.isEmpty()");
            }

            final List<TestCase> testCases = new ArrayList<>();
            for (TestCase testCase : testSuite.getTestCases()) {
                if (testCase.getTestCaseId() == null) {
                    TestCaseRecord testCaseRecord = dsl.newRecord(TEST_CASE);
                    testCaseRecord.setTestSuiteFk(testSuite.getTestSuiteId())
                            .setTestStatusFk(testCase.getTestStatusFk())
                            .setClassName(testCase.getClassName())
                            .setName(testCase.getName())
                            .setTime(testCase.getTime())
                            .setTestCaseId(testCase.getTestCaseId())
                            .store();

                    testCase = testCaseRecord.into(TestCase.class);
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
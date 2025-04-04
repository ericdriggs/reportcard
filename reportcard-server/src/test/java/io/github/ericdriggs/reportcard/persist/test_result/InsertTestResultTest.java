package io.github.ericdriggs.reportcard.persist.test_result;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.dto.TestCaseFault;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.persist.TestResultPersistService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Profile("test")
public class InsertTestResultTest extends AbstractTestResultPersistTest {

    final static String company = "company10";
    final static String org = "org10";
    final static String repo = "repo10";
    final static String branch = "branch10";
    final static String sha = "a5493474-274c-44bd-93a1-82e9df1c15d4";
    final static TreeMap<String, String> metadata = TestData.jobInfo;
    final UUID runReference = UUID.randomUUID();
    final static String stage = "stage10";

    final static int testResultErrorCount = 0;
    final static int testResultFailureCount = 1;
    final static int testResultSkippedCount = 0;
    final static int testResultTestCount = 1;
    final static BigDecimal testResultTime = new BigDecimal("0.5");

    final static int testSuiteErrorCount = 5;
    final static int testSuiteFailureCount = 6;
    final static int testSuiteSkippedCount = 7;
    final static int testSuiteTestCount = 8;
    final static BigDecimal testSuiteTime = new BigDecimal("1.690");
    final static String testSuitePackage = "com.foo.bar";

    final static String testSuiteName = "testSuiteName10";
    final static String testCaseClassName = "testCaseClassName10";
    final static String testCaseName = "testCaseName10";
    final static TestStatus testCaseStatus = TestStatus.FAILURE;
    final static BigDecimal testCaseTime = new BigDecimal("0.500");

    final String testCaseFaultValue = "value1";
    final String testCaseFaultMessage = "message1";
    final String testCaseFaultType = "type1";
    final FaultContext testCaseFaultContext = FaultContext.FAILURE;

    final static Map<String, String> externalLinksMap;

    static {
        externalLinksMap = new HashMap<>();
        externalLinksMap.put("foo", "http://www.foo.com");
        externalLinksMap.put("bar", "http://www.bar.com");
    }

    @Autowired
    public InsertTestResultTest(TestResultPersistService testResultPersistService) {
        super(testResultPersistService);
    }

    @Test
    public void insertTestResultTest() {

        final TestResultModel testResultBefore = getInsertableTestResult();
        assertValues(testResultBefore);

        final TestResultModel testResultInsert = testResultPersistService.insertTestResult(testResultBefore);
        assertInsertedTestResult(testResultBefore, testResultInsert);
    }

    @Test
    public void givenInsertedTestResult_WhenReinsert_ThenIdempotentSuccess() {

        final TestResultModel testResultBefore = getInsertableTestResult();
        assertValues(testResultBefore);

        final TestResultModel testResultInsert = testResultPersistService.insertTestResult(testResultBefore);
        final TestResultModel testResultInsert2 = testResultPersistService.insertTestResult(testResultBefore);
        assertInsertedTestResult(testResultBefore, testResultInsert);
        assertInsertedTestResult(testResultInsert, testResultInsert2);
    }

    void assertInsertedTestResult(TestResultModel testResultBefore, TestResultModel testResultInsert) {
        assertValues(testResultInsert);
        assertExternalLinks(testResultInsert);

        {
            final Set<TestResultModel> testResultsGet = testResultPersistService.getTestResults(testResultBefore.getStageFk());
            assertEquals(1, testResultsGet.size());
            final TestResultModel testResultGet = testResultsGet.iterator().next();
            assertValues(testResultGet);
            assertExternalLinks(testResultGet);
        }

        {
            TestResultModel testResultGet = testResultPersistService.getTestResult(testResultInsert.getTestResultId());
            assertValues(testResultGet);
            assertExternalLinks(testResultGet);
            assertNotNull(testResultGet.getTestSuitesJson());
            assertNotEquals("{}", testResultGet.getTestSuitesJson(), testResultInsert.getTestSuitesJson());
        }
    }

    private void assertValues(TestResultModel testResult) {

        List<TestSuiteModel> testSuites = testResult.getTestSuites();
        assertFalse(CollectionUtils.isEmpty(testSuites));
        assertEquals(1, testSuites.size());
        final TestSuiteModel testSuite = testResult.getTestSuites().get(0);

        List<TestCaseModel> testCases = testSuite.getTestCases();
        assertFalse(CollectionUtils.isEmpty(testCases));
        assertEquals(1, testCases.size());
        final TestCaseModel testCase = testSuite.getTestCases().get(0);

        List<TestCaseFaultModel> testCaseFaults = testCase.getTestCaseFaults();
        assertFalse(testCaseFaults.isEmpty());
        assertEquals(1, testCaseFaults.size());
        final TestCaseFault testCaseFault = testCase.getTestCaseFaults().get(0);

        assertEquals(testResultErrorCount, testResult.getError());
        assertEquals(testResultFailureCount, testResult.getFailure());
        assertEquals(testResultSkippedCount, testResult.getSkipped());
        assertEquals(testResultTestCount, testResult.getTests());
        assertThat(testResultTime, Matchers.comparesEqualTo(testResult.getTime()));

        assertEquals(testSuiteErrorCount, testSuite.getError());
        assertEquals(testSuiteFailureCount, testSuite.getFailure());
        assertEquals(testSuiteSkippedCount, testSuite.getSkipped());
        assertEquals(testSuiteName, testSuite.getName());
        assertEquals(testSuiteTestCount, testSuite.getTests());
        assertThat(testSuiteTime, Matchers.comparesEqualTo(testSuite.getTime()));
        assertEquals(testSuitePackage, testSuite.getPackageName());

        assertEquals(testCaseClassName, testCase.getClassName());
        assertEquals(testCaseName, testCase.getName());
        assertEquals(testCaseStatus, testCase.getTestStatus());
        assertEquals(testCaseStatus.getStatusId(), testCase.getTestStatusFk());
        assertThat(testCaseTime, Matchers.comparesEqualTo(testCase.getTime()));

        assertEquals(testCaseFaultContext.getFaultContextId(), testCaseFault.getFaultContextFk());
        assertEquals(testCaseFaultMessage, testCaseFault.getMessage());
        assertEquals(testCaseFaultType, testCaseFault.getType());
        assertEquals(testCaseFaultValue, testCaseFault.getValue());
    }

    final static ObjectMapper objectMapper = new ObjectMapper();

    private void assertExternalLinks(TestResultModel testResult) {
        String jsonString = testResult.getExternalLinks();
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {
        };
        try {
            Map<String, String> actualExternalLinksMap = objectMapper.readValue(jsonString, typeRef);
            assertEquals(externalLinksMap, actualExternalLinksMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private StageDetails getStageDetails() {

        return StageDetails.builder()
                .company(company)
                .org(org)
                .repo(repo)
                .branch(branch)
                .sha(sha)
                .jobInfo(metadata)
                .runReference(runReference)
                .stage(stage)
                .build();

    }

    private TestResultModel getInsertableTestResult() {

        TestResultModel testResult = new TestResultModel();
        testResult.setError(testResultErrorCount);
        testResult.setFailure(testResultFailureCount);
        testResult.setSkipped(testResultSkippedCount);
        testResult.setTests(testResultTestCount);
        testResult.setTime(testResultTime);
        testResult.setExternalLinksMap(externalLinksMap);

        StagePath stagePath;
        {
            StageDetails stageDetails = getStageDetails();
            stagePath = testResultPersistService.getUpsertedStagePath(stageDetails);
            assertTrue(stagePath.isComplete());
        }
        testResult.setStageFk(stagePath.getStage().getStageId());

        List<TestSuiteModel> testSuites = new ArrayList<>();
        {//TestSuite

            TestSuiteModel testSuite = TestSuiteModel.builder().build();
            testSuite.setName(testSuiteName);
            testSuite.setError(testSuiteErrorCount);
            testSuite.setFailure(testSuiteFailureCount);
            testSuite.setPackageName(testSuitePackage);
            testSuite.setSkipped(testSuiteSkippedCount);
            testSuite.setTests(testSuiteTestCount);
            testSuite.setTime(testSuiteTime);

            List<TestCaseModel> testCases = new ArrayList<>();
            {//TestCase

                TestCaseModel testCase = TestCaseModel.builder().build();
                testCase.setClassName(testCaseClassName);
                testCase.setTestStatusFk(testCaseStatus.getStatusId());
                testCase.setName(testCaseName);
                testCase.setTime(testCaseTime);
                TestCaseFaultModel testCaseFault = TestCaseFaultModel.builder().build();
                {
                    testCaseFault.setValue(testCaseFaultValue);
                    testCaseFault.setMessage(testCaseFaultMessage);
                    testCaseFault.setType(testCaseFaultType);
                    testCaseFault.setFaultContextFk(FaultContext.FAILURE.getFaultContextId());
                    testCaseFault.setFaultContext(FaultContext.FAILURE);
                }
                testCase.addTestCaseFault(testCaseFault);
                testCases.add(testCase);
            }
            testSuite.setTestCases(testCases);
            testSuites.add(testSuite);
        }
        testResult.setTestSuites(testSuites);

        assertFalse(testResult.getTestSuites().isEmpty());
        assertFalse(testResult.getTestSuites().get(0).getTestCases().isEmpty());
        return testResult;
    }

}

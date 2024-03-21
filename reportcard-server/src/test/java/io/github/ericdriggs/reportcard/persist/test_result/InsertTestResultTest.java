package io.github.ericdriggs.reportcard.persist.test_result;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.persist.TestResultPersistService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Profile("test")
public class InsertTestResultTest extends AbstractTestResultPersistTest {

    final static String company = "company10";
    final static String org = "org10";
    final static String repo = "repo10";
    final static String branch = "branch10";
    final static String sha = "a5493474-274c-44bd-93a1-82e9df1c15d4";
    final static TreeMap<String, String> metadata = TestData.jobInfo;
    final static String runReference = "run23";
    final static String stage = "stage10";

    final static int testResultErrorCount = 10;
    final static int testResultFailureCount = 20;
    final static int testResultSkippedCount = 30;
    final static int testResultTestCount = 70;
    final static BigDecimal testResultTime = new BigDecimal("3.141");

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
        assertValues(testResultInsert);
        assertIdsandFks(testResultInsert);
        assertExternalLinks(testResultInsert);

        {
            final Set<TestResultModel> testResultsGet = testResultPersistService.getTestResults(testResultBefore.getStageFk());
            assertEquals(1, testResultsGet.size());
            final TestResultModel testResultGet = testResultsGet.iterator().next();
            assertValues(testResultGet);
            assertIdsandFks(testResultGet);
            assertExternalLinks(testResultGet);
        }

        {
            TestResultModel testResultGet = testResultPersistService.getTestResult(testResultInsert.getTestResultId());
            assertValues(testResultGet);
            assertIdsandFks(testResultGet);
            assertExternalLinks(testResultGet);
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

        Assertions.assertEquals(testResultErrorCount, testResult.getError());
        Assertions.assertEquals(testResultFailureCount, testResult.getFailure());
        Assertions.assertEquals(testResultSkippedCount, testResult.getSkipped());
        Assertions.assertEquals(testResultTestCount, testResult.getTests());
        Assertions.assertEquals(testResultTime, testResult.getTime());

        Assertions.assertEquals(testSuiteErrorCount, testSuite.getError());
        Assertions.assertEquals(testSuiteFailureCount, testSuite.getFailure());
        Assertions.assertEquals(testSuiteSkippedCount, testSuite.getSkipped());
        Assertions.assertEquals(testSuiteName, testSuite.getName());
        Assertions.assertEquals(testSuiteTestCount, testSuite.getTests());
        Assertions.assertEquals(testSuiteTime, testSuite.getTime());
        Assertions.assertEquals(testSuitePackage, testSuite.getPackageName());

        Assertions.assertEquals(testCaseClassName, testCase.getClassName());
        Assertions.assertEquals(testCaseName, testCase.getName());
        assertEquals(testCaseStatus, testCase.getTestStatus());
        Assertions.assertEquals(testCaseStatus.getStatusId(), testCase.getTestStatusFk());
        Assertions.assertEquals(testCaseTime, testCase.getTime());
    }

    private void assertIdsandFks(TestResultModel testResult) {

        final TestSuiteModel testSuite = testResult.getTestSuites().get(0);
        final TestCaseModel testCase = testSuite.getTestCases().get(0);

        assertNotNull(testResult.getTestResultId());
        assertNotNull(testSuite.getTestSuiteId());
        assertNotNull(testCase.getTestCaseId());

        Assertions.assertEquals(testResult.getTestResultId(), testSuite.getTestResultFk());
        Assertions.assertEquals(testSuite.getTestSuiteId(), testCase.getTestSuiteFk());
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

        StagePath stagePath;
        {
            StageDetails stageDetails = getStageDetails();
            stagePath = testResultPersistService.getUpsertedStagePath(stageDetails);
            assertTrue(stagePath.isComplete());
        }

        TestResultModel testResult = new TestResultModel();
        testResult.setStageFk(stagePath.getStage().getStageId());
        testResult.setError(testResultErrorCount);
        testResult.setFailure(testResultFailureCount);
        testResult.setSkipped(testResultSkippedCount);
        testResult.setTests(testResultTestCount);
        testResult.setTime(testResultTime);
        testResult.setExternalLinksMap(externalLinksMap);

        List<TestSuiteModel> testSuites = new ArrayList<>();
        {//TestSuite

            TestSuiteModel testSuite = new TestSuiteModel();
            testSuite.setName(testSuiteName);
            testSuite.setError(testSuiteErrorCount);
            testSuite.setFailure(testSuiteFailureCount);
            testSuite.setPackageName(testSuitePackage);
            testSuite.setSkipped(testSuiteSkippedCount);
            testSuite.setTests(testSuiteTestCount);
            testSuite.setTime(testSuiteTime);

            List<TestCaseModel> testCases = new ArrayList<>();
            {//TestCase

                TestCaseModel testCase = new TestCaseModel();
                testCase.setClassName(testCaseClassName);
                testCase.setTestStatusFk(testCaseStatus.getStatusId());
                testCase.setName(testCaseName);
                testCase.setTime(testCaseTime);
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

package io.github.ericdriggs.reportcard.persist.test_result;

import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.model.*;
import io.github.ericdriggs.reportcard.persist.TestResultPersistService;
import io.github.ericdriggs.reportcard.xml.ResultCount;
import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@Profile("test")
public class InsertStagePathTest extends AbstractTestResultPersistTest {

    private final static Random random = new Random();
    @Autowired
    public InsertStagePathTest(TestResultPersistService testResultPersistService) {
        super(testResultPersistService);
    }

    @Test
    public void insertStagePathAllInserted() {
        StageDetails request =
                StageDetails.builder()
                        .company("newCompany")
                        .org("newOrg")
                        .repo("newRepo")
                        .branch("newBranch")
                        .sha("newSha")
                        .jobInfo(TestData.jobInfo)
                        .runReference("64bb0231-9a2e-4492-bbd1-e0aeba24c982")
                        .stage("newStage")
                        .build();

        TestResultModel testResultModel = getTestResultModel();
        StagePathTestResult stagePathTestResult = testResultPersistService.insertTestResult(request, testResultModel);
        StagePath stagePath = stagePathTestResult.getStagePath();

        assertNotNull(stagePath);
        assertNotNull(stagePath.getCompany());
        assertNotNull(stagePath.getOrg());
        assertNotNull(stagePath.getRepo());
        assertNotNull(stagePath.getBranch());
        assertNotNull(stagePath.getJob());
        assertNotNull(stagePath.getRun());
        assertNotNull(stagePath.getStage());

        Assertions.assertEquals(request.getCompany(), stagePath.getCompany().getCompanyName());
        Assertions.assertEquals(request.getOrg(), stagePath.getOrg().getOrgName());
        Assertions.assertEquals(request.getRepo(), stagePath.getRepo().getRepoName());
        Assertions.assertEquals(request.getBranch(), stagePath.getBranch().getBranchName());
        Assertions.assertEquals(request.getSha(), stagePath.getRun().getSha());
        JsonAssert.assertJsonEquals(request.getJobInfo(), stagePath.getJob().getJobInfo());
        Assertions.assertEquals(request.getRunReference(), stagePath.getRun().getRunReference());
        Assertions.assertEquals(request.getStage(), stagePath.getStage().getStageName());
        assertNotNull(stagePath.getStage().getStageId());

        //use coarse assertion of within a few seconds since side effect updates last run
        assertTrue(Duration.between(
                                   stagePath.getRun().getRunDate(),
                                   (stagePath.getJob().getLastRun()))
                           .compareTo(Duration.ofSeconds(5)) < 0);
        Assertions.assertEquals(stagePath.getRun().getRunDate().truncatedTo(ChronoUnit.MINUTES), stagePath.getJob().getLastRun().truncatedTo(ChronoUnit.MINUTES));


        //duplicate request should be idempotent
        StagePath stagePath2 = testResultPersistService.getUpsertedStagePath(request);
        assertEquals(stagePath, stagePath2);
    }

    @Test
    void stagePathEqualsTest() {
        StagePath s1 = new StagePath();
        StagePath s2 = new StagePath();
        assertEquals(s1, s2);
    }

    public static TestResultModel getTestResultModel() {
        TestResultModel testResult = new TestResultModel();
        {
            TestSuiteModel testSuite = getTestSuite(List.of(getTestCase(TestStatus.FAILURE), getTestCase(TestStatus.FAILURE)));
            testResult.getTestSuites().add(testSuite);
            ResultCount testSuite_resultCount = testSuite.getResultCount();
            assertEquals(2, testSuite_resultCount.getFailures());
        }
        {
            List<TestCaseModel> testCases = new ArrayList<>();
            TestSuiteModel testSuite = getTestSuite(List.of(getTestCase(TestStatus.ERROR)));
            testResult.getTestSuites().add(testSuite);

            ResultCount testSuite_resultCount = testSuite.getResultCount();
            assertEquals(1, testSuite_resultCount.getErrors());
        }
        testResult.updateTotalsFromTestSuites();
        return testResult;
    }


    static TestSuiteModel getTestSuite(List<TestCaseModel> testCases) {
        TestSuiteModel testSuite = new TestSuiteModel().setTestCases(testCases);
        ResultCount resultCount = testSuite.getResultCount();
        testSuite.setName("suite_" + random.nextInt());
        testSuite.setTests(testCases.size());
        testSuite.setSkipped(resultCount.getSkipped());
        testSuite.setError(resultCount.getErrors());
        testSuite.setFailure(resultCount.getFailures());
        testSuite.setTime(resultCount.getTime());
        return testSuite;
    }

    static TestCaseModel getTestCase(TestStatus testStatus) {

        int randomInt = random.nextInt();
        TestCaseModel testCase = new TestCaseModel();

        testCase.setTestStatusFk(testStatus.getStatusId());
        testCase.setName("name-"+randomInt);
        if (testStatus.isErrorOrFailure()) {
            FaultContext faultContext = null;
            if (testStatus == TestStatus.ERROR) {
                faultContext  = FaultContext.ERROR;
            } else if (testStatus == TestStatus.FAILURE) {
                faultContext = FaultContext.FAILURE;
            } else {
                throw new IllegalStateException("not yet supported: " + testStatus);
            }
            TestCaseFaultModel testCaseFault = new TestCaseFaultModel();
            testCaseFault
                    .setFaultContextFk(faultContext.getFaultContextId())
                    .setMessage("message-" + randomInt)
                    .setType("type-"+randomInt)
                    .setValue("value-"+randomInt);

            testCase.addTestCaseFault(testCaseFault);
        }
        return testCase;

    }
}

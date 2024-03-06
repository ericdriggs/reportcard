package io.github.ericdriggs.reportcard.persist.test_result;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.StageDetails;
import io.github.ericdriggs.reportcard.persist.TestResultPersistService;
import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import static org.junit.jupiter.api.Assertions.*;

@Profile("test")
public class GetStagePathTest extends AbstractTestResultPersistTest {

    @Autowired
    public GetStagePathTest(TestResultPersistService testResultPersistService) {
        super(testResultPersistService);
    }

    @Test
    public void getStagePathAllFound() throws JsonProcessingException {
        StageDetails request =
                StageDetails.builder()
                        .company(TestData.company)
                        .org(TestData.org)
                        .repo(TestData.repo)
                        .branch(TestData.branch)
                        .sha(TestData.sha)
                        .jobInfo(TestData.jobInfo)
                        .runReference(TestData.runReference)
                        .stage(TestData.stage)
                        .build();

        StagePath stagePath = testResultPersistService.getStagePath(request);
        assertTrue(stagePath.isComplete(), stagePath.validate().toString());

        Assertions.assertEquals(stagePath.getCompany().getCompanyName(), request.getCompany());
        Assertions.assertEquals(stagePath.getOrg().getOrgName(), request.getOrg());
        Assertions.assertEquals(stagePath.getRepo().getRepoName(), request.getRepo());
        Assertions.assertEquals(stagePath.getBranch().getBranchName(), request.getBranch());
        Assertions.assertEquals(stagePath.getRun().getSha(), request.getSha());

        JsonAssert.assertJsonEquals(request.getJobInfo(), stagePath.getJob().getJobInfo());

        Assertions.assertEquals(stagePath.getRun().getRunReference(), request.getRunReference());
        Assertions.assertEquals(stagePath.getStage().getStageName(), request.getStage());
        assertNotNull(stagePath.getStage().getStageId());
    }

    @Test
    public void getStagePath_Missing_build() {
        StageDetails request =
                StageDetails.builder()
                        .company(TestData.company)
                        .org(TestData.org)
                        .repo(TestData.repo)
                        .branch(TestData.branch)
                        .sha(TestData.sha)
                        .jobInfo(TestData.jobInfo)
                        .runReference("not_found")
                        .stage(TestData.stage)
                        .build();

        StagePath stagePath = testResultPersistService.getStagePath(request);
        assertNotNull(stagePath);
        assertNotNull(stagePath.getCompany());
        assertNotNull(stagePath.getOrg());
        assertNotNull(stagePath.getRepo());
        assertNotNull(stagePath.getBranch());
        assertNotNull(stagePath.getJob());
        assertNull(stagePath.getRun());
        assertNull(stagePath.getStage());

        Assertions.assertEquals(stagePath.getOrg().getOrgName(), request.getOrg());
        Assertions.assertEquals(stagePath.getRepo().getRepoName(), request.getRepo());
        Assertions.assertEquals(stagePath.getBranch().getBranchName(), request.getBranch());
        JsonAssert.assertJsonEquals(stagePath.getJob().getJobInfo(), request.getJobInfo());

        //downstream of not found
        assertNull(stagePath.getRun());
        Assertions.assertNull( stagePath.getStage());
    }

}

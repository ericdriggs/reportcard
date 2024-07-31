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

import java.util.UUID;

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

        Assertions.assertEquals(request.getCompany(), stagePath.getCompany().getCompanyName());
        Assertions.assertEquals(request.getOrg(), stagePath.getOrg().getOrgName());
        Assertions.assertEquals(request.getRepo(), stagePath.getRepo().getRepoName());
        Assertions.assertEquals(request.getBranch(), stagePath.getBranch().getBranchName());
        Assertions.assertEquals(request.getSha(), stagePath.getRun().getSha());

        JsonAssert.assertJsonEquals(request.getJobInfo(), stagePath.getJob().getJobInfo());

        Assertions.assertEquals(request.getRunReference().toString(), stagePath.getRun().getRunReference());
        Assertions.assertEquals(request.getStage(), stagePath.getStage().getStageName());
        assertNotNull(stagePath.getStage().getStageId());
    }

    @Test
    public void getStagePath_Missing_build() {
        final UUID missingUUID = UUID.fromString("76751676-edba-48fc-b29b-ac47ec413a1f");
        StageDetails request =
                StageDetails.builder()
                        .company(TestData.company)
                        .org(TestData.org)
                        .repo(TestData.repo)
                        .branch(TestData.branch)
                        .sha(TestData.sha)
                        .jobInfo(TestData.jobInfo)
                        .runReference(missingUUID)
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

        Assertions.assertEquals(request.getOrg(), stagePath.getOrg().getOrgName());
        Assertions.assertEquals(request.getRepo(), stagePath.getRepo().getRepoName());
        Assertions.assertEquals(request.getBranch(), stagePath.getBranch().getBranchName());
        JsonAssert.assertJsonEquals(request.getJobInfo(), stagePath.getJob().getJobInfo());

        //downstream of not found
        assertNull(stagePath.getRun());
        Assertions.assertNull(stagePath.getStage());
    }

}

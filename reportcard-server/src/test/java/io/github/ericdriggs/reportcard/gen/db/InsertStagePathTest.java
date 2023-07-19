package io.github.ericdriggs.reportcard.gen.db;

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
public class InsertStagePathTest extends AbstractTestResultPersistTest {

    @Autowired
    public InsertStagePathTest(TestResultPersistService testResultPersistService) {
        super(testResultPersistService);
    }

    @Test
    public void insertStagePathAllInserted() {
        StageDetails request =
                new StageDetails()
                        .setOrg("newOrg")
                        .setRepo("newRepo")
                        .setBranch("newBranch")
                        .setSha("newSha")
                        .setJobInfo(TestData.jobInfo)
                        .setRunReference("64bb0231-9a2e-4492-bbd1-e0aeba24c982")
                        .setStage("newStage");

        StagePath stagePath = testResultPersistService.getUpsertedStagePath(request);

        assertNotNull(stagePath);
        assertNotNull(stagePath.getOrg());
        assertNotNull(stagePath.getRepo());
        assertNotNull(stagePath.getBranch());
        assertNotNull(stagePath.getJob());
        assertNotNull(stagePath.getRun());
        assertNotNull(stagePath.getStage());

        Assertions.assertEquals(request.getOrg(), stagePath.getOrg().getOrgName());
        Assertions.assertEquals(request.getRepo(), stagePath.getRepo().getRepoName());
        Assertions.assertEquals(request.getBranch(), stagePath.getBranch().getBranchName());
        Assertions.assertEquals(request.getSha(), stagePath.getRun().getSha());
        JsonAssert.assertJsonEquals(request.getJobInfo(), stagePath.getJob().getJobInfo());
        Assertions.assertEquals(request.getRunReference(), stagePath.getRun().getRunReference());
        Assertions.assertEquals(request.getStage(), stagePath.getStage().getStageName());
        assertNotNull(stagePath.getStage().getStageId());

        Assertions.assertEquals(stagePath.getRun().getCreated(), stagePath.getBranch().getLastRun());
        Assertions.assertEquals(stagePath.getRun().getCreated(), stagePath.getJob().getLastRun());
    }

}

package io.github.ericdriggs.reportcard.gen.db;

import io.github.ericdriggs.reportcard.UploadService;
import io.github.ericdriggs.reportcard.model.RunStagePath;
import io.github.ericdriggs.reportcard.model.ReportMetaData;
import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import static org.junit.jupiter.api.Assertions.*;

@Profile("test")
public class InsertBuildStagePathTest extends AbstractUploadDbTest {

    @Autowired
    public InsertBuildStagePathTest(UploadService uploadService) {
        super(uploadService);
    }

    @Test
    public void insertBuildStagePathAllInserted() {
        ReportMetaData request =
                new ReportMetaData()
                        .setOrg("newOrg")
                        .setRepo("newRepo")
                        .setBranch("newBranch")
                        .setSha("newSha")
                        .setJobInfo(TestData.metadata)
                        .setRunReference("64bb0231-9a2e-4492-bbd1-e0aeba24c982")
                        .setStage("newStage");

        RunStagePath bsp = uploadService.getOrInsertRunStagePath(request);

        assertNotNull(bsp);
        assertNotNull(bsp.getOrg());
        assertNotNull(bsp.getRepo());
        assertNotNull(bsp.getBranch());
        assertNotNull(bsp.getJob());
        assertNotNull(bsp.getRun());
        assertNotNull(bsp.getStage());

        Assertions.assertEquals(request.getOrg(), bsp.getOrg().getOrgName());
        Assertions.assertEquals(request.getRepo(), bsp.getRepo().getRepoName());
        Assertions.assertEquals(request.getBranch(), bsp.getBranch().getBranchName());
        Assertions.assertEquals(request.getSha(), bsp.getRun().getSha());
        JsonAssert.assertJsonEquals(request.getJobInfo(), bsp.getJob().getJobInfo());
        Assertions.assertEquals(request.getRunReference(), bsp.getRun().getRunReference());
        Assertions.assertEquals(request.getStage(), bsp.getStage().getStageName());
        assertNotNull(bsp.getStage().getStageId());
    }

}

package io.github.ericdriggs.reportcard.gen.db;

import io.github.ericdriggs.reportcard.ReportCardService;
import io.github.ericdriggs.reportcard.model.ExecutionStagePath;
import io.github.ericdriggs.reportcard.model.ReportMetaData;
import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import static org.junit.jupiter.api.Assertions.*;

@Profile("test")
//@EnableConfigurationProperties
public class InsertBuildStagePathTest extends AbstractDbTest {

    @Autowired
    public InsertBuildStagePathTest(ReportCardService reportCardService) {
        super(reportCardService);
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
                        .setExecutionReference("64bb0231-9a2e-4492-bbd1-e0aeba24c982")
                        .setStage("newStage");

        ExecutionStagePath bsp = reportCardService.getOrInsertExecutionStagePath(request);

        assertNotNull(bsp);
        assertNotNull(bsp.getOrg());
        assertNotNull(bsp.getRepo());
        assertNotNull(bsp.getBranch());
        assertNotNull(bsp.getJob());
        assertNotNull(bsp.getExecution());
        assertNotNull(bsp.getStage());

        Assertions.assertEquals(request.getOrg(), bsp.getOrg().getOrgName());
        Assertions.assertEquals(request.getRepo(), bsp.getRepo().getRepoName());
        Assertions.assertEquals(request.getBranch(), bsp.getBranch().getBranchName());
        Assertions.assertEquals(request.getSha(), bsp.getExecution().getSha());
        JsonAssert.assertJsonEquals(request.getJobInfo(), bsp.getJob().getJobInfo());
        Assertions.assertEquals(request.getExecutionReference(), bsp.getExecution().getExecutionReference());
        Assertions.assertEquals(request.getStage(), bsp.getStage().getStageName());
        assertNotNull(bsp.getStage().getStageId());
    }

}

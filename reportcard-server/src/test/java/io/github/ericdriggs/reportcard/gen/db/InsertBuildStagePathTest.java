package io.github.ericdriggs.reportcard.gen.db;

import io.github.ericdriggs.reportcard.ReportCardService;
import io.github.ericdriggs.reportcard.model.ExecutionStagePath;
import io.github.ericdriggs.reportcard.model.HostApplicationPipeline;
import io.github.ericdriggs.reportcard.model.ReportMetaData;
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
                        .setHostApplicationPipeline(new HostApplicationPipeline("newHost", "newApplication", "newPipeline"))
                        .setExternalExecutionId("64bb0231-9a2e-4492-bbd1-e0aeba24c982")
                        .setStage("newStage");

        ExecutionStagePath bsp = reportCardService.getOrInsertExecutionStagePath(request);

        assertNotNull(bsp);
        assertNotNull(bsp.getOrg());
        assertNotNull(bsp.getRepo());
        assertNotNull(bsp.getBranch());
        assertNotNull(bsp.getSha());
        assertNotNull(bsp.getContext());
        assertNotNull(bsp.getExecution());
        assertNotNull(bsp.getStage());

        Assertions.assertEquals(request.getOrg(), bsp.getOrg().getOrgName());
        Assertions.assertEquals(request.getRepo(), bsp.getRepo().getRepoName());
        Assertions.assertEquals(request.getBranch(), bsp.getBranch().getBranchName());
        Assertions.assertEquals(request.getSha(), bsp.getSha().getSha());
        Assertions.assertEquals(request.getHostApplicationPipeline().getHost(), bsp.getContext().getHost());
        Assertions.assertEquals(request.getHostApplicationPipeline().getApplication(), bsp.getContext().getApplication());
        Assertions.assertEquals(request.getHostApplicationPipeline().getPipeline(), bsp.getContext().getPipeline());
        Assertions.assertEquals(request.getExternalExecutionId(), bsp.getExecution().getExecutionExternalId());
        Assertions.assertEquals(request.getStage(), bsp.getStage().getStageName());
        assertNotNull(bsp.getStage().getStageId());
    }

}

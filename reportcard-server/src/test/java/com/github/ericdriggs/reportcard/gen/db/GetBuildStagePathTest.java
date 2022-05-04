package com.github.ericdriggs.reportcard.gen.db;

import com.github.ericdriggs.reportcard.ReportCardService;
import com.github.ericdriggs.reportcard.model.ExecutionStagePath;
import com.github.ericdriggs.reportcard.model.ReportMetaData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import static org.junit.jupiter.api.Assertions.*;

@Profile("test")
//@EnableConfigurationProperties
public class GetBuildStagePathTest extends AbstractDbTest {


    @Autowired
    public GetBuildStagePathTest(ReportCardService reportCardService ) {
        super(reportCardService);
    }


    @Test
    public void getBuildStagePathAllFound() {
        ReportMetaData request =
                new ReportMetaData()
                        .setOrg(TestData.org)
                        .setRepo(TestData.repo)
                        .setBranch(TestData.branch)
                        .setSha(TestData.sha)
                        .setHostApplicationPipeline(TestData.hostApplicationPipeline)
                        .setExternalExecutionId(TestData.externalExecutionId)
                        .setStage(TestData.stage);

        ExecutionStagePath bsp = reportCardService.getExecutionStagePath(request);
        assertTrue(bsp.isComplete());

        Assertions.assertEquals(bsp.getOrg().getOrgName(), request.getOrg());
        Assertions.assertEquals(bsp.getRepo().getRepoName(), request.getRepo());
        Assertions.assertEquals(bsp.getBranch().getBranchName(), request.getBranch());
        Assertions.assertEquals(bsp.getSha().getSha(), request.getSha());

        Assertions.assertEquals(bsp.getContext().getHost(), request.getHostApplicationPipeline().getHost());
        Assertions.assertEquals(bsp.getContext().getApplication(), request.getHostApplicationPipeline().getApplication());
        Assertions.assertEquals(bsp.getContext().getPipeline(), request.getHostApplicationPipeline().getPipeline());

        Assertions.assertEquals(bsp.getExecution().getExecutionExternalId(), request.getExternalExecutionId());
        Assertions.assertEquals(bsp.getStage().getStageName(), request.getStage());
        assertNotNull(bsp.getStage().getStageId());
    }

    @Test
    public void getBuildStagePath_Missing_build() {
        ReportMetaData request =
        new ReportMetaData()
                .setOrg(TestData.org)
                .setRepo(TestData.repo)
                .setBranch(TestData.branch)
                .setSha(TestData.sha)
                .setHostApplicationPipeline(TestData.hostApplicationPipeline)
                .setExternalExecutionId("not_found");

        ExecutionStagePath bsp = reportCardService.getExecutionStagePath(request);
        assertNotNull(bsp);
        assertNotNull(bsp.getOrg());
        assertNotNull(bsp.getRepo());
        assertNotNull(bsp.getBranch());
        assertNotNull(bsp.getSha());
        assertNotNull(bsp.getContext());
        assertNull(bsp.getExecution());
        assertNull(bsp.getStage());

        Assertions.assertEquals(bsp.getOrg().getOrgName(), request.getOrg());
        Assertions.assertEquals(bsp.getRepo().getRepoName(), request.getRepo());
        Assertions.assertEquals(bsp.getBranch().getBranchName(), request.getBranch());
        Assertions.assertEquals(bsp.getSha().getSha(), request.getSha());
        Assertions.assertEquals(bsp.getContext().getHost(), request.getHostApplicationPipeline().getHost());
        Assertions.assertEquals(bsp.getContext().getApplication(), request.getHostApplicationPipeline().getApplication());
        Assertions.assertEquals(bsp.getContext().getPipeline(), request.getHostApplicationPipeline().getPipeline());

    }

}

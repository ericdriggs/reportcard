package io.github.ericdriggs.reportcard.gen.db;

import com.fasterxml.jackson.core.JsonProcessingException;
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
public class GetBuildStagePathTest extends AbstractDbTest {

    @Autowired
    public GetBuildStagePathTest(ReportCardService reportCardService) {
        super(reportCardService);
    }

    @Test
    public void getBuildStagePathAllFound() throws JsonProcessingException {
        ReportMetaData request =
                new ReportMetaData()
                        .setOrg(TestData.org)
                        .setRepo(TestData.repo)
                        .setBranch(TestData.branch)
                        .setSha(TestData.sha)
                        .setJobInfo(TestData.metadata)
                        .setExecutionReference(TestData.executionReference)
                        .setStage(TestData.stage);

        ExecutionStagePath bsp = reportCardService.getExecutionStagePath(request);
        assertTrue(bsp.isComplete(), bsp.validate().toString());

        Assertions.assertEquals(bsp.getOrg().getOrgName(), request.getOrg());
        Assertions.assertEquals(bsp.getRepo().getRepoName(), request.getRepo());
        Assertions.assertEquals(bsp.getBranch().getBranchName(), request.getBranch());
        Assertions.assertEquals(bsp.getExecution().getSha(), request.getSha());

        JsonAssert.assertJsonEquals(request.getJobInfo(), bsp.getJob().getJobInfo());

        Assertions.assertEquals(bsp.getExecution().getExecutionReference(), request.getExecutionReference());
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
                        .setJobInfo(TestData.metadata)
                        .setExecutionReference("not_found")
                        .setStage(TestData.stage);

        ExecutionStagePath bsp = reportCardService.getExecutionStagePath(request);
        assertNotNull(bsp);
        assertNotNull(bsp.getOrg());
        assertNotNull(bsp.getRepo());
        assertNotNull(bsp.getBranch());
        assertNotNull(bsp.getJob());
        assertNull(bsp.getExecution());
        assertNull(bsp.getStage());

        Assertions.assertEquals(bsp.getOrg().getOrgName(), request.getOrg());
        Assertions.assertEquals(bsp.getRepo().getRepoName(), request.getRepo());
        Assertions.assertEquals(bsp.getBranch().getBranchName(), request.getBranch());
        JsonAssert.assertJsonEquals(bsp.getJob().getJobInfo(), request.getJobInfo());

        //downstream of not found
        assertNull(bsp.getExecution());
        Assertions.assertNull( bsp.getStage());
    }

}

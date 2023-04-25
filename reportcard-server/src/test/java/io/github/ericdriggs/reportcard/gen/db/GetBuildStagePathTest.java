package io.github.ericdriggs.reportcard.gen.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ericdriggs.reportcard.ReportCardService;
import io.github.ericdriggs.reportcard.model.ExecutionStagePath;
import io.github.ericdriggs.reportcard.model.ReportMetaData;
import io.github.ericdriggs.reportcard.util.JsonCompare;
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
                        .setMetadata(TestData.metadata)
                        .setExternalExecutionId(TestData.externalExecutionId)
                        .setStage(TestData.stage);

        ExecutionStagePath bsp = reportCardService.getExecutionStagePath(request);
        assertTrue(bsp.isComplete());

        Assertions.assertEquals(bsp.getOrg().getOrgName(), request.getOrg());
        Assertions.assertEquals(bsp.getRepo().getRepoName(), request.getRepo());
        Assertions.assertEquals(bsp.getBranch().getBranchName(), request.getBranch());
        Assertions.assertEquals(bsp.getSha().getSha(), request.getSha());

        JsonCompare.assertJsonEquals(request.getMetadata(), bsp.getContext().getMetadata());

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
                        .setMetadata(TestData.metadata)
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
        JsonCompare.assertJsonEquals(bsp.getContext().getMetadata(), request.getMetadataJson());
    }

}

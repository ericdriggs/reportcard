package io.github.ericdriggs.reportcard.gen.db;

import com.fasterxml.jackson.core.JsonProcessingException;
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
public class GetBuildStagePathTest extends AbstractUploadDbTest {

    @Autowired
    public GetBuildStagePathTest(UploadService uploadService) {
        super(uploadService);
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
                        .setRunReference(TestData.runReference)
                        .setStage(TestData.stage);

        RunStagePath bsp = uploadService.getRunStagePath(request);
        assertTrue(bsp.isComplete(), bsp.validate().toString());

        Assertions.assertEquals(bsp.getOrg().getOrgName(), request.getOrg());
        Assertions.assertEquals(bsp.getRepo().getRepoName(), request.getRepo());
        Assertions.assertEquals(bsp.getBranch().getBranchName(), request.getBranch());
        Assertions.assertEquals(bsp.getRun().getSha(), request.getSha());

        JsonAssert.assertJsonEquals(request.getJobInfo(), bsp.getJob().getJobInfo());

        Assertions.assertEquals(bsp.getRun().getRunReference(), request.getRunReference());
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
                        .setRunReference("not_found")
                        .setStage(TestData.stage);

        RunStagePath bsp = uploadService.getRunStagePath(request);
        assertNotNull(bsp);
        assertNotNull(bsp.getOrg());
        assertNotNull(bsp.getRepo());
        assertNotNull(bsp.getBranch());
        assertNotNull(bsp.getJob());
        assertNull(bsp.getRun());
        assertNull(bsp.getStage());

        Assertions.assertEquals(bsp.getOrg().getOrgName(), request.getOrg());
        Assertions.assertEquals(bsp.getRepo().getRepoName(), request.getRepo());
        Assertions.assertEquals(bsp.getBranch().getBranchName(), request.getBranch());
        JsonAssert.assertJsonEquals(bsp.getJob().getJobInfo(), request.getJobInfo());

        //downstream of not found
        assertNull(bsp.getRun());
        Assertions.assertNull( bsp.getStage());
    }

}

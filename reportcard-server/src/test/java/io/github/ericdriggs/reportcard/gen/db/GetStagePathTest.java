package io.github.ericdriggs.reportcard.gen.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ericdriggs.reportcard.UploadService;
import io.github.ericdriggs.reportcard.model.StagePath;
import io.github.ericdriggs.reportcard.model.ReportMetaData;
import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import static org.junit.jupiter.api.Assertions.*;

@Profile("test")
public class GetStagePathTest extends AbstractUploadDbTest {

    @Autowired
    public GetStagePathTest(UploadService uploadService) {
        super(uploadService);
    }

    @Test
    public void getStagePathAllFound() throws JsonProcessingException {
        ReportMetaData request =
                new ReportMetaData()
                        .setOrg(TestData.org)
                        .setRepo(TestData.repo)
                        .setBranch(TestData.branch)
                        .setSha(TestData.sha)
                        .setJobInfo(TestData.metadata)
                        .setRunReference(TestData.runReference)
                        .setStage(TestData.stage);

        StagePath stagePath = uploadService.getStagePath(request);
        assertTrue(stagePath.isComplete(), stagePath.validate().toString());

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
        ReportMetaData request =
                new ReportMetaData()
                        .setOrg(TestData.org)
                        .setRepo(TestData.repo)
                        .setBranch(TestData.branch)
                        .setSha(TestData.sha)
                        .setJobInfo(TestData.metadata)
                        .setRunReference("not_found")
                        .setStage(TestData.stage);

        StagePath stagePath = uploadService.getStagePath(request);
        assertNotNull(stagePath);
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

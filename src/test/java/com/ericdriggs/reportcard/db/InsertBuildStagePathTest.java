package com.ericdriggs.reportcard.db;

import com.ericdriggs.reportcard.ReportCardService;
import com.ericdriggs.reportcard.model.BuildStagePath;
import com.ericdriggs.reportcard.model.BuildStagePathRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import static org.junit.jupiter.api.Assertions.*;

@Profile("test")
//@EnableConfigurationProperties
public class InsertBuildStagePathTest extends AbstractDbTest {

    @Autowired
    public InsertBuildStagePathTest(ReportCardService reportCardService ) {
        super(reportCardService);
    }


    @Test
    public void insertBuildStagePathAllInserted() {
        BuildStagePathRequest request =
                new BuildStagePathRequest()
                        .setOrgName("newOrg")
                .setRepoName("newRepo")
                .setAppName("newApp")
                .setBranchName("newBranch")
                .setBuildOrdinal(1)
                .setStageName("newStage");

        BuildStagePath bsp = reportCardService.getOrInsertBuildStagePath(request);

        assertNotNull(bsp);
        assertNotNull(bsp.getOrg());
        assertNotNull(bsp.getRepo());
        assertNotNull(bsp.getApp());
        assertNotNull(bsp.getBranch());
        assertNotNull(bsp.getAppBranch());
        assertNotNull(bsp.getBuild());
        assertNotNull(bsp.getStage());
        assertNotNull(bsp.getBuildStage());

        assertEquals(bsp.getOrg().getOrgName(), request.getOrgName());
        assertEquals(bsp.getRepo().getRepoName(), request.getRepoName());
        assertEquals(bsp.getApp().getAppName(), request.getAppName());
        assertEquals(bsp.getBranch().getBranchName(), request.getBranchName());
        assertNotNull(bsp.getAppBranch().getAppBranchId());
        assertEquals(bsp.getBuild().getAppBranchBuildOrdinal(), request.getBuildOrdinal());
        assertEquals(bsp.getStage().getStageName(), request.getStageName());
        assertNotNull(bsp.getBuildStage().getBuildStageId());
    }

}

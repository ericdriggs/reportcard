package com.ericdriggs.reportcard.db;

import com.ericdriggs.reportcard.ReportCardService;
import com.ericdriggs.reportcard.model.BuildStagePath;
import com.ericdriggs.reportcard.model.BuildStagePathRequest;
import com.ericdriggs.reportcard.model.ReportMetatData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import static org.junit.jupiter.api.Assertions.*;

@Profile("test")
//@EnableConfigurationProperties
public class InsertBuildStagePathTest extends AbstractDbTest {

    final String buildUniqueString = "64bb0231-9a2e-4492-bbd1-e0aeba24c982";

    @Autowired
    public InsertBuildStagePathTest(ReportCardService reportCardService) {
        super(reportCardService);
    }


    @Test
    public void insertBuildStagePathAllInserted() {
        ReportMetatData request =
                new ReportMetatData()
                        .setOrg("newOrg")
                        .setRepo("newRepo")
                        .setApp("newApp")
                        .setBranch("newBranch")
                        .setBuildIdentifier(buildUniqueString)
                        .setStage("newStage");

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

        assertEquals(request.getOrg(), bsp.getOrg().getOrgName());
        assertEquals(request.getRepo(), bsp.getRepo().getRepoName());
        assertEquals(request.getApp(), bsp.getApp().getAppName());
        assertEquals(request.getBranch(), bsp.getBranch().getBranchName());
        assertNotNull(bsp.getAppBranch().getAppBranchId());
        assertEquals(request.getBuildIdentifier(), bsp.getBuild().getBuildUniqueString());
        assertEquals(request.getStage(), bsp.getStage().getStageName());
        assertNotNull(bsp.getBuildStage().getBuildStageId());
    }

}

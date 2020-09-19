package com.ericdriggs.reportcard.db;

import com.ericdriggs.reportcard.ReportCardService;
import com.ericdriggs.reportcard.db.tables.pojos.Org;
import com.ericdriggs.reportcard.db.tables.pojos.Repo;
import com.ericdriggs.reportcard.db.tables.records.RepoRecord;
import com.ericdriggs.reportcard.model.BuildStagePath;
import com.ericdriggs.reportcard.model.BuildStagePathRequest;
import com.ericdriggs.reportcard.model.ReportMetatData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;

import static org.junit.jupiter.api.Assertions.*;

@Profile("test")
//@EnableConfigurationProperties
public class GetBuildStagePathTest extends AbstractDbTest {

    final static String buildUniqueString = "9282be75-6ca5-424b-a7ec-13d13370ba90";

    @Autowired
    public GetBuildStagePathTest(ReportCardService reportCardService ) {
        super(reportCardService);
    }


    @Test
    public void getBuildStagePathAllFound() {
        ReportMetatData request =
                new ReportMetatData()
                        .setOrg("default")
                .setRepo("default")
                .setApp("app1")
                .setBranch("master")
                .setBuildIdentifier(buildUniqueString)
                .setStage("unit");

        BuildStagePath bsp = reportCardService.getBuildStagePath(request);
        assertTrue(bsp.isComplete());

        assertEquals(bsp.getOrg().getOrgName(), request.getOrg());
        assertEquals(bsp.getRepo().getRepoName(), request.getRepo());
        assertEquals(bsp.getApp().getAppName(), request.getApp());
        assertEquals(bsp.getBranch().getBranchName(), request.getBranch());
        assertNotNull(bsp.getAppBranch().getAppBranchId());
        assertEquals(bsp.getBuild().getBuildUniqueString(), request.getBuildIdentifier());
        assertEquals(bsp.getStage().getStageName(), request.getStage());
        assertNotNull(bsp.getBuildStage().getBuildStageId());
    }

    @Test
    public void getBuildStagePath_Missing_build() {

        ReportMetatData request =
                new ReportMetatData()
                        .setOrg("default")
                        .setRepo("default")
                        .setApp("app1")
                        .setBranch("master")
                        .setBuildIdentifier("not_found")
                        .setStage("unit");

        BuildStagePath bsp = reportCardService.getBuildStagePath(request);
        assertNotNull(bsp);
        assertNotNull(bsp.getOrg());
        assertNotNull(bsp.getRepo());
        assertNotNull(bsp.getApp());
        assertNotNull(bsp.getBranch());
        assertNotNull(bsp.getAppBranch());
        assertNull(bsp.getBuild());
        assertNotNull(bsp.getStage());
        assertNull(bsp.getBuildStage());

        assertEquals(bsp.getOrg().getOrgName(), request.getOrg());
        assertEquals(bsp.getRepo().getRepoName(), request.getRepo());
        assertEquals(bsp.getApp().getAppName(), request.getApp());
        assertEquals(bsp.getBranch().getBranchName(), request.getBranch());
        assertNotNull(bsp.getAppBranch().getAppBranchId());
        //assertEquals(bsp.getBuild().getAppBranchBuildOrdinal(), request.getBuildOrdinal());
        assertEquals(bsp.getStage().getStageName(), request.getStage());
        //assertNotNull(bsp.getBuildStage().getBuildStageId());
    }

    @Test
    public void getBuildStagePath_Missing_app_build_stage() {

        ReportMetatData request =
                new ReportMetatData()
                        .setOrg("default")
                        .setRepo("default")
                        .setApp("not_found")
                        .setBranch("master")
                        .setBuildIdentifier("not_found")
                        .setStage("not_found");

        BuildStagePath bsp = reportCardService.getBuildStagePath(request);
        assertNotNull(bsp);
        assertNotNull(bsp.getOrg());
        assertNotNull(bsp.getRepo());
        assertNull(bsp.getApp());
        assertNotNull(bsp.getBranch());
        assertNull(bsp.getAppBranch());
        assertNull(bsp.getBuild());
        assertNull(bsp.getStage());
        assertNull(bsp.getBuildStage());

        assertEquals(bsp.getOrg().getOrgName(), request.getOrg());
        assertEquals(bsp.getRepo().getRepoName(), request.getRepo());
        //assertEquals(bsp.getApp().getAppName(), request.getAppName());
        assertEquals(bsp.getBranch().getBranchName(), request.getBranch());
        //assertNotNull(bsp.getAppBranch().getAppBranchId());
        //assertEquals(bsp.getBuild().getAppBranchBuildOrdinal(), request.getBuildOrdinal());
        //assertEquals(bsp.getStage().getStageName(), request.getStageName());
        //assertNotNull(bsp.getBuildStage().getBuildStageId());

    }

    @Test
    public void getBuildStagePath_Missing_branch_build_stage() {

        ReportMetatData request =
                new ReportMetatData()
                        .setOrg("default")
                        .setRepo("default")
                        .setApp("app1")
                        .setBranch("not_found")
                        .setBuildIdentifier("not_found")
                        .setStage("not_found");

        BuildStagePath bsp = reportCardService.getBuildStagePath(request);
        assertNotNull(bsp);
        assertNotNull(bsp.getOrg());
        assertNotNull(bsp.getRepo());
        assertNotNull(bsp.getApp());
        assertNull(bsp.getBranch());
        assertNull(bsp.getAppBranch());
        assertNull(bsp.getBuild());
        assertNull(bsp.getStage());
        assertNull(bsp.getBuildStage());

        assertEquals(bsp.getOrg().getOrgName(), request.getOrg());
        assertEquals(bsp.getRepo().getRepoName(), request.getRepo());
        assertEquals(bsp.getApp().getAppName(), request.getApp());
        //assertEquals(bsp.getBranch().getBranchName(), request.getBranchName());
        //assertNotNull(bsp.getAppBranch().getAppBranchId());
        //assertEquals(bsp.getBuild().getAppBranchBuildOrdinal(), request.getBuildOrdinal());
        //assertEquals(bsp.getStage().getStageName(), request.getStageName());
        //assertNotNull(bsp.getBuildStage().getBuildStageId());
    }
}

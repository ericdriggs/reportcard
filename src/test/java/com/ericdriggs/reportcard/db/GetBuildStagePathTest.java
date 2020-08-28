package com.ericdriggs.reportcard.db;

import com.ericdriggs.reportcard.ReportCardService;
import com.ericdriggs.reportcard.db.tables.pojos.Org;
import com.ericdriggs.reportcard.db.tables.pojos.Repo;
import com.ericdriggs.reportcard.db.tables.records.RepoRecord;
import com.ericdriggs.reportcard.model.BuildStagePath;
import com.ericdriggs.reportcard.model.BuildStagePathRequest;
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
        BuildStagePathRequest request =
                new BuildStagePathRequest()
                        .setOrgName("default")
                .setRepoName("default")
                .setAppName("app1")
                .setBranchName("master")
                .setBuildOrdinal(1)
                .setStageName("unit");

        BuildStagePath bsp = reportCardService.getBuildStagePath(request);
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

    @Test
    public void getBuildStagePath_Missing_build() {

        BuildStagePathRequest request =
                new BuildStagePathRequest()
                        .setOrgName("default")
                        .setRepoName("default")
                        .setAppName("app1")
                        .setBranchName("master")
                        .setBuildOrdinal(-1)
                        .setStageName("unit");

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

        assertEquals(bsp.getOrg().getOrgName(), request.getOrgName());
        assertEquals(bsp.getRepo().getRepoName(), request.getRepoName());
        assertEquals(bsp.getApp().getAppName(), request.getAppName());
        assertEquals(bsp.getBranch().getBranchName(), request.getBranchName());
        assertNotNull(bsp.getAppBranch().getAppBranchId());
        //assertEquals(bsp.getBuild().getAppBranchBuildOrdinal(), request.getBuildOrdinal());
        assertEquals(bsp.getStage().getStageName(), request.getStageName());
        //assertNotNull(bsp.getBuildStage().getBuildStageId());
    }

    @Test
    public void getBuildStagePath_Missing_app_build_stage() {

        BuildStagePathRequest request =
                new BuildStagePathRequest()
                        .setOrgName("default")
                        .setRepoName("default")
                        .setAppName("not_found")
                        .setBranchName("master")
                        .setBuildOrdinal(-1)
                        .setStageName("not_found");

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

        assertEquals(bsp.getOrg().getOrgName(), request.getOrgName());
        assertEquals(bsp.getRepo().getRepoName(), request.getRepoName());
        //assertEquals(bsp.getApp().getAppName(), request.getAppName());
        assertEquals(bsp.getBranch().getBranchName(), request.getBranchName());
        //assertNotNull(bsp.getAppBranch().getAppBranchId());
        //assertEquals(bsp.getBuild().getAppBranchBuildOrdinal(), request.getBuildOrdinal());
        //assertEquals(bsp.getStage().getStageName(), request.getStageName());
        //assertNotNull(bsp.getBuildStage().getBuildStageId());

    }

    @Test
    public void getBuildStagePath_Missing_branch_build_stage() {

        BuildStagePathRequest request =
                new BuildStagePathRequest()
                        .setOrgName("default")
                        .setRepoName("default")
                        .setAppName("app1")
                        .setBranchName("not_found")
                        .setBuildOrdinal(-1)
                        .setStageName("not_found");

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

        assertEquals(bsp.getOrg().getOrgName(), request.getOrgName());
        assertEquals(bsp.getRepo().getRepoName(), request.getRepoName());
        assertEquals(bsp.getApp().getAppName(), request.getAppName());
        //assertEquals(bsp.getBranch().getBranchName(), request.getBranchName());
        //assertNotNull(bsp.getAppBranch().getAppBranchId());
        //assertEquals(bsp.getBuild().getAppBranchBuildOrdinal(), request.getBuildOrdinal());
        //assertEquals(bsp.getStage().getStageName(), request.getStageName());
        //assertNotNull(bsp.getBuildStage().getBuildStageId());
    }
}

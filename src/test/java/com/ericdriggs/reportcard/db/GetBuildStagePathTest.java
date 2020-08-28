package com.ericdriggs.reportcard.db;

import com.ericdriggs.reportcard.ReportCardService;
import com.ericdriggs.reportcard.db.tables.pojos.Org;
import com.ericdriggs.reportcard.db.tables.pojos.Repo;
import com.ericdriggs.reportcard.db.tables.records.RepoRecord;
import com.ericdriggs.reportcard.model.BuildStagePath;
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
        final String org = "default";
        final String repo = "default";
        final String app = "app1";
        final String branch = "master";
        final Integer buildOrdinal = 1;
        final String stage = "unit";

        BuildStagePath bsp = reportCardService.getBuildStagePath(org, repo, app, branch, buildOrdinal, stage);
        assertNotNull(bsp);
        assertNotNull(bsp.getOrg());
        assertNotNull(bsp.getRepo());
        assertNotNull(bsp.getApp());
        assertNotNull(bsp.getBranch());
        assertNotNull(bsp.getBuild());
        assertNotNull(bsp.getStage());

        assertEquals(bsp.getOrg().getOrgName(), org);
        assertEquals(bsp.getRepo().getRepoName(), repo);
        assertEquals(bsp.getApp().getAppName(), app);
        assertEquals(bsp.getBranch().getBranchName(), branch);
        assertEquals(bsp.getBuild().getAppBranchBuildOrdinal(), buildOrdinal);
        assertEquals(bsp.getStage().getStageName(), stage);
    }

    @Test
    public void getBuildStagePath_Missing_build() {
        final String org = "default";
        final String repo = "default";
        final String app = "app1";
        final String branch = "master";
        final Integer buildOrdinal = -1;
        final String stage = "unit";

        BuildStagePath bsp = reportCardService.getBuildStagePath(org, repo, app, branch, buildOrdinal, stage);
        assertNotNull(bsp);

        assertNotNull(bsp.getOrg());
        assertNotNull(bsp.getRepo());
        assertNotNull(bsp.getApp());
        assertNotNull(bsp.getBranch());
        assertNull(bsp.getBuild());
        assertNotNull(bsp.getStage());

        assertEquals(bsp.getOrg().getOrgName(), org);
        assertEquals(bsp.getRepo().getRepoName(), repo);
        assertEquals(bsp.getApp().getAppName(), app);
        assertEquals(bsp.getBranch().getBranchName(), branch);
        //build is null
        assertEquals(bsp.getStage().getStageName(), stage);
    }

    @Test
    public void getBuildStagePath_Missing_app_build_stage() {
        final String org = "default";
        final String repo = "default";
        final String app = "not_found";
        final String branch = "master";
        final Integer buildOrdinal = -1;
        final String stage = "not_found";

        BuildStagePath bsp = reportCardService.getBuildStagePath(org, repo, app, branch, buildOrdinal, stage);

        assertNotNull(bsp);

        assertNotNull(bsp.getOrg());
        assertNotNull(bsp.getRepo());
        assertNull(bsp.getApp());
        assertNotNull(bsp.getBranch());
        assertNull(bsp.getBuild());
        assertNull(bsp.getStage());

        assertEquals(bsp.getOrg().getOrgName(), org);
        assertEquals(bsp.getRepo().getRepoName(), repo);
        //app is null
        assertEquals(bsp.getBranch().getBranchName(), branch);
        //build is null
        //stage is null

    }

    @Test
    public void getBuildStagePath_Missing_branch_build_stage() {
        final String org = "default";
        final String repo = "default";
        final String app = "app1";
        final String branch = "not_found";
        final Integer buildOrdinal = -1;
        final String stage = "not_found";

        BuildStagePath bsp = reportCardService.getBuildStagePath(org, repo, app, branch, buildOrdinal, stage);

        assertNotNull(bsp);
        assertNotNull(bsp.getOrg());
        assertNotNull(bsp.getRepo());
        assertNotNull(bsp.getApp());
        assertNull(bsp.getBranch());
        assertNull(bsp.getBuild());
        assertNull(bsp.getStage());


        assertEquals(bsp.getOrg().getOrgName(), org);
        assertEquals(bsp.getRepo().getRepoName(), repo);
        assertEquals(bsp.getApp().getAppName(), app);
        //branch is null
        //build is null
        //stage is null
    }
}

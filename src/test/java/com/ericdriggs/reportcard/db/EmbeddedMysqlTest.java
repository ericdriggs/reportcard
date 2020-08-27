package com.ericdriggs.reportcard.db;

import com.ericdriggs.reportcard.ReportCardService;
import com.ericdriggs.reportcard.db.tables.pojos.Org;
import com.ericdriggs.reportcard.db.tables.pojos.Repo;
import com.ericdriggs.reportcard.db.tables.records.RepoRecord;
import com.ericdriggs.reportcard.model.BuildStagePath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Profile("test")
//@EnableConfigurationProperties
public class EmbeddedMysqlTest extends AbstractDbTest {

    @Autowired
    public EmbeddedMysqlTest(ReportCardService reportCardService ) {
        super(reportCardService);
    }

    @Test
    public void getRepoFromRepoRecord() {
        Repo repo = reportCardService.getRepoFromRepoRecord("default", "default");
        assertNotNull(repo);
    }

    @Test
    public void getBuildStagePath() {
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
        assertEquals(bsp.getBranch().getBranchName(), branch);
        assertEquals(bsp.getApp().getAppName(), app);
        assertEquals(bsp.getBuild().getAppBranchBuildOrdinal(), buildOrdinal);
        assertEquals(bsp.getStage().getStageName(), stage);
    }
}

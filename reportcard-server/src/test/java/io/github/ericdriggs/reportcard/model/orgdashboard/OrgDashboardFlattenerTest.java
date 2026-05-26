package io.github.ericdriggs.reportcard.model.orgdashboard;

import io.github.ericdriggs.reportcard.controller.browse.response.FlatDashboardEntry;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.CompanyPojo;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.OrgPojo;
import io.github.ericdriggs.reportcard.model.graph.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class OrgDashboardFlattenerTest {

    @Test
    void flatten_multipleRuns_selectsLatestByRunId() {
        StageGraph stage = new StageGraph(1L, "unit", 1L, List.of(), List.of());
        RunGraph olderRun = new RunGraph(1L, "ref1", 1L, 1, "sha1", Instant.parse("2024-01-01T00:00:00Z"), true, List.of(stage));
        RunGraph newerRun = new RunGraph(5L, "ref5", 1L, 2, "sha5", Instant.parse("2024-01-05T00:00:00Z"), false, List.of(stage));
        RunGraph middleRun = new RunGraph(3L, "ref3", 1L, 3, "sha3", Instant.parse("2024-01-03T00:00:00Z"), true, List.of(stage));

        TreeMap<String, String> jobInfo = new TreeMap<>();
        jobInfo.put("app", "myapp");

        JobGraph job = new JobGraph(10L, jobInfo, 1, "{\"app\":\"myapp\"}", Instant.now(), List.of(olderRun, middleRun, newerRun));
        BranchGraph branch = new BranchGraph(1, "main", 1, Instant.now(), List.of(job));
        RepoGraph repo = new RepoGraph(1, "my-repo", 1, List.of(branch));

        OrgDashboard dashboard = OrgDashboard.builder()
                .companyPojo(CompanyPojo.builder().companyId(1).companyName("acme").build())
                .orgPojo(OrgPojo.builder().orgId(1).orgName("eng").companyFk(1).build())
                .repoGraphs(List.of(repo))
                .build();

        List<FlatDashboardEntry> result = OrgDashboardFlattener.flatten(List.of(dashboard));

        assertEquals(1, result.size());
        FlatDashboardEntry entry = result.get(0);
        assertEquals(5L, entry.getRunId());
        assertEquals(2, entry.getJobRunCount());
        assertEquals("sha5", entry.getSha());
        assertFalse(entry.getIsSuccess());
    }

    @Test
    void flatten_jobWithNoRuns_excluded() {
        TreeMap<String, String> jobInfo = new TreeMap<>();
        jobInfo.put("app", "myapp");

        JobGraph jobWithRuns = new JobGraph(10L, jobInfo, 1, "{\"app\":\"myapp\"}", Instant.now(),
                List.of(new RunGraph(1L, "ref1", 10L, 1, "sha1", Instant.now(), true,
                        List.of(new StageGraph(1L, "unit", 1L, List.of(), List.of())))));
        JobGraph jobNoRuns = new JobGraph(20L, jobInfo, 1, "{\"app\":\"myapp\"}", Instant.now(), List.of());
        JobGraph jobNullRuns = new JobGraph(30L, jobInfo, 1, "{\"app\":\"myapp\"}", Instant.now(), null);

        BranchGraph branch = new BranchGraph(1, "main", 1, Instant.now(), List.of(jobWithRuns, jobNoRuns, jobNullRuns));
        RepoGraph repo = new RepoGraph(1, "my-repo", 1, List.of(branch));

        OrgDashboard dashboard = OrgDashboard.builder()
                .companyPojo(CompanyPojo.builder().companyId(1).companyName("acme").build())
                .orgPojo(OrgPojo.builder().orgId(1).orgName("eng").companyFk(1).build())
                .repoGraphs(List.of(repo))
                .build();

        List<FlatDashboardEntry> result = OrgDashboardFlattener.flatten(List.of(dashboard));

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getJobId());
    }

    @Test
    void flatten_multipleReposAndBranches_flattensAll() {
        TreeMap<String, String> jobInfo = new TreeMap<>();
        jobInfo.put("app", "a");

        StageGraph stageA = new StageGraph(1L, "unit", 1L, List.of(), List.of());
        RunGraph run1 = new RunGraph(1L, "ref1", 1L, 1, "sha1", Instant.now(), true, List.of(stageA));
        RunGraph run2 = new RunGraph(2L, "ref2", 2L, 1, "sha2", Instant.now(), false, List.of(stageA));

        JobGraph job1 = new JobGraph(1L, jobInfo, 1, "{}", Instant.now(), List.of(run1));
        JobGraph job2 = new JobGraph(2L, jobInfo, 2, "{}", Instant.now(), List.of(run2));

        BranchGraph branch1 = new BranchGraph(1, "main", 1, Instant.now(), List.of(job1));
        BranchGraph branch2 = new BranchGraph(2, "develop", 2, Instant.now(), List.of(job2));

        RepoGraph repo1 = new RepoGraph(1, "repo-a", 1, List.of(branch1));
        RepoGraph repo2 = new RepoGraph(2, "repo-b", 1, List.of(branch2));

        OrgDashboard dashboard = OrgDashboard.builder()
                .companyPojo(CompanyPojo.builder().companyId(1).companyName("acme").build())
                .orgPojo(OrgPojo.builder().orgId(1).orgName("eng").companyFk(1).build())
                .repoGraphs(List.of(repo1, repo2))
                .build();

        List<FlatDashboardEntry> result = OrgDashboardFlattener.flatten(List.of(dashboard));

        assertEquals(2, result.size());
        assertEquals("repo-a", result.get(0).getRepo());
        assertEquals("main", result.get(0).getBranch());
        assertEquals("repo-b", result.get(1).getRepo());
        assertEquals("develop", result.get(1).getBranch());
    }

    @Test
    void flatten_nullCompanyPojo_skipped() {
        RepoGraph repo = new RepoGraph(1, "my-repo", 1, List.of());

        OrgDashboard dashboard = OrgDashboard.builder()
                .companyPojo(null)
                .orgPojo(OrgPojo.builder().orgId(1).orgName("eng").companyFk(1).build())
                .repoGraphs(List.of(repo))
                .build();

        List<FlatDashboardEntry> result = OrgDashboardFlattener.flatten(List.of(dashboard));
        assertTrue(result.isEmpty());
    }

    @Test
    void flatten_nullOrgPojo_skipped() {
        RepoGraph repo = new RepoGraph(1, "my-repo", 1, List.of());

        OrgDashboard dashboard = OrgDashboard.builder()
                .companyPojo(CompanyPojo.builder().companyId(1).companyName("acme").build())
                .orgPojo(null)
                .repoGraphs(List.of(repo))
                .build();

        List<FlatDashboardEntry> result = OrgDashboardFlattener.flatten(List.of(dashboard));
        assertTrue(result.isEmpty());
    }

    @Test
    void flatten_nullDashboardList_returnsEmpty() {
        List<FlatDashboardEntry> result = OrgDashboardFlattener.flatten(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void flatten_emptyDashboardList_returnsEmpty() {
        List<FlatDashboardEntry> result = OrgDashboardFlattener.flatten(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

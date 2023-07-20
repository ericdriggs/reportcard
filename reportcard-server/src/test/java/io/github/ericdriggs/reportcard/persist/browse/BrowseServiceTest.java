package io.github.ericdriggs.reportcard.persist.browse;

import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.persist.BrowseService;;
import io.github.ericdriggs.reportcard.util.JsonCompare;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BrowseServiceTest extends AbstractBrowseServiceTest {
    @Autowired
    public BrowseServiceTest(BrowseService browseService) {
        super(browseService);
    }

    @Test
    void getOrgsSuccessTest() {
        Set<Org> orgs = browseService.getOrgs();
        assertNotNull(orgs);
        assertTrue(orgs.size() > 0);
        Set<String> orgNames = orgs.stream().map(Org::getOrgName).collect(Collectors.toSet());
        assertTrue(orgNames.contains(TestData.org));
    }

    @Test
    void getOrgSuccessTest() {
        Org org = browseService.getOrg(TestData.org);
        validateTestOrg(org);
    }

    @Test
    void getOrgNotFoundTest() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            browseService.getOrg("MISSING_ORG");
        });
        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains("Unable to find org"));
        assertTrue(ex.getMessage().contains("MISSING_ORG"));
        assertEquals(404, ex.getStatus().value());

    }

    @Test
    void getOrgReposSuccessTest() {
        final Map<Org, Set<Repo>> orgRepos = browseService.getOrgsRepos();
        final Org org = getTestOrg(orgRepos.keySet());
        validateTestOrg(org);
        final Repo repo = getTestRepo(orgRepos.get(org));
        validateTestRepo(repo);
    }

    @Test
    void getOrgReposBranchesTest() {
        Map<Org, Map<Repo, Set<Branch>>> orgReposBranches = browseService.getOrgReposBranches(TestData.org);
        final Org org = getTestOrg(orgReposBranches.keySet());
        validateTestOrg(org);
        final Map<Repo, Set<Branch>> repoBranches = orgReposBranches.get(org);
        final Repo repo = getTestRepo(repoBranches.keySet());
        validateTestRepo(repo);
        final Branch branch = getTestBranch(repoBranches.get(repo));
        validateTestBranch(branch);
    }


    @Test
    void getRepoSuccessTest() {
        Repo repo = browseService.getRepo(TestData.org, TestData.repo);
        validateTestRepo(repo);
    }

    @Test
    void getRepoNotFoundTest() {
        {
            ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
                browseService.getRepo("MISSING_ORG", "MISSING_REPO");
            });
            assertNotNull(ex.getMessage());
            assertTrue(ex.getMessage().contains("MISSING_ORG"));
            assertTrue(ex.getMessage().contains("MISSING_REPO"));
            assertEquals(404, ex.getStatus().value());
        }
        {
            ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
                browseService.getRepo(TestData.org, "MISSING_REPO");
            });
            assertNotNull(ex.getMessage());
            assertTrue(ex.getMessage().contains(TestData.org));
            assertTrue(ex.getMessage().contains("MISSING_REPO"));
            assertEquals(404, ex.getStatus().value());
        }
    }


    @Test
    void getRepoBranchesJobsTest() {
        Map<Repo, Map<Branch, Set<Job>>> repoBranchesJobs = browseService.getRepoBranchesJobs(TestData.org, TestData.repo);
        final Repo repo = getTestRepo(repoBranchesJobs.keySet());
        final Map<Branch, Set<Job>> branchJobs = repoBranchesJobs.get(repo);
        final Branch branch = getTestBranch(branchJobs.keySet());
        final Job job = getTestJob(nestedSet(branchJobs.values()));
        validateTestRepo(repo);
        validateTestBranch(branch);
        validateTestJob(job);
    }

    @Test
    void getBranchTest() {
        Branch branch = browseService.getBranch(TestData.org, TestData.repo, TestData.branch);
        validateTestBranch(branch);
    }

    @Test
    void getBranchJobsRunsTest() {
        Map<Branch, Map<Job, Set<Run>>> repoBranchesJobs = browseService.getBranchJobsRuns(TestData.org, TestData.repo, TestData.branch, null);
        final Branch branch = getTestBranch(repoBranchesJobs.keySet());
        final Map<Job, Set<Run>> jobRuns = repoBranchesJobs.get(branch);
        final Job job = getTestJob(jobRuns.keySet());
        final Run run = getTestRun(nestedSet(jobRuns.values()));
        validateTestBranch(branch);
        validateTestJob(job);
        validateTestRun(run);
    }


    @Test
    void getJobTest() {
        Job job = browseService.getJob(TestData.org, TestData.repo, TestData.branch, TestData.jobInfo);
        validateTestJob(job);
    }



    private Org getTestOrg(Collection<Org> orgs) {
        assertEquals(1, orgs.size());
        Org org = null;
        for (Org o : orgs) {
            if (o.getOrgName().equals(TestData.org)) {
                org = o;
                break;
            }
        }
        assertNotNull(org);
        return org;
    }

    private Repo getTestRepo(Collection<Repo> repos) {
        assertEquals(1, repos.size());
        Repo repo = null;
        for (Repo r : repos) {
            if (r.getRepoName().equals(TestData.repo)) {
                repo = r;
                break;
            }
        }
        assertNotNull(repo);
        return repo;
    }

    private Branch getTestBranch(Collection<Branch> branches) {
        assertEquals(1, branches.size());
        Branch branch = null;
        for (Branch b : branches) {
            if (b.getBranchName().equals(TestData.branch)) {
                branch = b;
                break;
            }
        }
        assertNotNull(branch);
        return branch;
    }

    private static <T> List<T> nestedSet(Collection<Set<T>> t) {
        return t.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    private Job getTestJob(Collection<Job> jobs) {
        assertEquals(1, jobs.size());
        Job job = null;
        for (Job j : jobs) {
            if (JsonCompare.equalsMap(j.getJobInfo(), TestData.jobInfo)) {
                job = j;
                break;
            }
        }
        assertNotNull(job);
        return job;
    }

    private Run getTestRun(Collection<Run> runs) {
        assertEquals(1, runs.size());
        Run run  = null;
        for (Run r : runs) {
            if (r.getRunReference().equals(TestData.runReference)) {
                run = r;
                break;
            }
        }
        assertNotNull(run);
        return run;
    }

    private void validateTestOrg(Org org) {
        assertNotNull(org);
        assertEquals(TestData.org, org.getOrgName());
        assertEquals(1, org.getOrgId());
    }

    private void validateTestRepo(Repo repo) {
        assertEquals(TestData.repo, repo.getRepoName());
        assertEquals(1, repo.getRepoId());
        assertEquals(1, repo.getOrgFk());
    }

    private void validateTestBranch(Branch branch) {
        assertEquals(TestData.branch, branch.getBranchName());
        assertEquals(1, branch.getBranchId());
        assertEquals(1, branch.getRepoFk());
        assertNotNull(branch.getLastRun());
    }

    private void validateTestJob(Job job) {
        assertTrue(JsonCompare.equalsMap(job.getJobInfo(), TestData.jobInfo));
        assertEquals(1, job.getBranchFk());
        assertEquals(1, job.getJobId());
        assertNotNull(job.getLastRun());
    }

    private void validateTestRun(Run run) {
        assertEquals(TestData.runReference, run.getRunReference());
        assertEquals(1, run.getRunId());
        assertEquals(1, run.getJobFk());
        assertEquals(1, run.getJobRunCount());
        assertEquals(TestData.sha, run.getSha());
        assertNotNull(run.getCreated());
        assertNotNull(run.getCreated());
    }
}

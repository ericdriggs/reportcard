package io.github.ericdriggs.reportcard.persist.browse;

import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Branch;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Org;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.Repo;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BrowseServiceTest extends AbstractBrowseServiceTest {
    @Autowired
    public BrowseServiceTest(BrowseService browseService) {
        super(browseService);
    }

    @Test
    void getOrgsTest() {
        Set<Org> orgs = browseService.getOrgs();
        assertNotNull(orgs);
        assertTrue(orgs.size() > 0);
        Set<String> orgNames = orgs.stream().map(Org::getOrgName).collect(Collectors.toSet());
        assertTrue(orgNames.contains(TestData.org));
    }

    @Test
    void getOrgTest() {
        Org org = browseService.getOrg(TestData.org);
        validateTestOrg(org);
    }

    @Test
    void getOrgReposTest() {
        final Map<Org, Set<Repo>> orgRepos = browseService.getOrgsRepos();
        final Org org = getTestOrg(orgRepos.keySet());
        validateTestOrg(org);
        final Repo repo = getTestRepo(orgRepos.get(org));
        validateTestRepo(repo);
    }

    @Test
    void getRepoTest() {
        Repo repo = browseService.getRepo(TestData.org, TestData.repo);
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

    private Org getTestOrg(Set<Org> orgs) {
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

    private Repo getTestRepo(Set<Repo> repos) {
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

    private Branch getTestBranch(Set<Branch> branches) {
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
        assertNotNull( branch.getLastRun());
    }
}

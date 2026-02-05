package io.github.ericdriggs.reportcard.controller.browse;

import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.browse.AbstractBrowseServiceTest;
import io.github.ericdriggs.reportcard.util.JsonCompare;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for BrowseJsonController hierarchy endpoints.
 * Tests validate JSON serialization and HTTP responses for company, org, repo, and branch level endpoints.
 */
public class BrowseJsonControllerTest extends AbstractBrowseServiceTest {

    private final BrowseJsonController controller;

    @Autowired
    public BrowseJsonControllerTest(BrowseService browseService, BrowseJsonController browseJsonController) {
        super(browseService);
        this.controller = browseJsonController;
    }

    @Test
    void getCompanyOrgsJsonSuccessTest() {
        // Call controller endpoint
        ResponseEntity<Map<CompanyPojo, Set<OrgPojo>>> response = controller.getCompanyOrgs();

        // Verify HTTP response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify response body
        Map<CompanyPojo, Set<OrgPojo>> companyOrgs = response.getBody();
        assertNotNull(companyOrgs);
        assertFalse(companyOrgs.isEmpty());

        // Verify test data appears in response
        boolean companyWasFound = false;
        for (Map.Entry<CompanyPojo, Set<OrgPojo>> entry : companyOrgs.entrySet()) {
            final CompanyPojo company = entry.getKey();
            final Set<OrgPojo> orgs = entry.getValue();
            assertNotNull(orgs);
            assertFalse(orgs.isEmpty());

            if (company.getCompanyName().equalsIgnoreCase(TestData.company)) {
                companyWasFound = true;
                // Verify expected org exists
                boolean orgFound = false;
                for (OrgPojo org : orgs) {
                    if (org.getOrgName().equals(TestData.org)) {
                        orgFound = true;
                        break;
                    }
                }
                assertTrue(orgFound, "Expected org '" + TestData.org + "' not found in company '" + TestData.company + "'");
            }
        }
        assertTrue(companyWasFound, "Expected company '" + TestData.company + "' not found in response");
    }

    @Test
    void getCompanyOrgsReposJsonSuccessTest() {
        // Call controller endpoint
        ResponseEntity<Map<CompanyPojo, Map<OrgPojo, Set<RepoPojo>>>> response =
            controller.getCompanyOrgsRepos(TestData.company);

        // Verify HTTP response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify response body
        Map<CompanyPojo, Map<OrgPojo, Set<RepoPojo>>> companyOrgsRepos = response.getBody();
        assertNotNull(companyOrgsRepos);
        assertFalse(companyOrgsRepos.isEmpty());

        // Verify test data appears in response
        boolean testDataFound = false;
        for (Map.Entry<CompanyPojo, Map<OrgPojo, Set<RepoPojo>>> companyEntry : companyOrgsRepos.entrySet()) {
            final CompanyPojo company = companyEntry.getKey();
            final Map<OrgPojo, Set<RepoPojo>> orgRepos = companyEntry.getValue();
            assertNotNull(orgRepos);
            assertFalse(orgRepos.isEmpty());

            if (company.getCompanyName().equalsIgnoreCase(TestData.company)) {
                for (Map.Entry<OrgPojo, Set<RepoPojo>> orgEntry : orgRepos.entrySet()) {
                    final OrgPojo org = orgEntry.getKey();
                    final Set<RepoPojo> repos = orgEntry.getValue();
                    assertNotNull(repos);
                    assertFalse(repos.isEmpty());

                    if (org.getOrgName().equals(TestData.org)) {
                        // Verify expected repo exists
                        for (RepoPojo repo : repos) {
                            if (repo.getRepoName().equals(TestData.repo)) {
                                testDataFound = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        assertTrue(testDataFound, "Expected test data (company: " + TestData.company +
            ", org: " + TestData.org + ", repo: " + TestData.repo + ") not found in response");
    }

    @Test
    void getOrgReposBranchesJsonSuccessTest() {
        // Call controller endpoint
        ResponseEntity<Map<OrgPojo, Map<RepoPojo, Set<BranchPojo>>>> response =
            controller.getOrgReposBranches(TestData.company, TestData.org);

        // Verify HTTP response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify response body
        Map<OrgPojo, Map<RepoPojo, Set<BranchPojo>>> orgReposBranches = response.getBody();
        assertNotNull(orgReposBranches);
        assertFalse(orgReposBranches.isEmpty());

        // Verify test data appears in response
        boolean testDataFound = false;
        for (Map.Entry<OrgPojo, Map<RepoPojo, Set<BranchPojo>>> orgEntry : orgReposBranches.entrySet()) {
            final OrgPojo org = orgEntry.getKey();
            final Map<RepoPojo, Set<BranchPojo>> repoBranches = orgEntry.getValue();
            assertNotNull(repoBranches);
            assertFalse(repoBranches.isEmpty());

            if (org.getOrgName().equals(TestData.org)) {
                for (Map.Entry<RepoPojo, Set<BranchPojo>> repoEntry : repoBranches.entrySet()) {
                    final RepoPojo repo = repoEntry.getKey();
                    final Set<BranchPojo> branches = repoEntry.getValue();
                    assertNotNull(branches);
                    assertFalse(branches.isEmpty());

                    if (repo.getRepoName().equals(TestData.repo)) {
                        // Verify expected branch exists
                        for (BranchPojo branch : branches) {
                            if (branch.getBranchName().equals(TestData.branch)) {
                                testDataFound = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        assertTrue(testDataFound, "Expected test data (org: " + TestData.org +
            ", repo: " + TestData.repo + ", branch: " + TestData.branch + ") not found in response");
    }

    @Test
    void getRepoBranchesJobsJsonSuccessTest() {
        // Call controller endpoint
        ResponseEntity<Map<RepoPojo, Map<BranchPojo, Set<JobPojo>>>> response =
            controller.getRepoBranchesJobs(TestData.company, TestData.org, TestData.repo);

        // Verify HTTP response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify response body
        Map<RepoPojo, Map<BranchPojo, Set<JobPojo>>> repoBranchesJobs = response.getBody();
        assertNotNull(repoBranchesJobs);
        assertFalse(repoBranchesJobs.isEmpty());

        // Verify test data appears in response
        boolean testDataFound = false;
        for (Map.Entry<RepoPojo, Map<BranchPojo, Set<JobPojo>>> repoEntry : repoBranchesJobs.entrySet()) {
            final RepoPojo repo = repoEntry.getKey();
            final Map<BranchPojo, Set<JobPojo>> branchJobs = repoEntry.getValue();
            assertNotNull(branchJobs);
            assertFalse(branchJobs.isEmpty());

            if (repo.getRepoName().equals(TestData.repo)) {
                for (Map.Entry<BranchPojo, Set<JobPojo>> branchEntry : branchJobs.entrySet()) {
                    final BranchPojo branch = branchEntry.getKey();
                    final Set<JobPojo> jobs = branchEntry.getValue();
                    assertNotNull(jobs);
                    assertFalse(jobs.isEmpty());

                    if (branch.getBranchName().equals(TestData.branch)) {
                        // Verify expected job exists (matching jobInfo)
                        for (JobPojo job : jobs) {
                            if (JsonCompare.equalsMap(job.getJobInfo(), TestData.jobInfo)) {
                                testDataFound = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        assertTrue(testDataFound, "Expected test data (repo: " + TestData.repo +
            ", branch: " + TestData.branch + ", jobInfo: " + TestData.jobInfo + ") not found in response");
    }
}

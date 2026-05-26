package io.github.ericdriggs.reportcard.controller.browse;

import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.model.orgdashboard.OrgDashboard;
import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.browse.AbstractBrowseServiceTest;
import io.github.ericdriggs.reportcard.util.StringMapUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BrowseJsonControllerRepoDashboardJobInfoTest extends AbstractBrowseServiceTest {

    private final BrowseJsonController controller;
    private final String jobInfoString = StringMapUtil.toEqualsCsv(TestData.jobInfo);

    @Autowired
    public BrowseJsonControllerRepoDashboardJobInfoTest(BrowseService browseService, BrowseJsonController controller) {
        super(browseService);
        this.controller = controller;
    }

    @Test
    void getRepoDashboardWithJobInfoJsonSuccessTest() {
        ResponseEntity<List<OrgDashboard>> response =
            controller.getRepoDashboardWithJobInfoJson(
                TestData.repo, jobInfoString,
                Collections.emptyList(), true, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertNotNull(response.getBody().get(0).getRepoGraphs());
        assertFalse(response.getBody().get(0).getRepoGraphs().isEmpty());
    }

    @Test
    void getRepoDashboardWithJobInfoMatchesUnfilteredTest() {
        ResponseEntity<List<OrgDashboard>> jobInfoResponse =
            controller.getRepoDashboardWithJobInfoJson(
                TestData.repo, jobInfoString,
                Collections.emptyList(), true, null);

        ResponseEntity<List<OrgDashboard>> unfilteredResponse =
            controller.getRepoDashboardJson(
                TestData.repo, Collections.emptyList(), true, null);

        assertEquals(unfilteredResponse.getStatusCode(), jobInfoResponse.getStatusCode());
        assertNotNull(jobInfoResponse.getBody());
        assertNotNull(unfilteredResponse.getBody());
        assertEquals(unfilteredResponse.getBody().size(), jobInfoResponse.getBody().size());
        assertEquals(
            countJobs(unfilteredResponse.getBody()),
            countJobs(jobInfoResponse.getBody()));
    }

    @Test
    void getRepoDashboardWithJobInfoFilterExcludesNonMatchingTest() {
        ResponseEntity<List<OrgDashboard>> unfilteredResponse =
            controller.getRepoDashboardJson(
                TestData.repo, Collections.emptyList(), true, null);

        ResponseEntity<List<OrgDashboard>> filteredResponse =
            controller.getRepoDashboardWithJobInfoJson(
                TestData.repo, "application=NONEXISTENT",
                Collections.emptyList(), true, null);

        assertNotNull(unfilteredResponse.getBody());
        assertFalse(unfilteredResponse.getBody().isEmpty());

        int unfilteredJobCount = countJobs(unfilteredResponse.getBody());
        int filteredJobCount = countJobs(filteredResponse.getBody());
        assertTrue(unfilteredJobCount > 0, "Unfiltered should have jobs");
        assertEquals(0, filteredJobCount, "Filter with non-existent value should return no jobs");
    }

    @Test
    void getRepoDashboardWithPartialJobInfoMatchesTest() {
        // Sparse filter: single key from jobInfo should still match
        ResponseEntity<List<OrgDashboard>> partialResponse =
            controller.getRepoDashboardWithJobInfoJson(
                TestData.repo, "application=fooapp",
                Collections.emptyList(), true, null);

        ResponseEntity<List<OrgDashboard>> fullResponse =
            controller.getRepoDashboardWithJobInfoJson(
                TestData.repo, jobInfoString,
                Collections.emptyList(), true, null);

        assertNotNull(partialResponse.getBody());
        assertNotNull(fullResponse.getBody());
        assertEquals(
            countJobs(fullResponse.getBody()),
            countJobs(partialResponse.getBody()),
            "Partial jobInfo filter should match same jobs as full jobInfo");
    }

    private int countJobs(List<OrgDashboard> dashboards) {
        int count = 0;
        for (OrgDashboard dashboard : dashboards) {
            if (dashboard.getRepoGraphs() != null) {
                for (var repoGraph : dashboard.getRepoGraphs()) {
                    if (repoGraph.branches() != null) {
                        for (var branch : repoGraph.branches()) {
                            if (branch.jobs() != null) {
                                count += branch.jobs().size();
                            }
                        }
                    }
                }
            }
        }
        return count;
    }
}

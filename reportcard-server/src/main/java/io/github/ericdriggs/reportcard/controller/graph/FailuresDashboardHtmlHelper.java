package io.github.ericdriggs.reportcard.controller.graph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.ericdriggs.reportcard.controller.browse.BrowseHtmlHelper;
import io.github.ericdriggs.reportcard.model.failures.DailyTestAggregation;
import io.github.ericdriggs.reportcard.model.failures.FailuresDashboard;
import io.github.ericdriggs.reportcard.model.failures.FailuresDashboardRequest;
import io.github.ericdriggs.reportcard.model.failures.TestCaseFailureSummary;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class FailuresDashboardHtmlHelper extends BrowseHtmlHelper {

    private static final Logger log = LoggerFactory.getLogger(FailuresDashboardHtmlHelper.class);

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static String renderHtml(FailuresDashboard dashboard) {
        final String main = getMainDiv(dashboard);
        return getPage(main, getBreadCrumbs(dashboard.getRequest()), "flex-column")
                .replace("<body>", "<body onload=\"initFailuresDashboard()\">")
                .replace("<!--additionalLinks-->",
                        "<link rel=\"stylesheet\" href=\"/css/failures.css\">" + ls
                                + "<script src=\"/js/failures.js\"></script>" + ls);
    }

    public static String renderEmptyHtml(String company, String org) {
        FailuresDashboardRequest request = FailuresDashboardRequest.builder()
                .company(company)
                .org(org)
                .build();
        String main = failuresEmptyDiv
                .replace("<!--company-->", escapeHtml(company))
                .replace("<!--org-->", org != null ? escapeHtml(org) : "");
        return getPage(main, getBreadCrumbs(request), "flex-column")
                .replace("<body>", "<body onload=\"initFailuresDashboard()\">")
                .replace("<!--additionalLinks-->",
                        "<link rel=\"stylesheet\" href=\"/css/failures.css\">" + ls
                                + "<script src=\"/js/failures.js\"></script>" + ls);
    }

    static String getMainDiv(FailuresDashboard dashboard) {
        FailuresDashboardRequest request = dashboard.getRequest();
        String chartSection = renderChartSection(dashboard);

        return failuresMainDiv
                .replace("<!--company-->", escapeHtml(request.getCompany()))
                .replace("<!--org-->", request.getOrg() != null ? escapeHtml(request.getOrg()) : "")
                .replace("<!--jobInfoKey-->", request.getJobInfoKey() != null ? escapeHtml(request.getJobInfoKey()) : "")
                .replace("<!--jobInfoValues-->", request.getJobInfoValues() != null ? escapeHtml(String.join(",", request.getJobInfoValues())) : "")
                .replace("<!--days-->", String.valueOf(request.getDays()))
                .replace("<!--failureThreshold-->", String.valueOf(request.getFailureThreshold()))
                .replace("<!--repos-->", request.getRepos() != null ? escapeHtml(String.join(",", request.getRepos())) : "")
                .replace("<!--failingTestRows-->", renderFailingTestRows(dashboard.getFailingTests()))
                .replace("<!--chartSection-->", chartSection)
                .replace("<!--generated-->", dashboard.getGenerated().truncatedTo(ChronoUnit.SECONDS).toString());
    }

    static String renderChartSection(FailuresDashboard dashboard) {
        if (dashboard.getDailyAggregationsByOrg() != null && !dashboard.getDailyAggregationsByOrg().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (var entry : dashboard.getDailyAggregationsByOrg().entrySet()) {
                String orgName = entry.getKey();
                String json;
                try {
                    json = objectMapper.writeValueAsString(entry.getValue());
                } catch (JsonProcessingException e) {
                    log.error("Failed to serialize daily aggregation for org '{}': {}", orgName, e.getMessage(), e);
                    json = "[]";
                }
                sb.append("<h3>").append(escapeHtml(orgName)).append("</h3>").append(ls);
                sb.append("<div class=\"chart-container\" data-daily='").append(json).append("'></div>").append(ls);
            }
            return sb.toString();
        } else {
            String dailyJson;
            try {
                dailyJson = objectMapper.writeValueAsString(dashboard.getDailyAggregations());
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize daily aggregation data: {}", e.getMessage(), e);
                dailyJson = "[]";
            }
            return "<div id=\"chart-container\" class=\"chart-container\"></div>" + ls
                    + "<script id=\"daily-data\" type=\"application/json\">" + dailyJson + "</script>" + ls;
        }
    }

    static String renderFailingTestRows(List<TestCaseFailureSummary> failingTests) {
        if (failingTests == null || failingTests.isEmpty()) {
            return "<tr><td colspan=\"9\">No failing tests found above threshold.</td></tr>";
        }
        StringBuilder sb = new StringBuilder();
        for (TestCaseFailureSummary t : failingTests) {
            sb.append("<tr>")
                    .append("<td>").append(escapeHtml(t.getPackageName())).append("</td>")
                    .append("<td>").append(escapeHtml(t.getSuiteName())).append("</td>")
                    .append("<td>").append(escapeHtml(t.getCaseName())).append("</td>")
                    .append("<td class=\"fail-percent\">").append(t.getSuccessPercent().toPlainString()).append("%</td>")
                    .append("<td>").append(t.getFailSince() != null ? t.getFailSince().truncatedTo(ChronoUnit.SECONDS).toString() : "").append("</td>")
                    .append("<td>").append(t.getTotalRuns()).append("</td>")
                    .append("<td>").append(escapeHtml(t.getRepo())).append("</td>")
                    .append("<td>").append(escapeHtml(t.getBranch())).append("</td>")
                    .append("<td>").append(escapeHtml(t.getJobInfo())).append("</td>")
                    .append("</tr>").append(ls);
        }
        return sb.toString();
    }

    static List<Pair<String, String>> getBreadCrumbs(FailuresDashboardRequest request) {
        List<Pair<String, String>> breadCrumbs = new ArrayList<>();
        breadCrumbs.add(Pair.of("home", "/"));
        breadCrumbs.add(Pair.of(request.getCompany(), "/company/" + request.getCompany()));
        if (request.getOrg() != null) {
            breadCrumbs.add(Pair.of(request.getOrg(), "/company/" + request.getCompany() + "/org/" + request.getOrg()));
        }
        breadCrumbs.add(Pair.of("failures", "#"));
        return breadCrumbs;
    }

    static String failuresMainDiv =
            """
            <div id="failures-dashboard">
                <fieldset class="filter-fieldset">
                <legend>Failures Filters</legend>
                <form id="failures-form" method="get">
                    <div class="filter-row">
                        <label for="jobInfoKey">Key:</label>
                        <input type="text" id="jobInfoKey" style="width: 200px; padding: 5px;" placeholder="e.g. application" value="<!--jobInfoKey-->">
                        <label for="jobInfoValue">Value:</label>
                        <input type="text" id="jobInfoValue" style="width: 250px; padding: 5px;" placeholder="e.g. my_app,other_app" value="<!--jobInfoValues-->">
                    </div>
                    <div class="filter-row">
                        <label for="days-select">Days:</label>
                        <select id="days-select" style="padding: 5px;">
                            <option value="7">7</option>
                            <option value="30">30</option>
                            <option value="60">60</option>
                        </select>
                        <label for="threshold-input">Failure Threshold %:</label>
                        <input type="number" id="threshold-input" min="1" max="100" value="<!--failureThreshold-->" style="width: 60px; padding: 5px;">
                        <label for="repos-input">Repos:</label>
                        <input type="text" id="repos-input" style="width: 200px; padding: 5px;" value="<!--repos-->" placeholder="all repos (comma-sep)">
                    </div>
                    <div class="filter-row">
                        <button type="submit" style="padding: 8px 20px; background: #007bff; color: white; border: none; cursor: pointer;">Generate</button>
                        <button type="button" onclick="window.location.href=window.location.pathname" style="padding: 8px 20px; margin-left: 10px; background: #6c757d; color: white; border: none; cursor: pointer;">Clear</button>
                        <span class="generated-time">Generated: <!--generated--></span>
                    </div>
                </form>
                </fieldset>

                <h2>Persistently Failing Tests</h2>
                <table class="sortable failures-table" id="failures-table">
                    <thead>
                    <tr>
                        <th>Package</th>
                        <th>Suite</th>
                        <th>Test Case</th>
                        <th>Success %</th>
                        <th>Fail Since</th>
                        <th>Runs</th>
                        <th>Repo</th>
                        <th>Branch</th>
                        <th>Job Info</th>
                    </tr>
                    </thead>
                    <tbody>
                        <!--failingTestRows-->
                    </tbody>
                </table>

                <h2>Daily Test Results</h2>
                <!--chartSection-->
            </div>
            """;

    static String failuresEmptyDiv =
            """
            <div id="failures-dashboard">
                <fieldset class="filter-fieldset">
                <legend>Failures Filters</legend>
                <form id="failures-form" method="get">
                    <div class="filter-row">
                        <label for="jobInfoKey">Key:</label>
                        <input type="text" id="jobInfoKey" style="width: 200px; padding: 5px;" placeholder="e.g. application" value="">
                        <label for="jobInfoValue">Value:</label>
                        <input type="text" id="jobInfoValue" style="width: 250px; padding: 5px;" placeholder="e.g. my_app,other_app" value="">
                    </div>
                    <div class="filter-row">
                        <label for="days-select">Days:</label>
                        <select id="days-select" style="padding: 5px;">
                            <option value="7">7</option>
                            <option value="30">30</option>
                            <option value="60">60</option>
                        </select>
                        <label for="threshold-input">Failure Threshold %:</label>
                        <input type="number" id="threshold-input" min="1" max="100" value="50" style="width: 60px; padding: 5px;">
                        <label for="repos-input">Repos:</label>
                        <input type="text" id="repos-input" style="width: 200px; padding: 5px;" value="" placeholder="all repos (comma-sep)">
                    </div>
                    <div class="filter-row">
                        <button type="submit" style="padding: 8px 20px; background: #007bff; color: white; border: none; cursor: pointer;">Generate</button>
                        <button type="button" onclick="window.location.href=window.location.pathname" style="padding: 8px 20px; margin-left: 10px; background: #6c757d; color: white; border: none; cursor: pointer;">Clear</button>
                    </div>
                </form>
                </fieldset>

                <p class="filter-hint"><em>Enter filters above and click Generate to view the failures report.</em></p>
            </div>
            """;
}

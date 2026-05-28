package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgDTO;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchDTO;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobInfoDTO;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoDTO;
import io.github.ericdriggs.reportcard.controller.browse.BrowseHtmlHelper;
import io.github.ericdriggs.reportcard.model.metrics.company.MetricsIntervalResultCountMaps;
import io.github.ericdriggs.reportcard.model.metrics.company.RunResultCount;
import io.github.ericdriggs.reportcard.model.trend.InstantRange;
import io.github.ericdriggs.reportcard.util.NumberStringUtil;
import io.github.ericdriggs.reportcard.util.StringMapUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static io.github.ericdriggs.reportcard.util.NumberStringUtil.*;

public class MetricsHtmlHelper {
    final static String ls = System.lineSeparator();
    private static final int DELTA_SIGNIFICANCE_THRESHOLD_PERCENT = 10;

    public static String renderMetricsIntervalResultCountMaps(MetricsIntervalResultCountMaps metricsIntervalResultCountMaps) {

        final String main = renderMetricsMain(metricsIntervalResultCountMaps);
        return BrowseHtmlHelper.getPage(main, Collections.emptyList(), "metrics")
                .replace("<!--additionalLinks-->", "<link rel=\"stylesheet\" href=\"/css/metrics.css\">" + ls);

    }

    private static String renderMetricsMain(MetricsIntervalResultCountMaps metricsIntervalResultCountMaps) {

        StringBuilder sb = new StringBuilder();
        sb.append(shortcuts);
        sb.append(legend);
        sb.append(renderPeriodSummary(metricsIntervalResultCountMaps.getOrgResultCounts()));
        sb.append(renderResultCountMap(metricsIntervalResultCountMaps.getOrgResultCounts()));
        sb.append(renderResultCountMap(metricsIntervalResultCountMaps.getRepoResultCounts()));
        sb.append(renderResultCountMap(metricsIntervalResultCountMaps.getBranchResultCounts()));
        sb.append(renderResultCountMap(metricsIntervalResultCountMaps.getJobResultCounts()));
        return sb.toString();
    }

    private static <T> String renderPeriodSummary(TreeMap<T, TreeMap<InstantRange, RunResultCount>> resultCounts) {
        if (resultCounts == null || resultCounts.isEmpty()) {
            return "";
        }
        TreeMap<InstantRange, RunResultCount> firstEntry = resultCounts.firstEntry().getValue();
        if (firstEntry == null || firstEntry.isEmpty()) {
            return "";
        }
        List<InstantRange> ranges = new ArrayList<>(firstEntry.keySet());
        ranges.sort((a, b) -> b.getStart().compareTo(a.getStart()));

        InstantRange current = ranges.get(0);
        InstantRange previous = ranges.size() > 1 ? ranges.get(1) : null;

        long days = java.time.Duration.between(current.getStart(), current.getEnd()).toDays();

        StringBuilder sb = new StringBuilder();
        sb.append("<dl>").append(ls);
        sb.append("<dt>Period length</dt><dd>").append(days).append(" days</dd>").append(ls);
        sb.append("<dt>Current period</dt><dd>").append(NumberStringUtil.friendlyDateRange(current.getStart(), current.getEnd())).append("</dd>").append(ls);
        if (previous != null) {
            sb.append("<dt>Previous period</dt><dd>").append(NumberStringUtil.friendlyDateRange(previous.getStart(), previous.getEnd())).append("</dd>").append(ls);
        }
        sb.append("</dl>").append(ls);
        return sb.toString();
    }

    private static <T> boolean hasAnyJobTime(TreeMap<T, TreeMap<InstantRange, RunResultCount>> dtoResultCounts) {
        for (TreeMap<InstantRange, RunResultCount> ranges : dtoResultCounts.values()) {
            for (RunResultCount rrc : ranges.values()) {
                if (rrc.getClockDurationSeconds() != null && rrc.getRunCount().getRuns() != null && rrc.getRunCount().getRuns() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static <T> String renderResultCountMap(TreeMap<T, TreeMap<InstantRange, RunResultCount>> dtoResultCounts) {
        if (dtoResultCounts == null || dtoResultCounts.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        String dtoName = null;
        {
            for (Map.Entry<T, TreeMap<InstantRange, RunResultCount>> orgEntry : dtoResultCounts.entrySet()) {
                T t = orgEntry.getKey();
                dtoName = getDtoName(t);
                break;
            }
        }
        final String dtoNameLower = dtoName == null ? null : dtoName.toLowerCase();
        boolean showJobTime = hasAnyJobTime(dtoResultCounts);

        sb.append("<fieldset id='").append(dtoNameLower).append("-fieldset'>").append(ls);
        sb.append("<legend>").append(dtoName).append("</legend>").append(ls);
        sb.append("<table class='sortable' id='").append(dtoNameLower).append("-table'>").append(ls);
        sb.append("<thead>").append(ls);

        // Single header row for stacked layout
        sb.append("<tr>").append(ls);

        for (Map.Entry<T, TreeMap<InstantRange, RunResultCount>> orgEntry : dtoResultCounts.entrySet()) {
            final T t = orgEntry.getKey();
            sb.append(renderDtoHeadersStacked(t));
            break;
        }

        sb.append("<th class='test-header'>Success %</th><th class='test-header'>Δ</th>").append(ls);
        sb.append("<th class='test-header'>Test Runs</th><th class='test-header'>Δ</th>").append(ls);
        sb.append("<th class='test-header'>Duration Avg</th><th class='test-header'>Δ</th>").append(ls);
        if (showJobTime) {
            sb.append("<th class='run-header'>Job Time Avg</th><th class='run-header'>Δ</th>").append(ls);
        }
        sb.append("<th class='run-header'>Job Pass %</th><th class='run-header'>Δ</th>").append(ls);
        sb.append("<th class='run-header'>Job Runs</th><th class='run-header'>Δ</th>").append(ls);

        sb.append("</tr>").append(ls);
        sb.append("</thead>").append(ls);

        sb.append(renderTableBodyStacked(dtoResultCounts, showJobTime));
        sb.append("</table>").append(ls);
        sb.append("</fieldset>").append(ls);
        sb.append("<br>").append(ls);
        return sb.toString();
    }

    private static <T> String renderTableBodyStacked(TreeMap<T, TreeMap<InstantRange, RunResultCount>> orgResultCounts, boolean showJobTime) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tbody>").append(ls);

        for (Map.Entry<T, TreeMap<InstantRange, RunResultCount>> orgEntry : orgResultCounts.entrySet()) {

            // Filter out rows without tests
            boolean rowHasTests = false;
            for (Map.Entry<InstantRange, RunResultCount> rangeRunResultCountEntry : orgEntry.getValue().entrySet()) {
                if (rangeRunResultCountEntry.getValue().getResultCount().getTests() > 0) {
                    rowHasTests = true;
                }
            }
            if (!rowHasTests) {
                continue;
            }

            final T t = orgEntry.getKey();
            final TreeMap<InstantRange, RunResultCount> rangeResultCount = orgEntry.getValue();

            // Get periods in descending order (most recent first)
            List<Map.Entry<InstantRange, RunResultCount>> periods = new ArrayList<>(rangeResultCount.entrySet());
            periods.sort((a, b) -> b.getKey().getStart().compareTo(a.getKey().getStart()));

            RunResultCount currentResult = periods.size() > 0 ? periods.get(0).getValue() : null;
            RunResultCount previousResult = periods.size() > 1 ? periods.get(1).getValue() : null;
            sb.append("<tr>").append(ls);
            sb.append(renderDtoSingleRow(t));
            sb.append(renderInlineDeltaCells(currentResult, previousResult, showJobTime));
            sb.append("</tr>").append(ls);
        }
        sb.append("</tbody>").append(ls);
        return sb.toString();
    }

    private static <T> String renderDtoSingleRow(T t) {
        if (t instanceof CompanyOrgDTO dto) {
            return "<td class='entity-name'>" + dto.getCompany() + "</td>" + ls +
                   "<td class='entity-name'>" + dto.getOrg() + "</td>" + ls;
        }
        if (t instanceof CompanyOrgRepoDTO dto) {
            return "<td class='entity-name'>" + dto.getCompany() + "</td>" + ls +
                   "<td class='entity-name'>" + dto.getOrg() + "</td>" + ls +
                   "<td class='entity-name'>" + dto.getRepo() + "</td>" + ls;
        }
        if (t instanceof CompanyOrgRepoBranchDTO dto) {
            return "<td class='entity-name'>" + dto.getCompany() + "</td>" + ls +
                   "<td class='entity-name'>" + dto.getOrg() + "</td>" + ls +
                   "<td class='entity-name'>" + dto.getRepo() + "</td>" + ls +
                   "<td class='entity-name'>" + dto.getBranch() + "</td>" + ls;
        }
        if (t instanceof CompanyOrgRepoBranchJobInfoDTO dto) {
            return "<td class='entity-name'>" + dto.getCompany() + "</td>" + ls +
                   "<td class='entity-name'>" + dto.getOrg() + "</td>" + ls +
                   "<td class='entity-name'>" + dto.getRepo() + "</td>" + ls +
                   "<td class='entity-name'>" + dto.getBranch() + "</td>" + ls +
                   "<td class='entity-name'>" + StringMapUtil.valuesOnlyColonSeparated(dto.getJobInfo()) + "</td>" + ls;
        }
        throw new IllegalArgumentException("Unsupported type: " + t.getClass().getSimpleName());
    }

    private static String renderInlineDeltaCells(RunResultCount current, RunResultCount previous, boolean showJobTime) {
        StringBuilder sb = new StringBuilder();
        if (current == null) {
            current = RunResultCount.builder().build();
        }

        Integer currRuns = current.getRunCount().getRuns();
        boolean noRuns = currRuns == null || currRuns == 0;
        BigDecimal currTestPct = noRuns ? null : current.getResultCount().getTestSuccessPercent().setScale(0, RoundingMode.HALF_UP);
        Integer currTests = current.getResultCount().getTests();
        BigDecimal currTime = current.getResultCount().getTime();
        BigDecimal currClockDuration = current.getClockDurationSeconds();
        BigDecimal currTestTimeAvg = divide(currTime, currRuns);
        BigDecimal currJobTimeAvg = divide(currClockDuration, currRuns);
        BigDecimal currRunPct = noRuns ? null : current.getRunCount().getRunSuccessPercent().setScale(0, RoundingMode.HALF_UP);

        Integer prevRuns = previous != null ? previous.getRunCount().getRuns() : null;
        boolean prevNoRuns = prevRuns == null || prevRuns == 0;
        BigDecimal prevTestPct = (!prevNoRuns) ? previous.getResultCount().getTestSuccessPercent().setScale(0, RoundingMode.HALF_UP) : null;
        Integer prevTests = previous != null ? previous.getResultCount().getTests() : null;
        BigDecimal prevTime = previous != null ? previous.getResultCount().getTime() : null;
        BigDecimal prevClockDuration = previous != null ? previous.getClockDurationSeconds() : null;
        BigDecimal prevTestTimeAvg = divide(prevTime, prevRuns);
        BigDecimal prevJobTimeAvg = divide(prevClockDuration, prevRuns);
        BigDecimal prevRunPct = (!prevNoRuns) ? previous.getRunCount().getRunSuccessPercent().setScale(0, RoundingMode.HALF_UP) : null;


        // Test pass % (higher is good)
        String currTestPctStr = currTestPct != null ? percentFromBigDecimal(currTestPct) : "-";
        sb.append(renderInlineCell(currTestPctStr,
                sortVal(currTestPct),
                NumberStringUtil.formatDeltaPercent(currTestPct, prevTestPct),
                getDeltaCssClass(currTestPct, prevTestPct, true),
                buildTooltip(prevTestPct != null ? percentFromBigDecimal(prevTestPct) : "—"),
                "percent"));

        // Test executions (more is better)
        sb.append(renderInlineCell(fromIntegerPadded(currTests),
                sortVal(currTests),
                NumberStringUtil.formatDeltaInteger(currTests, prevTests),
                getDeltaCssClassInt(currTests, prevTests, true),
                buildTooltip(prevTests != null ? String.valueOf(prevTests) : "—"),
                "count"));

        // Duration Avg (lower is better)
        sb.append(renderInlineCell(fromSecondBigDecimalPadded(currTestTimeAvg),
                sortVal(currTestTimeAvg),
                NumberStringUtil.formatDeltaDuration(currTestTimeAvg, prevTestTimeAvg),
                getDeltaCssClassDuration(currTestTimeAvg, prevTestTimeAvg),
                buildTooltip(prevTestTimeAvg != null ? fromSecondBigDecimal(prevTestTimeAvg) : "—"),
                "count"));

        // Job Time Avg (lower is better) — column hidden when no rows have data
        if (showJobTime) {
            sb.append(renderInlineCell(
                    currJobTimeAvg != null ? fromSecondBigDecimalPadded(currJobTimeAvg) : "-",
                    sortVal(currJobTimeAvg),
                    currJobTimeAvg != null ? NumberStringUtil.formatDeltaDuration(currJobTimeAvg, prevJobTimeAvg) : "—",
                    getDeltaCssClassDuration(currJobTimeAvg, prevJobTimeAvg),
                    buildTooltip(prevJobTimeAvg != null ? fromSecondBigDecimal(prevJobTimeAvg) : "—"),
                    "count"));
        }

        // Job pass % (higher is good)
        String currRunPctStr = currRunPct != null ? percentFromBigDecimal(currRunPct) : "-";
        sb.append(renderInlineCell(currRunPctStr,
                sortVal(currRunPct),
                NumberStringUtil.formatDeltaPercent(currRunPct, prevRunPct),
                getDeltaCssClass(currRunPct, prevRunPct, true),
                buildTooltip(prevRunPct != null ? percentFromBigDecimal(prevRunPct) : "—"),
                "percent"));

        // Job runs (neutral)
        sb.append(renderInlineCell(fromIntegerPadded(currRuns),
                sortVal(currRuns),
                NumberStringUtil.formatDeltaInteger(currRuns, prevRuns),
                getDeltaCssClassInt(currRuns, prevRuns, false),
                buildTooltip(prevRuns != null ? String.valueOf(prevRuns) : "—"),
                "count"));

        return sb.toString();
    }

    private static String renderInlineCell(String primaryValue, String sortValue, String deltaText, String deltaCssClass,
                                            String tooltip, String cellClass) {
        StringBuilder sb = new StringBuilder();
        sb.append("<td class='").append(cellClass).append("' data-sort='").append(sortValue).append("' data-tooltip='").append(tooltip).append("'>");
        sb.append(primaryValue);
        sb.append("</td>");
        sb.append("<td class='delta'>");
        if (deltaText != null && !deltaText.equals("—")) {
            sb.append("<span class='metric-delta ").append(deltaCssClass).append("'>(").append(deltaText).append(")</span>");
        }
        sb.append("</td>").append(ls);
        return sb.toString();
    }

    private static String sortVal(BigDecimal val) {
        return val != null ? val.toPlainString() : "";
    }

    private static String sortVal(Integer val) {
        return val != null ? val.toString() : "";
    }

    private static String buildTooltip(String previousValue) {
        return escapeHtmlAttr("previous: " + stripHtml(previousValue));
    }

    private static String escapeHtmlAttr(String s) {
        return s.replace("&", "&amp;").replace("\"", "&quot;").replace("'", "&#39;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String stripHtml(String s) {
        if (s == null) return "";
        s = s.replaceAll("<span class='transparent'>[^<]*</span>", "");
        return s.replaceAll("<[^>]*>", "");
    }

    private static String getDeltaCssClassDuration(BigDecimal current, BigDecimal previous) {
        if (current == null || previous == null) return "delta-neutral";
        if (previous.compareTo(BigDecimal.ZERO) == 0) return "delta-neutral";
        // Calculate percentage change
        BigDecimal percentChange = current.subtract(previous)
            .multiply(BigDecimal.valueOf(100))
            .divide(previous.abs(), 0, RoundingMode.HALF_UP);
        // Use DELTA_SIGNIFICANCE_THRESHOLD_PERCENT threshold
        if (percentChange.abs().compareTo(BigDecimal.valueOf(DELTA_SIGNIFICANCE_THRESHOLD_PERCENT)) <= 0) {
            return "delta-neutral";
        }
        // For duration, lower is better (higherIsGood = false)
        String direction = NumberStringUtil.deltaDirection(current.subtract(previous), false);
        return "delta-" + direction;
    }

    private static String getDeltaCssClass(BigDecimal current, BigDecimal previous, boolean higherIsGood) {
        if (current == null || previous == null) {
            return "delta-neutral";
        }
        BigDecimal delta = current.subtract(previous);
        // For percentage metrics, check absolute change (e.g., 92% -> 95% = 3 points)
        BigDecimal absChange = delta.abs();
        if (absChange.compareTo(BigDecimal.valueOf(DELTA_SIGNIFICANCE_THRESHOLD_PERCENT)) <= 0) {
            return "delta-neutral";
        }
        String direction = NumberStringUtil.deltaDirection(delta, higherIsGood);
        return "delta-" + direction;
    }

    private static String getDeltaCssClassInt(Integer current, Integer previous, boolean higherIsGood) {
        if (current == null || previous == null) return "delta-neutral";
        int delta = current - previous;
        // For counts, use relative DELTA_SIGNIFICANCE_THRESHOLD_PERCENT threshold
        if (previous == 0 || Math.abs(delta) * 100 / Math.abs(previous) <= DELTA_SIGNIFICANCE_THRESHOLD_PERCENT) {
            return "delta-neutral";
        }
        String direction = NumberStringUtil.deltaDirection(BigDecimal.valueOf(delta), higherIsGood);
        return "delta-" + direction;
    }

    private static <T> String renderDtoHeadersStacked(T t) {
        if (t instanceof CompanyOrgDTO) {
            return renderOrgHeadersStacked();
        }
        if (t instanceof CompanyOrgRepoDTO) {
            return renderRepoHeadersStacked();
        }
        if (t instanceof CompanyOrgRepoBranchDTO) {
            return renderBranchHeadersStacked();
        }
        if (t instanceof CompanyOrgRepoBranchJobInfoDTO) {
            return renderJobHeadersStacked();
        }
        throw new IllegalArgumentException("Unsupported type: " + t.getClass().getSimpleName());
    }

    private static String renderOrgHeadersStacked() {
        return "<th class='dto-header'>Company</th>" + ls +
               "<th class='dto-header'>Org</th>" + ls;
    }

    private static String renderRepoHeadersStacked() {
        return "<th class='dto-header'>Company</th>" + ls +
               "<th class='dto-header'>Org</th>" + ls +
               "<th class='dto-header'>Repo</th>" + ls;
    }

    private static String renderBranchHeadersStacked() {
        return "<th class='dto-header'>Company</th>" + ls +
               "<th class='dto-header'>Org</th>" + ls +
               "<th class='dto-header'>Repo</th>" + ls +
               "<th class='dto-header'>Branch</th>" + ls;
    }

    private static String renderJobHeadersStacked() {
        return "<th class='dto-header'>Company</th>" + ls +
               "<th class='dto-header'>Org</th>" + ls +
               "<th class='dto-header'>Repo</th>" + ls +
               "<th class='dto-header'>Branch</th>" + ls +
               "<th class='dto-header'>JobInfo</th>" + ls;
    }

    private static <T> String getDtoName(T t) {
        if (t instanceof CompanyOrgDTO) {
            return "Org";
        }
        if (t instanceof CompanyOrgRepoDTO) {
            return "Repo";
        }
        if (t instanceof CompanyOrgRepoBranchDTO) {
            return "Branch";
        }
        if (t instanceof CompanyOrgRepoBranchJobInfoDTO) {
            return "Job";
        }
        throw new IllegalArgumentException("Unsupported type: " + t.getClass().getSimpleName());
    }

    final static String shortcuts =
            """
            <fieldset class="top-fieldset" style="vertical-align:top;">
                <legend>Table of contents</legend>
                <ul class="shortcuts">
                    <li><a href="#org-fieldset">org</a></li>
                    <li><a href="#repo-fieldset">repo</a></li>
                    <li><a href="#branch-fieldset">branch</a></li>
                    <li><a href="#job-fieldset">job</a></li>
                </ul>
            </fieldset>
            """;

    final static String legend =
            """
            <details style="display:inline-block; vertical-align:top;">
            <summary>Definitions</summary>
            <fieldset class='top-fieldset'>
            <dl>
            	<dt>Δ columns</dt>
            	<dd>Change from previous period. Hover any cell for previous value.</dd>

            	<dt>-</dt>
            	<dd>No runs in current period.</dd>

            	<dt>Success %</dt>
            	<dd>The % of test executions which passed.</dd>

            	<dt>Test Runs</dt>
            	<dd>The total number of test executions.</dd>

            	<dt>Duration Avg</dt>
            	<dd>Average test execution time per job run.</dd>

            	<dt>Job Time Avg</dt>
            	<dd>Average wall clock job duration.</dd>

            	<dt>Job Pass %</dt>
            	<dd>The percentage of job runs with no failing tests.</dd>

            	<dt>Job Runs</dt>
            	<dd>Total job runs.</dd>

            	<dt>Delta Colors</dt>
            	<dd>Green = improvement, Red = regression. Only shown for changes &gt;10%.</dd>
            </dl>
            </fieldset>
            </details>
            """;

    public static BigDecimal divide(BigDecimal num, Integer dem) {
        if (num == null || dem == null) {
            return num;
        }
        return divide(num, new BigDecimal(dem));
    }

    public static BigDecimal divide(BigDecimal num, BigDecimal dem) {
        if (num == null || dem == null || BigDecimal.ZERO.equals(dem)) {
            return num;
        }
        return num.divide(dem, RoundingMode.HALF_UP);
    }

}

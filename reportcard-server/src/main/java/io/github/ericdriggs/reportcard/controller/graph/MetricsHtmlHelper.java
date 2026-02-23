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
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
        sb.append("<br>");
        sb.append(renderResultCountMap(metricsIntervalResultCountMaps.getOrgResultCounts()));
        sb.append(renderResultCountMap(metricsIntervalResultCountMaps.getRepoResultCounts()));
        sb.append(renderResultCountMap(metricsIntervalResultCountMaps.getBranchResultCounts()));
        sb.append(renderResultCountMap(metricsIntervalResultCountMaps.getJobResultCounts()));
        return sb.toString();
    }

    private static <T> String renderResultCountMap(TreeMap<T, TreeMap<InstantRange, RunResultCount>> dtoResultCounts) {
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

        sb.append("<th class='interval-header'>Period</th>").append(ls);
        sb.append("<th class='test-header'>Test pass %</th>").append(ls);
        sb.append("<th class='test-header'>Test executions</th>").append(ls);
        sb.append("<th class='test-header'>Test Time Total</th>").append(ls);
        sb.append("<th class='run-header'>Job Time Avg</th>").append(ls);
        sb.append("<th class='run-header'>Job pass %</th>").append(ls);
        sb.append("<th class='run-header'>Job runs</th>").append(ls);

        sb.append("</tr>").append(ls);
        sb.append("</thead>").append(ls);

        sb.append(renderTableBodyStacked(dtoResultCounts));
        sb.append("</table>").append(ls);
        sb.append("</fieldset>").append(ls);
        sb.append("<br>").append(ls);
        return sb.toString();
    }

    private static <T> String renderTableBodyStacked(TreeMap<T, TreeMap<InstantRange, RunResultCount>> orgResultCounts) {
        StringBuilder sb = new StringBuilder();
        
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
            // Sort by start date descending (most recent first)
            periods.sort((a, b) -> b.getKey().getStart().compareTo(a.getKey().getStart()));

            RunResultCount currentResult = periods.size() > 0 ? periods.get(0).getValue() : null;
            RunResultCount previousResult = periods.size() > 1 ? periods.get(1).getValue() : null;
            InstantRange currentRange = periods.size() > 0 ? periods.get(0).getKey() : null;
            InstantRange previousRange = periods.size() > 1 ? periods.get(1).getKey() : null;

            int entityColCount = getEntityColCount(t);
            
            // Each entity group in its own tbody for styling
            sb.append("<tbody class='metrics-group'>").append(ls);
            
            // Current row
            sb.append("<tr class='row-current'>").append(ls);
            sb.append(renderDtoWithRowspan(t, 3)); // Span 3 rows
            sb.append("<td class='period-current'>").append(formatPeriodLabel("Current", currentRange)).append("</td>").append(ls);
            sb.append(renderMetricCells(currentResult));
            sb.append("</tr>").append(ls);
            
            // Previous row
            sb.append("<tr class='row-previous'>").append(ls);
            sb.append("<td class='period-previous'>").append(formatPeriodLabel("Previous", previousRange)).append("</td>").append(ls);
            sb.append(renderMetricCells(previousResult));
            sb.append("</tr>").append(ls);
            
            // Delta row (no background color - text colors only)
            sb.append("<tr class='row-delta'>").append(ls);
            sb.append("<td class='period-delta'>Δ Change %</td>").append(ls);
            sb.append(renderDeltaCells(currentResult, previousResult));
            sb.append("</tr>").append(ls);
            
            sb.append("</tbody>").append(ls);
        }
        return sb.toString();
    }

    private static String formatPeriodLabel(String label, InstantRange range) {
        if (range == null) {
            return "—";
        }
        return NumberStringUtil.friendlyDateRange(range.getStart(), range.getEnd());
    }

    private static String renderMetricCells(RunResultCount resultCount) {
        StringBuilder sb = new StringBuilder();
        if (resultCount == null) {
            resultCount = RunResultCount.builder().build();
        }

        final BigDecimal testSuccessPercent = resultCount.getResultCount().getTestSuccessPercent().setScale(0, RoundingMode.HALF_UP);
        final Integer totalTests = resultCount.getResultCount().getTests();
        final BigDecimal totalTime = resultCount.getResultCount().getTime();
        final Integer runCount = resultCount.getRunCount().getRuns();
        final BigDecimal averageTime = divide(totalTime, runCount);
        final BigDecimal runSuccessPercent = resultCount.getRunCount().getRunSuccessPercent().setScale(0, RoundingMode.HALF_UP);

        sb.append("<td class='percent'>").append(percentFromBigDecimal(testSuccessPercent)).append("</td>").append(ls);
        sb.append("<td class='count'>").append(fromIntegerPadded(totalTests)).append("</td>").append(ls);
        sb.append("<td class='count'>").append(fromSecondBigDecimalPadded(totalTime)).append("</td>").append(ls);
        sb.append("<td class='count'>").append(fromSecondBigDecimalPadded(averageTime)).append("</td>").append(ls);
        sb.append("<td class='percent'>").append(percentFromBigDecimal(runSuccessPercent)).append("</td>").append(ls);
        sb.append("<td class='count'>").append(fromIntegerPadded(runCount)).append("</td>").append(ls);
        return sb.toString();
    }

    private static String renderDeltaCells(RunResultCount current, RunResultCount previous) {
        StringBuilder sb = new StringBuilder();
        
        BigDecimal currTestPct = current != null ? current.getResultCount().getTestSuccessPercent() : null;
        BigDecimal prevTestPct = previous != null ? previous.getResultCount().getTestSuccessPercent() : null;
        Integer currTests = current != null ? current.getResultCount().getTests() : null;
        Integer prevTests = previous != null ? previous.getResultCount().getTests() : null;
        BigDecimal currTime = current != null ? current.getResultCount().getTime() : null;
        BigDecimal prevTime = previous != null ? previous.getResultCount().getTime() : null;
        Integer currRuns = current != null ? current.getRunCount().getRuns() : null;
        Integer prevRuns = previous != null ? previous.getRunCount().getRuns() : null;
        BigDecimal currRunPct = current != null ? current.getRunCount().getRunSuccessPercent() : null;
        BigDecimal prevRunPct = previous != null ? previous.getRunCount().getRunSuccessPercent() : null;
        
        BigDecimal currAvgTime = divide(currTime, currRuns);
        BigDecimal prevAvgTime = divide(prevTime, prevRuns);

        // Test pass % (higher is good)
        sb.append(renderDeltaCell(currTestPct, prevTestPct, true, true));
        // Test executions (neutral)
        sb.append(renderDeltaCellInteger(currTests, prevTests, false));
        // Test Time Total (neutral)
        sb.append(renderDeltaCellDuration(currTime, prevTime));
        // Job Time Avg (neutral - could argue lower is better, but keeping neutral)
        sb.append(renderDeltaCellDuration(currAvgTime, prevAvgTime));
        // Job pass % (higher is good)
        sb.append(renderDeltaCell(currRunPct, prevRunPct, true, true));
        // Job runs (neutral)
        sb.append(renderDeltaCellInteger(currRuns, prevRuns, false));
        
        return sb.toString();
    }

    private static String renderDeltaCell(BigDecimal current, BigDecimal previous, boolean isPercent, boolean higherIsGood) {
        StringBuilder sb = new StringBuilder();
        String value = isPercent ? NumberStringUtil.formatDeltaPercent(current, previous) : "—";
        String cssClass = getDeltaCssClass(current, previous, higherIsGood);
        sb.append("<td class='percent ").append(cssClass).append("'>").append(value).append("</td>").append(ls);
        return sb.toString();
    }

    private static String renderDeltaCellInteger(Integer current, Integer previous, boolean higherIsGood) {
        StringBuilder sb = new StringBuilder();
        String value = NumberStringUtil.formatDeltaInteger(current, previous);
        String cssClass = getDeltaCssClassInt(current, previous, higherIsGood);
        sb.append("<td class='count ").append(cssClass).append("'>").append(value).append("</td>").append(ls);
        return sb.toString();
    }

    private static String renderDeltaCellDuration(BigDecimal current, BigDecimal previous) {
        StringBuilder sb = new StringBuilder();
        String value = NumberStringUtil.formatDeltaDuration(current, previous);
        String cssClass = getDeltaCssClassDuration(current, previous);
        sb.append("<td class='count ").append(cssClass).append("'>").append(value).append("</td>").append(ls);
        return sb.toString();
    }

    private static String getDeltaCssClassDuration(BigDecimal current, BigDecimal previous) {
        if (current == null || previous == null) return "delta-neutral";
        if (previous.compareTo(BigDecimal.ZERO) == 0) return "delta-neutral";
        // Calculate percentage change
        BigDecimal percentChange = current.subtract(previous)
            .multiply(BigDecimal.valueOf(100))
            .divide(previous.abs(), 0, RoundingMode.HALF_UP);
        // Use 5% threshold
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
        // For counts, use relative 5% threshold
        if (previous == 0 || Math.abs(delta) * 100 / Math.abs(previous) <= DELTA_SIGNIFICANCE_THRESHOLD_PERCENT) {
            return "delta-neutral";
        }
        String direction = NumberStringUtil.deltaDirection(BigDecimal.valueOf(delta), higherIsGood);
        return "delta-" + direction;
    }

    private static String getDeltaRowClass(RunResultCount current, RunResultCount previous) {
        // Check if test pass % or job pass % has significant change
        BigDecimal threshold = BigDecimal.valueOf(DELTA_SIGNIFICANCE_THRESHOLD_PERCENT);
        BigDecimal currTestPct = current != null ? current.getResultCount().getTestSuccessPercent() : null;
        BigDecimal prevTestPct = previous != null ? previous.getResultCount().getTestSuccessPercent() : null;
        BigDecimal currRunPct = current != null ? current.getRunCount().getRunSuccessPercent() : null;
        BigDecimal prevRunPct = previous != null ? previous.getRunCount().getRunSuccessPercent() : null;
        
        boolean testSignificant = NumberStringUtil.isSignificantChange(currTestPct, prevTestPct, threshold);
        boolean runSignificant = NumberStringUtil.isSignificantChange(currRunPct, prevRunPct, threshold);
        
        if (testSignificant || runSignificant) {
            // Determine if overall change is good or bad
            BigDecimal testDelta = currTestPct != null && prevTestPct != null ? currTestPct.subtract(prevTestPct) : BigDecimal.ZERO;
            BigDecimal runDelta = currRunPct != null && prevRunPct != null ? currRunPct.subtract(prevRunPct) : BigDecimal.ZERO;
            BigDecimal totalDelta = testDelta.add(runDelta);
            if (totalDelta.compareTo(BigDecimal.ZERO) > 0) {
                return "significant-good";
            } else if (totalDelta.compareTo(BigDecimal.ZERO) < 0) {
                return "significant-bad";
            }
        }
        return "";
    }

    private static <T> int getEntityColCount(T t) {
        if (t instanceof CompanyOrgDTO) return 2;
        if (t instanceof CompanyOrgRepoDTO) return 3;
        if (t instanceof CompanyOrgRepoBranchDTO) return 4;
        if (t instanceof CompanyOrgRepoBranchJobInfoDTO) return 5;
        return 1;
    }

    private static <T> String renderDtoWithRowspan(T t, int rowspan) {
        if (t instanceof CompanyOrgDTO dto) {
            return renderOrgWithRowspan(dto, rowspan);
        }
        if (t instanceof CompanyOrgRepoDTO dto) {
            return renderRepoWithRowspan(dto, rowspan);
        }
        if (t instanceof CompanyOrgRepoBranchDTO dto) {
            return renderBranchWithRowspan(dto, rowspan);
        }
        if (t instanceof CompanyOrgRepoBranchJobInfoDTO dto) {
            return renderJobWithRowspan(dto, rowspan);
        }
        throw new IllegalArgumentException("Unsupported type: " + t.getClass().getSimpleName());
    }

    private static String renderOrgWithRowspan(CompanyOrgDTO dto, int rowspan) {
        return "<td class='entity-name' rowspan='" + rowspan + "'>" + dto.getCompany() + "</td>" + ls +
               "<td class='entity-name' rowspan='" + rowspan + "'>" + dto.getOrg() + "</td>" + ls;
    }

    private static String renderRepoWithRowspan(CompanyOrgRepoDTO dto, int rowspan) {
        return "<td class='entity-name' rowspan='" + rowspan + "'>" + dto.getCompany() + "</td>" + ls +
               "<td class='entity-name' rowspan='" + rowspan + "'>" + dto.getOrg() + "</td>" + ls +
               "<td class='entity-name' rowspan='" + rowspan + "'>" + dto.getRepo() + "</td>" + ls;
    }

    private static String renderBranchWithRowspan(CompanyOrgRepoBranchDTO dto, int rowspan) {
        return "<td class='entity-name' rowspan='" + rowspan + "'>" + dto.getCompany() + "</td>" + ls +
               "<td class='entity-name' rowspan='" + rowspan + "'>" + dto.getOrg() + "</td>" + ls +
               "<td class='entity-name' rowspan='" + rowspan + "'>" + dto.getRepo() + "</td>" + ls +
               "<td class='entity-name' rowspan='" + rowspan + "'>" + dto.getBranch() + "</td>" + ls;
    }

    private static String renderJobWithRowspan(CompanyOrgRepoBranchJobInfoDTO dto, int rowspan) {
        return "<td class='entity-name' rowspan='" + rowspan + "'>" + dto.getCompany() + "</td>" + ls +
               "<td class='entity-name' rowspan='" + rowspan + "'>" + dto.getOrg() + "</td>" + ls +
               "<td class='entity-name' rowspan='" + rowspan + "'>" + dto.getRepo() + "</td>" + ls +
               "<td class='entity-name' rowspan='" + rowspan + "'>" + dto.getBranch() + "</td>" + ls +
               "<td class='entity-name' rowspan='" + rowspan + "'>" + StringMapUtil.valuesOnlyColonSeparated(dto.getJobInfo()) + "</td>" + ls;
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

    // Keep old methods for backwards compatibility if needed
    @SuppressWarnings("unused")
    private static <T> String renderTableBody(TreeMap<T, TreeMap<InstantRange, RunResultCount>> orgResultCounts) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tbody>").append(ls);
        for (Map.Entry<T, TreeMap<InstantRange, RunResultCount>> orgEntry : orgResultCounts.entrySet()) {

            //filter out rows without tests
            boolean rowHasTests = false;
            for (Map.Entry<InstantRange, RunResultCount> rangeRunResultCountEntry : orgEntry.getValue().entrySet()) {
                if (rangeRunResultCountEntry.getValue().getResultCount().getTests() > 0) {
                    rowHasTests = true;
                }
            }
            if (!rowHasTests) {
                continue;
            }


            sb.append("<tr>").append(ls);
            final T t = orgEntry.getKey();
            sb.append(renderDto(t));

            final TreeMap<InstantRange, RunResultCount> rangeResultCount = orgEntry.getValue();
            for (Map.Entry<InstantRange, RunResultCount> rangeRunResultCountEntry : rangeResultCount.entrySet()) {
                RunResultCount resultCount = rangeRunResultCountEntry.getValue();
                if (resultCount == null) {
                    resultCount = RunResultCount.builder().build();
                }

                final BigDecimal testSuccessPercent = resultCount.getResultCount().getTestSuccessPercent().setScale(0, RoundingMode.HALF_UP);
                final Integer totalTests = resultCount.getResultCount().getTests();
                final BigDecimal totalTime = resultCount.getResultCount().getTime();
                final Integer runCount = resultCount.getRunCount().getRuns();
                final BigDecimal averageTime = divide(totalTime, runCount);
                final BigDecimal runSuccessPercent = resultCount.getRunCount().getRunSuccessPercent().setScale(0, RoundingMode.HALF_UP);

                sb.append("<td class='percent'>").append(percentFromBigDecimal(testSuccessPercent)).append("</td>").append(ls);
                sb.append("<td class='count'>").append(fromIntegerPadded(totalTests)).append("</td>").append(ls);
                sb.append("<td class='count'>").append(fromSecondBigDecimalPadded(totalTime)).append("</td>").append(ls);
                sb.append("<td class='count'>").append(fromSecondBigDecimalPadded(averageTime)).append("</td>").append(ls);
                sb.append("<td class='percent'>").append(percentFromBigDecimal(runSuccessPercent)).append("</td>").append(ls);
                sb.append("<td class='count'>").append(fromIntegerPadded(runCount)).append("</td>").append(ls);
            }
        }
        sb.append("</tbody>").append(ls);
        return sb.toString();
    }

    private static String instantRangeHeader(InstantRange instantRange) {

        final String header = "<th class='interval-header' colspan='6'>{start}&nbsp;&nbsp;→&nbsp;&nbsp;{end}</th>";
        return header
                .replace("{start}", instantToYmdhs(instantRange.getStart()))
                .replace("{end}", instantToYmdhs(instantRange.getEnd()));
    }

    private static String instantToYmdhs(Instant instant) {
        final String ISO_YHMDHS_FORMAT = "yyyy-MM-dd HH:mm";
        final DateTimeFormatter isoYmdhsFormatter = DateTimeFormatter.ofPattern(ISO_YHMDHS_FORMAT)
                .withZone(ZoneOffset.UTC);

        if (instant == null) {
            return "????-??-??";
        } else {
            return isoYmdhsFormatter.format(instant);
        }
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

    private static <T> String renderDtoHeaders(T t) {
        if (t instanceof CompanyOrgDTO) {
            return renderOrgHeaders();
        }
        if (t instanceof CompanyOrgRepoDTO) {
            return renderRepoHeaders();
        }
        if (t instanceof CompanyOrgRepoBranchDTO) {
            return renderBranchHeaders();
        }
        if (t instanceof CompanyOrgRepoBranchJobInfoDTO) {
            return renderJobHeaders();
        }
        throw new IllegalArgumentException("Unsupported type: " + t.getClass().getSimpleName());
    }

    private static <T> String renderDto(T t) {
        if (t instanceof CompanyOrgDTO dto) {
            return renderOrg(dto);
        }
        if (t instanceof CompanyOrgRepoDTO dto) {
            return renderRepo(dto);
        }
        if (t instanceof CompanyOrgRepoBranchDTO dto) {
            return renderBranch(dto);
        }
        if (t instanceof CompanyOrgRepoBranchJobInfoDTO dto) {
            return renderJob(dto);
        }
        throw new IllegalArgumentException("Unsupported type: " + t.getClass().getSimpleName());
    }

    private static String renderOrgHeaders() {
        return "<th class='dto-header' rowspan='2'>Company</th>" + ls +
               "<th class='dto-header' rowspan='2'>Org</th>" + ls;
    }

    private static String renderRepoHeaders() {
        return "<th class='dto-header' rowspan='2'>Company</th>" + ls +
               "<th class='dto-header' rowspan='2'>Org</th>" + ls +
               "<th class='dto-header' rowspan='2'>Repo</th>" + ls;
    }

    private static String renderBranchHeaders() {
        return "<th class='dto-header' rowspan='2'>Company</th>" + ls +
               "<th class='dto-header' rowspan='2'>Org</th>" + ls +
               "<th class='dto-header' rowspan='2'>Repo</th>" + ls +
               "<th class='dto-header' rowspan='2'>Branch</th>";
    }

    private static String renderJobHeaders() {
        return "<th class='dto-header' rowspan='2'>Company</th>" + ls +
               "<th class='dto-header' rowspan='2'>Org</th>" + ls +
               "<th class='dto-header' rowspan='2'>Repo</th>" + ls +
               "<th class='dto-header' rowspan='2'>Branch</th>" + ls +
               "<th class='dto-header' rowspan='2'>JobInfo</th>";
    }

    private static String renderOrg(CompanyOrgDTO dto) {
        return "<td>" + dto.getCompany() + "</td>" + ls +
               "<td>" + dto.getOrg() + "</td>" + ls;
    }

    private static String renderRepo(CompanyOrgRepoDTO dto) {
        return "<td>" + dto.getCompany() + "</td>" + ls +
               "<td>" + dto.getOrg() + "</td>" + ls +
               "<td>" + dto.getRepo() + "</td>" + ls;
    }

    private static String renderBranch(CompanyOrgRepoBranchDTO dto) {
        return "<td>" + dto.getCompany() + "</td>" + ls +
               "<td>" + dto.getOrg() + "</td>" + ls +
               "<td>" + dto.getRepo() + "</td>" + ls +
               "<td>" + dto.getBranch() + "</td>" + ls;
    }

    private static String renderJob(CompanyOrgRepoBranchJobInfoDTO dto) {
        return "<td>" + dto.getCompany() + "</td>" + ls +
               "<td>" + dto.getOrg() + "</td>" + ls +
               "<td>" + dto.getRepo() + "</td>" + ls +
               "<td>" + dto.getBranch() + "</td>" + ls +
               "<td>" + StringMapUtil.valuesOnlyColonSeparated(dto.getJobInfo()) + "</td>" + ls;
    }

    final static String shortcuts =
            """
            <fieldset class="top-fieldset">
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
            <fieldset class='top-fieldset'>
            <legend>Definitions</legend>
            <dl>
            	<dt>Period</dt>
            	<dd>Time range for metrics. Current = most recent, Previous = prior period, Δ Change % = percentage difference.</dd>
            	
            	<dt>Test Pass %</dt>
            	<dd>The % of test executions which passed.</dd>
                        
            	<dt>Test Executions</dt>
            	<dd>The total number of executions of tests</dd>
                        
            	<dt>Test Time Total</dt>
            	<dd>Cumulative test time</dd>
            	
            	<dt>Job Time Avg</dt>
            	<dd>Test Time Total / Job Runs</dd>
                        
            	<dt>Job Pass %</dt>
            	<dd>The percentage of job runs with no failing tests</dd>
                        
            	<dt>Job Runs</dt>
            	<dd>Total job runs</dd>
            	
            	<dt>Row Colors</dt>
            	<dd>Blue = Current period, Gray = Previous period. Delta rows show green/red only for changes &gt;5%.</dd>
            </dl>
            </fieldset>
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

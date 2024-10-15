package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgDTO;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchDTO;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobInfoDTO;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoDTO;
import io.github.ericdriggs.reportcard.controller.browse.BrowseHtmlHelper;
import io.github.ericdriggs.reportcard.model.metrics.company.MetricsIntervalResultCountMaps;
import io.github.ericdriggs.reportcard.model.metrics.company.RunResultCount;
import io.github.ericdriggs.reportcard.model.trend.InstantRange;
import org.apache.commons.lang3.tuple.Pair;

import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MetricsHtmlHelper {
    final static String ls = System.lineSeparator();

    public static String renderMetricsIntervalResultCountMaps(MetricsIntervalResultCountMaps metricsIntervalResultCountMaps) {

        List<Pair<String, String>> breadCrumbs = new ArrayList<>();
        breadCrumbs.add(Pair.of("metrics", "metrics"));
        final String main = renderMetricsMain(metricsIntervalResultCountMaps);
        return BrowseHtmlHelper.getPage(main, breadCrumbs, "metrics")
                .replace("<!--additionalLinks-->", "<link rel=\"stylesheet\" href=\"/css/metrics.css\">" + ls);

    }

    private static String renderMetricsMain(MetricsIntervalResultCountMaps metricsIntervalResultCountMaps) {

        StringBuilder sb = new StringBuilder();
        sb.append(shortcuts);
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

        sb.append("<fieldset id='").append(dtoName).append("'-fieldset'>").append(ls);
        sb.append("<legend>").append(dtoName).append("</legend>").append(ls);
        sb.append("<table class='sortable' id='").append(dtoName).append("-table'>").append(ls);
        sb.append("<thead>").append(ls);

        //span headers
        sb.append("<tr>").append(ls);

        for (Map.Entry<T, TreeMap<InstantRange, RunResultCount>> orgEntry : dtoResultCounts.entrySet()) {
            final T t = orgEntry.getKey();
            sb.append(renderDtoHeaders(t));
            break;
        }

        for (Map.Entry<T, TreeMap<InstantRange, RunResultCount>> orgEntry : dtoResultCounts.entrySet()) {
            final TreeMap<InstantRange, RunResultCount> rangeResultMap = orgEntry.getValue();
            for (Map.Entry<InstantRange, RunResultCount> rangeResultEntry : rangeResultMap.entrySet()) {
                sb.append(instantRangeHeader(rangeResultEntry.getKey()));
            }
            break;
        }
        sb.append("</tr>").append(ls);

        //metrics headers
        sb.append("<tr>").append(ls);
        for (Map.Entry<T, TreeMap<InstantRange, RunResultCount>> orgEntry : dtoResultCounts.entrySet()) {
            final String aggregationHeader =
                    """
                    <th>test %</th>
                    <th>tests</th>
                    <th>run %</th>
                    <th>runs</th>
                    """;
            sb.append(aggregationHeader.repeat(orgEntry.getValue().size()));
            break;
        }
        sb.append("</tr>").append(ls);
        sb.append("</thead>").append(ls);

        sb.append(renderTableBody(dtoResultCounts));
        sb.append("</table>").append(ls);
        sb.append("</fieldset>").append(ls);
        sb.append("<br>").append(ls);
        return sb.toString();
    }

    private static <T> String renderTableBody(TreeMap<T, TreeMap<InstantRange, RunResultCount>> orgResultCounts) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tbody>").append(ls);
        for (Map.Entry<T, TreeMap<InstantRange, RunResultCount>> orgEntry : orgResultCounts.entrySet()) {
            sb.append("<tr>").append(ls);
            final T t = orgEntry.getKey();
            sb.append(renderDto(t));

            final TreeMap<InstantRange, RunResultCount> rangeResultCount = orgEntry.getValue();
            for (Map.Entry<InstantRange, RunResultCount> rangeRunResultCountEntry : rangeResultCount.entrySet()) {
                RunResultCount resultCount = rangeRunResultCountEntry.getValue();
                if (resultCount == null) {
                    resultCount = RunResultCount.builder().build();
                }
                sb.append("<td>").append(resultCount.getResultCount().getTestSuccessPercent().setScale(0, RoundingMode.HALF_UP)).append("</td>").append(ls);
                sb.append("<td>").append(resultCount.getResultCount().getTests()).append("</td>").append(ls);
                sb.append("<td>").append(resultCount.getRunCount().getRunSuccessPercent().setScale(0, RoundingMode.HALF_UP)).append("</td>").append(ls);
                sb.append("<td>").append(resultCount.getRunCount().getRuns()).append("</td>").append(ls);
            }
        }
        sb.append("</tbody>").append(ls);
        return sb.toString();
    }

    private static String instantRangeHeader(InstantRange instantRange) {

        final String header = "<th colspan='4'>{start}&nbsp;&nbsp;→&nbsp;&nbsp;{end}</th>";
        return header
                .replace("{start}", instantToYmdhs(instantRange.getStart()))
                .replace("{end}", instantToYmdhs(instantRange.getEnd()));
    }

    private static String instantToYmdhs(Instant instant) {
        final String ISO_YHMDHS_FORMAT = "yyyy-MM-dd'T'HH:mmX";
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
            return "JobInfo";
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
        return "<th rowspan='2'>Company</th>" + ls +
               "<th rowspan='2'>Org</th>" + ls;
    }

    private static String renderRepoHeaders() {
        return "<th rowspan='2'>Company</th>" + ls +
               "<th rowspan='2'>Org</th>" + ls +
               "<th rowspan='2'>Repo</th>" + ls;
    }

    private static String renderBranchHeaders() {
        return "<th rowspan='2'>Company</th>" + ls +
               "<th rowspan='2'>Org</th>" + ls +
               "<th rowspan='2'>Repo</th>" + ls +
               "<th rowspan='2'>Branch</th>";
    }

    private static String renderJobHeaders() {
        return "<th rowspan='2'>Company</th>" + ls +
               "<th rowspan='2'>Org</th>" + ls +
               "<th rowspan='2'>Repo</th>" + ls +
               "<th rowspan='2'>Branch</th>" + ls +
               "<th rowspan='2'>JobInfo</th>";
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
               "<td>" + dto.getJobInfo() + "</td>" + ls;
    }

    final static String shortcuts =
            """
            <fieldset class="shortcuts-fieldset">
                <legend>metrics group by</legend>
                <ul class="shortcuts">
                    <li><a href="#org-fieldset">org</a></li>
                    <li><a href="#repo-fieldset">repo</a></li>
                    <li><a href="#branch-fieldset">branch</a></li>
                    <li><a href="#job-fieldset">job</a></li>
                </ul>
            </fieldset>
            <br>
            """;

}

package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgDTO;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchDTO;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobInfoDTO;
import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoDTO;
import io.github.ericdriggs.reportcard.controller.browse.BrowseHtmlHelper;
import io.github.ericdriggs.reportcard.model.metrics.company.MetricsIntervalResultCountMaps;
import io.github.ericdriggs.reportcard.model.metrics.company.RunResultCount;
import io.github.ericdriggs.reportcard.model.trend.InstantRange;
import io.github.ericdriggs.reportcard.util.StringMapUtil;
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
        final String dtoNameLower = dtoName == null ? null : dtoName.toLowerCase();

        sb.append("<fieldset id='").append(dtoNameLower).append("-fieldset'>").append(ls);
        sb.append("<legend>").append(dtoName).append("</legend>").append(ls);
        sb.append("<table class='sortable' id='").append(dtoNameLower).append("-table'>").append(ls);
        sb.append("<thead>").append(ls);

        //header row 1
        sb.append("<tr>").append(ls);

        for (Map.Entry<T, TreeMap<InstantRange, RunResultCount>> orgEntry : dtoResultCounts.entrySet()) {
            final T t = orgEntry.getKey();
            sb.append(renderDtoHeaders(t));
            break;
        }

        for (Map.Entry<T, TreeMap<InstantRange, RunResultCount>> orgEntry : dtoResultCounts.entrySet()) {
            final String aggregationHeader =
                    """
                    <th class='test-header'>test pass %</th>
                    <th class='test-header'>tests</th>
                    <th class='run-header'>run pass %</th>
                    <th class='run-header'>runs</th>
                    """;
            sb.append(aggregationHeader.repeat(orgEntry.getValue().size()));
            break;
        }

        sb.append("</tr>").append(ls);

        //header row 2
        sb.append("<tr>").append(ls);
        for (Map.Entry<T, TreeMap<InstantRange, RunResultCount>> orgEntry : dtoResultCounts.entrySet()) {
            final TreeMap<InstantRange, RunResultCount> rangeResultMap = orgEntry.getValue();
            for (Map.Entry<InstantRange, RunResultCount> rangeResultEntry : rangeResultMap.entrySet()) {
                sb.append(instantRangeHeader(rangeResultEntry.getKey()));
            }
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
                sb.append("<td class='percent'>").append(resultCount.getResultCount().getTestSuccessPercent().setScale(0, RoundingMode.HALF_UP)).append("%</td>").append(ls);
                sb.append("<td class='count'>").append(String.format("%,d", resultCount.getResultCount().getTests())).append("</td>").append(ls);
                sb.append("<td class='percent'>").append(resultCount.getRunCount().getRunSuccessPercent().setScale(0, RoundingMode.HALF_UP)).append("%</td>").append(ls);
                sb.append("<td class='count'>").append(String.format("%,d", resultCount.getRunCount().getRuns())).append("</td>").append(ls);
            }
        }
        sb.append("</tbody>").append(ls);
        return sb.toString();
    }

    private static String instantRangeHeader(InstantRange instantRange) {

        final String header = "<th class='interval-header' colspan='4'>{start}&nbsp;&nbsp;→&nbsp;&nbsp;{end}</th>";
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
            <fieldset class="shortcuts-fieldset">
                <legend>metrics group by</legend>
                <ul class="shortcuts">
                    <li><a href="#org-fieldset">org</a></li>
                    <li><a href="#repo-fieldset">repo</a></li>
                    <li><a href="#branch-fieldset">branch</a></li>
                    <li><a href="#jobinfo-fieldset">job</a></li>
                </ul>
            </fieldset>
            <br>
            """;

}

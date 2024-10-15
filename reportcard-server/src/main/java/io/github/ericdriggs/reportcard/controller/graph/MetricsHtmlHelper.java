package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgDTO;
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

    public static String render(MetricsIntervalResultCountMaps metricsIntervalResultCountMaps) {

        List<Pair<String, String>> breadCrumbs = new ArrayList<>();
        breadCrumbs.add(Pair.of("metrics", "metrics"));
        final String main = renderMetricsMain(metricsIntervalResultCountMaps);
        return BrowseHtmlHelper.getPage(main, breadCrumbs, "metrics")
                .replace("<!--additionalLinks-->", "<link rel=\"stylesheet\" href=\"/css/metrics.css\">" + ls);

    }

    private static String renderMetricsMain(MetricsIntervalResultCountMaps metricsIntervalResultCountMaps) {

        StringBuilder sb = new StringBuilder();
        final TreeMap<CompanyOrgDTO, TreeMap<InstantRange, RunResultCount>> orgResultCounts = metricsIntervalResultCountMaps.getOrgResultCounts();
        sb.append(shortcuts);
        sb.append(renderOrg(orgResultCounts));
        return sb.toString();
    }

    private static String renderOrg(TreeMap<CompanyOrgDTO, TreeMap<InstantRange, RunResultCount>> orgResultCounts) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class='sortable' id='org-metrics'>").append(ls);
        sb.append("<thead>").append(ls);

        //span headers
        sb.append("<tr>").append(ls);
        for (Map.Entry<CompanyOrgDTO, TreeMap<InstantRange, RunResultCount>> orgEntry : orgResultCounts.entrySet()) {
            sb.append("<th rowspan='2'>org</th>").append(ls);
            sb.append("<th rowspan='2'>repo</th>").append(ls);
            final TreeMap<InstantRange, RunResultCount> rangeResultMap = orgEntry.getValue();
            for (Map.Entry<InstantRange, RunResultCount> rangeResultEntry : rangeResultMap.entrySet()) {
                sb.append(instantRangeHeader(rangeResultEntry.getKey()));
            }
        }
        sb.append("</tr>").append(ls);

        //metrics headers
        sb.append("<tr>").append(ls);
        for (Map.Entry<CompanyOrgDTO, TreeMap<InstantRange, RunResultCount>> orgEntry : orgResultCounts.entrySet()) {
            final String aggregationHeader =
                    """
                    <th>run %</th>
                    <th>test %</th>
                    <th>tests</th>
                    <th>failures</th>
                    """;

            for (int i = 0; i < orgResultCounts.size(); i++) {
                sb.append(aggregationHeader);
            }
        }
        sb.append("</tr>").append(ls);
        sb.append("</thead>").append(ls);

        sb.append("<tbody>").append(ls);
        for (Map.Entry<CompanyOrgDTO, TreeMap<InstantRange, RunResultCount>> orgEntry : orgResultCounts.entrySet()) {
            sb.append("<tr>").append(ls);
            final CompanyOrgDTO companyOrgDTO = orgEntry.getKey();
            sb.append("<td>").append(companyOrgDTO.getCompany()).append("</td>").append(ls);
            sb.append("<td>").append(companyOrgDTO.getOrg()).append("</td>").append(ls);

            final TreeMap<InstantRange, RunResultCount> rangeResultCount = orgEntry.getValue();
            for (Map.Entry<InstantRange, RunResultCount> rangeRunResultCountEntry : rangeResultCount.entrySet()) {
                final RunResultCount resultCount = rangeRunResultCountEntry.getValue();
                sb.append("<td>").append(resultCount.getRunCount().getRunSuccessPercent().setScale(0, RoundingMode.HALF_UP)).append("</td>").append(ls);
                sb.append("<td>").append(resultCount.getResultCount().getTestSuccessPercent()).append("</td>").append(ls);
                sb.append("<td>").append(resultCount.getResultCount().getTests()).append("</td>").append(ls);
                sb.append("<td>").append(resultCount.getResultCount().getFailures()).append("</td>").append(ls);
            }

            final Map<InstantRange, RunResultCount> rangeRunResultCountMap = orgEntry.getValue();
            sb.append("</tr>").append(ls);
        }
        sb.append("</tbody>").append(ls);
        sb.append("</table>").append(ls);
        return sb.toString();
    }

    static String instantRangeHeader(InstantRange instantRange) {

        final String header = "<th colspan='4'>{start}&nbsp;&nbsp;→&nbsp;&nbsp;{end}</th>";
        return header
                .replace("{start}", instantToYmdhs(instantRange.getStart()))
                .replace("{end}", instantToYmdhs(instantRange.getEnd()));
    }

    public static String instantToYmdhs(Instant instant) {
        final String ISO_YHMDHS_FORMAT = "yyyy-MM-dd'T'HH:mmX";
        final DateTimeFormatter isoYmdhsFormatter = DateTimeFormatter.ofPattern(ISO_YHMDHS_FORMAT)
                .withZone(ZoneOffset.UTC);

        if (instant == null) {
            return "????-??-??";
        } else {
            return isoYmdhsFormatter.format(instant);
        }
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

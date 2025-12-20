package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.controller.browse.BrowseHtmlHelper;
import io.github.ericdriggs.reportcard.model.pipeline.JobDashboardMetrics;

import java.util.Collections;
import java.util.List;


import static io.github.ericdriggs.reportcard.util.NumberStringUtil.*;

public class PipelineDashboardHtmlHelper {
    final static String ls = System.lineSeparator();

    public static String renderPipelineDashboard(List<JobDashboardMetrics> metrics, String pipeline) {
        final String main = renderPipelineDashboardMain(metrics, pipeline);
        return BrowseHtmlHelper.getPage(main, Collections.emptyList(), "pipeline-dashboard")
                .replace("<!--additionalLinks-->", "<link rel=\"stylesheet\" href=\"/css/metrics.css\">" + ls);
    }

    private static String renderPipelineDashboardMain(List<JobDashboardMetrics> metrics, String pipeline) {
        StringBuilder sb = new StringBuilder();
        String title = pipeline != null && !pipeline.trim().isEmpty() ? pipeline : "All Pipelines";
        sb.append("<h1>Pipeline Dashboard - ").append(title).append("</h1>").append(ls);
        sb.append(renderPipelineTable(metrics));
        return sb.toString();
    }

    private static String renderPipelineTable(List<JobDashboardMetrics> metrics) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("<table class='sortable' id='pipeline-table'>").append(ls);
        sb.append("<thead>").append(ls);
        sb.append("<tr>").append(ls);
        sb.append("<th>Company</th>").append(ls);
        sb.append("<th>Org</th>").append(ls);
        sb.append("<th>Repo</th>").append(ls);
        sb.append("<th>Branch</th>").append(ls);
        sb.append("<th>Job Info</th>").append(ls);
        sb.append("<th>Days Since Passing</th>").append(ls);
        sb.append("<th>Job Pass %</th>").append(ls);
        sb.append("<th>Test Pass %</th>").append(ls);
        sb.append("</tr>").append(ls);
        sb.append("</thead>").append(ls);
        
        sb.append("<tbody>").append(ls);
        for (JobDashboardMetrics metric : metrics) {
            sb.append("<tr>").append(ls);
            sb.append("<td>").append(metric.getCompany()).append("</td>").append(ls);
            sb.append("<td>").append(metric.getOrg()).append("</td>").append(ls);
            sb.append("<td>").append(metric.getRepo()).append("</td>").append(ls);
            sb.append("<td>").append(metric.getBranch()).append("</td>").append(ls);
            sb.append("<td>").append(metric.getJobInfo()).append("</td>").append(ls);
            
            // Days since passing - show N/A if null
            String daysSince = metric.getDaysSincePassingRun() != null ? 
                metric.getDaysSincePassingRun().toString() : "N/A";
            sb.append("<td>").append(daysSince).append("</td>").append(ls);
            
            // Job pass % and Test pass %
            sb.append("<td class='percent'>").append(percentFromBigDecimal(metric.getJobPassPercent())).append("</td>").append(ls);
            sb.append("<td class='percent'>").append(percentFromBigDecimal(metric.getTestPassPercent())).append("</td>").append(ls);
            sb.append("</tr>").append(ls);
        }
        sb.append("</tbody>").append(ls);
        sb.append("</table>").append(ls);
        
        return sb.toString();
    }

    public static String renderPipelineDashboardMetrics(List<JobDashboardMetrics> metrics, io.github.ericdriggs.reportcard.model.pipeline.JobDashboardRequest request) {
        String jobInfo = request != null ? request.getJobInfo() : null;
        return renderPipelineDashboard(metrics, jobInfo);
    }
}
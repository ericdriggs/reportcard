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
        sb.append(renderFilterForm());
        sb.append(renderPipelineTable(metrics));
        return sb.toString();
    }

    private static String renderFilterForm() {
        StringBuilder sb = new StringBuilder();
        sb.append("<form method='get' style='margin: 20px 0; padding: 15px; border: 1px solid #ccc; background: #f9f9f9;'>").append(ls);
        sb.append("<div style='margin-bottom: 10px;'>").append(ls);
        sb.append("<label for='jobInfoKey' style='display: inline-block; width: 120px; font-weight: bold;'>Job Info Key:</label>").append(ls);
        sb.append("<input type='text' id='jobInfoKey' name='jobInfoKey' style='width: 350px; padding: 5px;' placeholder='e.g., pipeline, application'>").append(ls);
        sb.append("</div>").append(ls);
        sb.append("<div style='margin-bottom: 10px;'>").append(ls);
        sb.append("<label for='jobInfoValue' style='display: inline-block; width: 120px; font-weight: bold;'>Job Info Value:</label>").append(ls);
        sb.append("<input type='text' id='jobInfoValue' name='jobInfoValue' style='width: 350px; padding: 5px;' placeholder='e.g., build_acceptance, commons-utils'>").append(ls);
        sb.append("</div>").append(ls);
        sb.append("<div style='margin-bottom: 10px; margin-left: 120px; font-size: 0.9em; color: #666;'>").append(ls);
        sb.append("Wildcard matching: Use * before or after text (e.g., *acceptance, build*, *commons*). <br>Exact matching recommended for best performance.").append(ls);
        sb.append("</div>").append(ls);
        sb.append("<div style='margin-left: 120px;'>").append(ls);
        sb.append("<button type='submit' style='padding: 8px 20px; background: #007bff; color: white; border: none; cursor: pointer;'>Filter</button>").append(ls);
        sb.append("<button type='button' onclick='window.location.href=window.location.pathname' style='padding: 8px 20px; margin-left: 10px; background: #6c757d; color: white; border: none; cursor: pointer;'>Clear</button>").append(ls);
        sb.append("</div>").append(ls);
        sb.append("<script>").append(ls);
        sb.append("document.querySelector('form').addEventListener('submit', function(e) {").append(ls);
        sb.append("  e.preventDefault();").append(ls);
        sb.append("  const key = document.getElementById('jobInfoKey').value.trim();").append(ls);
        sb.append("  const value = document.getElementById('jobInfoValue').value.trim();").append(ls);
        sb.append("  if (key && value) {").append(ls);
        sb.append("    const jobInfo = key + ':' + value;").append(ls);
        sb.append("    window.location.href = window.location.pathname + '?jobInfo=' + encodeURIComponent(jobInfo);").append(ls);
        sb.append("  }").append(ls);
        sb.append("});").append(ls);
        sb.append("</script>").append(ls);
        sb.append("</form>").append(ls);
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
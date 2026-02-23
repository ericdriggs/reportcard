package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.controller.browse.BrowseHtmlHelper;
import io.github.ericdriggs.reportcard.model.pipeline.JobDashboardMetrics;
import io.github.ericdriggs.reportcard.util.NumberStringUtil;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;


import static io.github.ericdriggs.reportcard.util.NumberStringUtil.*;

public class PipelineDashboardHtmlHelper {
    final static String ls = System.lineSeparator();

    public static String renderPipelineDashboard(List<JobDashboardMetrics> metrics, String pipeline, Integer days) {
        final String main = renderPipelineDashboardMain(metrics, pipeline, days);
        return BrowseHtmlHelper.getPage(main, Collections.emptyList(), "pipeline-dashboard")
                .replace("<!--additionalLinks-->", "<link rel=\"stylesheet\" href=\"/css/metrics.css\">" + ls +
                        "<style>.pipeline-dashboard { margin-left: 20px; }</style>" + ls);
    }

    private static String renderPipelineDashboardMain(List<JobDashboardMetrics> metrics, String pipeline, Integer days) {
        StringBuilder sb = new StringBuilder();
        String title = pipeline != null && !pipeline.trim().isEmpty() ? pipeline : "All Jobs";
        sb.append("<h1>Org Jobs - ").append(title).append("</h1>").append(ls);
        String daysText = days != null ? days.toString() : "90";
        sb.append("<p style='margin-left: 20px; font-size: 1.1em;'>Metrics calculated from runs in the last <strong>").append(daysText).append(" days</strong></p>").append(ls);
        sb.append(renderFilterForm());
        sb.append(renderPipelineTable(metrics));
        return sb.toString();
    }

    private static String renderFilterForm() {
        StringBuilder sb = new StringBuilder();
        sb.append("<fieldset style='margin: 20px 0; padding: 15px; border: 1px solid #ccc; background: #f9f9f9;'>").append(ls);
        sb.append("<legend style='font-weight: bold;'>Filter by Job Info</legend>").append(ls);
        sb.append("<form method='get'>").append(ls);
        sb.append("<div style='margin-bottom: 10px;'>").append(ls);
        sb.append("<label for='jobInfoKey' style='display: inline-block; width: 80px; font-weight: bold;'>Key:</label>").append(ls);
        sb.append("<input type='text' id='jobInfoKey' name='jobInfoKey' style='width: 250px; padding: 5px; margin-right: 20px;' placeholder='e.g. application'>").append(ls);
        sb.append("<label for='jobInfoValue' style='display: inline-block; width: 80px; font-weight: bold;'>Value:</label>").append(ls);
        sb.append("<input type='text' id='jobInfoValue' name='jobInfoValue' style='width: 250px; padding: 5px;' placeholder='e.g. my_app'>").append(ls);
        sb.append("</div>").append(ls);
        sb.append("<div style='margin-bottom: 15px;'>").append(ls);
        sb.append("<label for='jobInfoKey2' style='display: inline-block; width: 80px; font-weight: bold;'>Key:</label>").append(ls);
        sb.append("<input type='text' id='jobInfoKey2' name='jobInfoKey2' style='width: 250px; padding: 5px; margin-right: 20px;' placeholder='e.g. pipeline'>").append(ls);
        sb.append("<label for='jobInfoValue2' style='display: inline-block; width: 80px; font-weight: bold;'>Value:</label>").append(ls);
        sb.append("<input type='text' id='jobInfoValue2' name='jobInfoValue2' style='width: 250px; padding: 5px;' placeholder='e.g. staging'>").append(ls);
        sb.append("</div>").append(ls);
        sb.append("<div style='margin-bottom: 15px;'>").append(ls);
        sb.append("<label for='days' style='display: inline-block; width: 80px; font-weight: bold;'>Days:</label>").append(ls);
        sb.append("<input type='number' id='days' name='days' style='width: 100px; padding: 5px;' placeholder='90' min='1'>").append(ls);
        sb.append("</div>").append(ls);
        sb.append("<div style='margin-bottom: 10px;'>").append(ls);
        sb.append("<button type='submit' style='padding: 8px 20px; background: #007bff; color: white; border: none; cursor: pointer;'>Filter</button>").append(ls);
        sb.append("<button type='button' onclick='window.location.href=window.location.pathname' style='padding: 8px 20px; margin-left: 10px; background: #6c757d; color: white; border: none; cursor: pointer;'>Clear</button>").append(ls);
        sb.append("</div>").append(ls);
        sb.append("<div style='font-size: 0.9em; color: #666;'>").append(ls);
        sb.append("Wildcard matching: Use * before or after text (e.g., *acceptance, build*, *commons*).<br>").append(ls);
        sb.append("Exact matching recommended for best performance.").append(ls);
        sb.append("</div>").append(ls);
        sb.append("<script>").append(ls);
        sb.append("// Populate form from query string on page load").append(ls);
        sb.append("(function() {").append(ls);
        sb.append("  const params = new URLSearchParams(window.location.search);").append(ls);
        sb.append("  const jobInfos = params.getAll('jobInfo');").append(ls);
        sb.append("  if (jobInfos.length > 0) {").append(ls);
        sb.append("    const parts = jobInfos[0].split(':', 2);").append(ls);
        sb.append("    if (parts.length === 2) {").append(ls);
        sb.append("      document.getElementById('jobInfoKey').value = parts[0];").append(ls);
        sb.append("      document.getElementById('jobInfoValue').value = parts[1];").append(ls);
        sb.append("    }").append(ls);
        sb.append("  }").append(ls);
        sb.append("  if (jobInfos.length > 1) {").append(ls);
        sb.append("    const parts = jobInfos[1].split(':', 2);").append(ls);
        sb.append("    if (parts.length === 2) {").append(ls);
        sb.append("      document.getElementById('jobInfoKey2').value = parts[0];").append(ls);
        sb.append("      document.getElementById('jobInfoValue2').value = parts[1];").append(ls);
        sb.append("    }").append(ls);
        sb.append("  }").append(ls);
        sb.append("  const days = params.get('days');").append(ls);
        sb.append("  if (days) {").append(ls);
        sb.append("    document.getElementById('days').value = days;").append(ls);
        sb.append("  }").append(ls);
        sb.append("})();").append(ls);
        sb.append("// Handle form submission").append(ls);
        sb.append("document.querySelector('form').addEventListener('submit', function(e) {").append(ls);
        sb.append("  e.preventDefault();").append(ls);
        sb.append("  const key = document.getElementById('jobInfoKey').value.trim();").append(ls);
        sb.append("  const value = document.getElementById('jobInfoValue').value.trim();").append(ls);
        sb.append("  const key2 = document.getElementById('jobInfoKey2').value.trim();").append(ls);
        sb.append("  const value2 = document.getElementById('jobInfoValue2').value.trim();").append(ls);
        sb.append("  const days = document.getElementById('days').value.trim();").append(ls);
        sb.append("  const params = new URLSearchParams();").append(ls);
        sb.append("  if (key && value) {").append(ls);
        sb.append("    params.append('jobInfo', key + ':' + value);").append(ls);
        sb.append("  }").append(ls);
        sb.append("  if (key2 && value2) {").append(ls);
        sb.append("    params.append('jobInfo', key2 + ':' + value2);").append(ls);
        sb.append("  }").append(ls);
        sb.append("  if (days) {").append(ls);
        sb.append("    params.append('days', days);").append(ls);
        sb.append("  }").append(ls);
        sb.append("  if (params.toString()) {").append(ls);
        sb.append("    window.location.href = window.location.pathname + '?' + params.toString();").append(ls);
        sb.append("  }").append(ls);
        sb.append("});").append(ls);
        sb.append("</script>").append(ls);
        sb.append("</form>").append(ls);
        sb.append("</fieldset>").append(ls);
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
        sb.append("<th>Days since SUCCESS</th>").append(ls);
        sb.append("<th>Last Run</th>").append(ls);
        sb.append("<th>Job Pass %</th>").append(ls);
        sb.append("<th>Test Pass %</th>").append(ls);
        sb.append("<th>Avg Run Duration</th>").append(ls);
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
            
            // Days since passing - show 0 (SUCCESS) if 0, N/A if null
            String daysSince;
            if (metric.getDaysSincePassingRun() == null) {
                daysSince = "N/A";
            } else if (metric.getDaysSincePassingRun() == 0) {
                daysSince = "0 (SUCCESS)";
            } else {
                daysSince = metric.getDaysSincePassingRun().toString();
            }
            String lastPassTooltip = NumberStringUtil.isoUtcTimestamp(metric.getLastPassingRun());
            sb.append("<td title=\"").append(lastPassTooltip).append("\">").append(daysSince).append("</td>").append(ls);
            
            // Last Run - relative time with absolute tooltip
            String lastRunDisplay = formatLastRun(metric.getLastRun());
            String lastRunTooltip = NumberStringUtil.isoUtcTimestamp(metric.getLastRun());
            sb.append("<td title=\"").append(lastRunTooltip).append("\">").append(lastRunDisplay).append("</td>").append(ls);
            
            // Job pass % and Test pass %
            sb.append("<td class='percent'>").append(percentFromBigDecimal(metric.getJobPassPercent())).append("</td>").append(ls);
            sb.append("<td class='percent'>").append(percentFromBigDecimal(metric.getTestPassPercent())).append("</td>").append(ls);
            sb.append("<td>").append(formatDuration(metric.getAvgRunDuration())).append("</td>").append(ls);
            sb.append("</tr>").append(ls);
        }
        sb.append("</tbody>").append(ls);
        sb.append("</table>").append(ls);
        
        sb.append("<fieldset style='margin-top: 20px; padding: 15px; border: 1px solid #ccc; background: #f9f9f9;'>").append(ls);
        sb.append("<legend style='font-weight: bold;'>Field Descriptions</legend>").append(ls);
        sb.append("<dl style='display: table; border-collapse: collapse; width: 100%;'>").append(ls);
        sb.append("<dt style='display: table-cell; border: 1px solid #ccc; padding: 8px; font-weight: bold; background: #f5f5f5;'>Days since SUCCESS</dt>").append(ls);
        sb.append("<dd style='display: table-cell; border: 1px solid #ccc; padding: 8px; margin: 0;'>Number of days since the last successful run. 0 (SUCCESS) means the job passed today. Hover for exact timestamp.</dd>").append(ls);
        sb.append("<dt style='display: table-cell; border: 1px solid #ccc; padding: 8px; font-weight: bold; background: #f5f5f5;'>Last Run</dt>").append(ls);
        sb.append("<dd style='display: table-cell; border: 1px solid #ccc; padding: 8px; margin: 0;'>Time since the job last ran, regardless of pass/fail. Hover for exact timestamp.</dd>").append(ls);
        sb.append("<dt style='display: table-cell; border: 1px solid #ccc; padding: 8px; font-weight: bold; background: #f5f5f5;'>Job Pass %</dt>").append(ls);
        sb.append("<dd style='display: table-cell; border: 1px solid #ccc; padding: 8px; margin: 0;'>Percentage of runs where every test in the job passed. Calculated as: (passing runs / total runs).</dd>").append(ls);
        sb.append("<dt style='display: table-cell; border: 1px solid #ccc; padding: 8px; font-weight: bold; background: #f5f5f5;'>Test Pass %</dt>").append(ls);
        sb.append("<dd style='display: table-cell; border: 1px solid #ccc; padding: 8px; margin: 0;'>Percentage of individual tests that passed. Calculated as: (passing tests / total tests) across all runs.</dd>").append(ls);
        sb.append("<dt style='display: table-cell; border: 1px solid #ccc; padding: 8px; font-weight: bold; background: #f5f5f5;'>Avg Run Duration</dt>").append(ls);
        sb.append("<dd style='display: table-cell; border: 1px solid #ccc; padding: 8px; margin: 0;'>Average wall clock execution time per run. Calculated as the time from earliest stage start to latest stage end for each run, then averaged across runs with timing data. Displays \"-\" for jobs without timing data.</dd>").append(ls);
        sb.append("</dl>").append(ls);
        sb.append("</fieldset>").append(ls);
        
        return sb.toString();
    }

    public static String renderPipelineDashboardMetrics(List<JobDashboardMetrics> metrics, io.github.ericdriggs.reportcard.model.pipeline.JobDashboardRequest request) {
        String title = request.getOrg() != null ?
            request.getCompany() + "/" + request.getOrg() :
            request.getCompany();
        return renderPipelineDashboard(metrics, title, request.getDays());
    }

    private static String formatDuration(BigDecimal durationSeconds) {
        if (durationSeconds == null) {
            return "-";  // Per DISP-03 requirement
        }
        return NumberStringUtil.fromSecondBigDecimalPadded(durationSeconds);
    }

    private static String formatLastRun(Instant lastRun) {
        if (lastRun == null) {
            return "—";
        }
        Duration duration = Duration.between(lastRun, Instant.now());
        return NumberStringUtil.fromSecondBigDecimalPadded(duration);
    }
}
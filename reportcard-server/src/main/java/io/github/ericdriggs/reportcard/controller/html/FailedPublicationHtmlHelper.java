package io.github.ericdriggs.reportcard.controller.html;

import io.github.ericdriggs.reportcard.controller.browse.BrowseHtmlHelper;
import io.github.ericdriggs.reportcard.model.publication.FailedPublication;
import io.github.ericdriggs.reportcard.model.publication.FailedPublicationStorage;
import io.github.ericdriggs.reportcard.model.publication.FailedPublicationsResponse;

public class FailedPublicationHtmlHelper extends BrowseHtmlHelper {

    public static String getFailedPublicationsPage(FailedPublicationsResponse response) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<div>").append(ls)
          .append("<h2>Suspected failed publications</h2>").append(ls)
          .append("<p>Stages with storage and no test result, run between ")
          .append(escapeHtml(String.valueOf(response.getDateCutoff()))).append(" and ")
          .append(escapeHtml(String.valueOf(response.getGraceCutoff()))).append(" (limit ")
          .append(response.getLimit()).append(").</p>").append(ls)
          .append("<table class=\"failed-publications\">").append(ls)
          .append("<thead><tr><th>Company</th><th>Org</th><th>Repo</th><th>Branch</th><th>Job Info</th><th>Run Id</th>")
          .append("<th>Run Reference</th><th>Sha</th><th>Run Date</th><th>Stage</th><th>Storage</th></tr></thead>").append(ls)
          .append("<tbody>").append(ls);
        for (FailedPublication publication : response.getFailedPublications()) {
            sb.append("<tr class=\"failed-publication\" data-stage-id=\"").append(publication.getStageId()).append("\">")
              .append(cell(publication.getCompanyName()))
              .append(cell(publication.getOrgName()))
              .append(cell(publication.getRepoName()))
              .append(cell(publication.getBranchName()))
              .append(cell(publication.getJobInfo()))
              .append(cell(String.valueOf(publication.getRunId())))
              .append(cell(publication.getRunReference()))
              .append(cell(publication.getSha()))
              .append(cell(String.valueOf(publication.getRunDate())))
              .append(cell(publication.getStageName()))
              .append("<td>");
            for (FailedPublicationStorage storage : publication.getStorages().values()) {
                sb.append("<a href=\"").append(escapeHtml(storage.getUrl())).append("\">")
                  .append(escapeHtml(storage.getLabel()))
                  .append(Boolean.TRUE.equals(storage.getIsUploadComplete()) ? " (complete)" : " (incomplete)")
                  .append("</a><br>");
            }
            sb.append("</td></tr>").append(ls);
        }
        sb.append("</tbody>").append(ls)
          .append("</table>").append(ls)
          .append("</div>").append(ls);
        return getPage(sb.toString(), getBreadCrumb(null));
    }

    static String cell(String value) {
        return "<td>" + escapeHtml(value) + "</td>";
    }
}

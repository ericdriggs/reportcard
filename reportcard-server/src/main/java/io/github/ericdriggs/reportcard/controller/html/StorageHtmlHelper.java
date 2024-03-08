package io.github.ericdriggs.reportcard.controller.html;

import io.github.ericdriggs.reportcard.cache.dto.CompanyOrgRepoBranchJobRunStageDTO;
import io.github.ericdriggs.reportcard.controller.StorageController;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StorageHtmlHelper extends HtmlHelper {

    ;//static methods only

    public static String getS3BrowsePage(ListObjectsV2Response response, String requestKey) {

        StringBuilder sb = new StringBuilder();
        sb.append("<div style='margin:30px'>").append(ls);
        sb.append("<ul>").append(ls);
        List<CommonPrefix> folders = response.commonPrefixes();
        for (CommonPrefix folder : folders) {
            final String prefix = folder.prefix();// .replaceAll("/$", "");

            final String[] parts = prefix.split("/");
            final String folderName = parts[parts.length - 1];
            //final String folderName = StringUtils.substringAfterLast(prefix, "/" );
            sb.append("<li>");
            sb.append("<a href=\"" + getPrefixUrl(prefix) + "\">");
            sb.append("<img alt=\"" + folderName + "\" src=\"/image/folder.svg\" class=\"report-img\" style='width:22px'>");
            sb.append(folderName);
            sb.append("</a>");
            sb.append("</li>").append(ls);
        }
        Collection<S3Object> s3Objects = response.contents();

        for (S3Object obj : s3Objects) {
            final String key = obj.key();
            final String fileName = StringUtils.substringAfterLast(key, "/");
            final String extension = StringUtils.substringAfterLast(key, ".");
            sb.append("<li>");
            sb.append("<a href=\"" + getPrefixUrl(key) + "\">");
            sb.append("<img alt=\"" + fileName + "\" src=\"" + ExtensionImage.getImageForExtension(extension) + "\" class=\"report-img\" >");
            sb.append(fileName);
            sb.append("</a>");
            sb.append("</li>").append(ls);
        }

        sb.append("</ul>").append(ls);
        sb.append("</div>").append(ls);
        //TODO: breadcrumb;
        return getPage(sb.toString(), getBreadCrumbForKey(requestKey));
    }

    protected static String getPrefixUrl(String prefix) {
        return StorageController.storageKeyPath + "/" + prefix;
    }

    protected static List<Pair<String, String>> getBreadCrumbForKey(String key) {
        List<Pair<String, String>> breadCrumbs = new ArrayList<>();
        breadCrumbs.add(Pair.of("home", getUrl(null)));

        //key = key.replaceAll("/$", "");
        String[] keyParts = key.split("/");

        String currentKeyPath = "";
        for (String keyPart : keyParts) {
            currentKeyPath = currentKeyPath + keyPart + "/";

            breadCrumbs.add(Pair.of(keyPart,
                    getPrefixUrl(currentKeyPath)));

        }
        return breadCrumbs;
    }

}

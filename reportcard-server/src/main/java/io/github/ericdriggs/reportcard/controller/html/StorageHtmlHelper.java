package io.github.ericdriggs.reportcard.controller.html;

import io.github.ericdriggs.reportcard.aws.comparatorr.CommonPrefixDescendingComparator;
import io.github.ericdriggs.reportcard.aws.comparatorr.S3ObjectComparator;

import io.github.ericdriggs.reportcard.controller.StorageController;
import io.github.ericdriggs.reportcard.controller.browse.BrowseHtmlHelper;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class StorageHtmlHelper extends BrowseHtmlHelper {

    ;//static methods only

    public static String getS3BrowsePage(ListObjectsV2Response response, String requestKey) {

        StringBuilder sb = new StringBuilder();
        sb.append("<div style='margin:30px'>").append(ls);
        sb.append("<ul>").append(ls);
        TreeSet<CommonPrefix> folders = new TreeSet<>(CommonPrefixDescendingComparator.INSTANCE);
        folders.addAll(response.commonPrefixes());
        for (CommonPrefix folder : folders) {
            String prefix = folder.prefix();// .replaceAll("/$", "");
            //hack because s3 ListObject isn't returning junit.tar.gz
            if (prefix.endsWith("junit/")) {
                prefix = prefix + "junit.tar.gz";
            }

            final String[] parts = prefix.split("/");
            String folderName = parts[parts.length - 1];
            //hack because s3 ListObject isn't returning junit.tar.gz
            if (folderName.endsWith("junit/")) {
                folderName = folderName + "junit.tar.gz";
            }

            //final String folderName = StringUtils.substringAfterLast(prefix, "/" );
            sb.append("<li>");
            sb.append("<a href=\"" + getPrefixUrl(prefix) + "\">");
            sb.append("<img alt=\"" + folderName + "\" src=\"/image/folder.svg\" class=\"report-img\" style='width:22px'>");
            sb.append(folderName);
            sb.append("</a>");
            sb.append("</li>").append(ls);
        }
        TreeSet<S3Object> s3Objects = new TreeSet<>(S3ObjectComparator.INSTANCE);
        s3Objects.addAll(response.contents());

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

    public static String getStorageUrl(StoragePojo storage) {
        StringBuilder sb = new StringBuilder();
        sb.append(StorageController.storageKeyPath );
        sb.append("/" + storage.getPrefix());
        if (storage.getIndexFile() != null) {
            sb.append("/" + storage.getIndexFile());
        }
        return sb.toString();
    }

    static String getPrefixUrl(String prefix) {
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

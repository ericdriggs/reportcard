package io.github.ericdriggs.reportcard.util.badge;

import io.github.ericdriggs.reportcard.persist.StorageType;
import io.github.ericdriggs.reportcard.util.badge.dto.RunBadgeDTO;

import java.net.URI;
import java.time.Instant;

public enum BadgeHtmlHelper {
    ;//static methods only


    public static String html(String label, URI uri) {
        final String badgeObject = objectData(htmlPath(label));
        return wrapLink(badgeObject, uri);
    }

    public static String lastSuccess(RunIdIDateShaUri r) {
        final String badgeObject = objectData(lastSuccessPath(r.getRunCount(), r.getRunDate(), r.getSha()));
        return wrapLink(badgeObject, r.getUri());
    }
    public static String status(BadgeStatusUri badgeStatusUri) {
        final String badgeObject = objectData(statusPath(badgeStatusUri.getBadgeStatus()));
        return wrapLink(badgeObject, badgeStatusUri.getUri());
    }

    public static String statusDateShaBadge(RunBadgeDTO r) {
        final String badgeObject = objectData(statusDateShaPath(r.getBadgeStatus(), r.getRunDate(), r.getSha()));
        return wrapLink(badgeObject, r.getUri());
    }

    public static String storage(StorageTypeUriLabel s) {
        final StorageType storageType = s.getStorageType();
        final URI uri = s.getUri();
        final String label = s.getLabel();
        if (StorageType.JUNIT.equals(storageType)) {
            return xml(label, uri);
        } else if (StorageType.XML.equals(storageType)) {
            return xml(label, uri);
        }
        return html(label, uri);
    }

    public static String xml(String label, URI uri) {
        final String badgeObject = objectData(xmlPath(label));
        return wrapLink(badgeObject, uri);
    }

    public static String trend(URI uri) {
        final String badgeObject = objectData(trendPath());
        return wrapLink(badgeObject, uri);
    }

    static String htmlPath(String label) {
        return "/badge/html/" + label;
    }

    static String lastSuccessPath(int runNumber, Instant date, String sha) {
        return "/badge/lastsuccess/" + runNumber + "?date=" + date.toString() + "&sha=" + sha;
    }

    static String objectData(String dataUrl) {
        return """
                <object data=\"{dataUrl}\"></object>
                """.replace("{dataUrl}", dataUrl);
    }

    static String statusDateShaPath(BadgeStatus badgeStatus, Instant date, String sha) {
        return "/badge/statusdatesha/" + badgeStatus.getText() + "?date=" + date.toString() + "&sha=" + sha;
    }

    static String statusPath(BadgeStatus badgeStatus) {
        return "/badge/status/" + badgeStatus.getText();
    }

    static String trendPath() {
        return "/badge/trend";
    }

    static String wrapLink(String content, URI uri) {
        return wrapLink(content, uri.toString());
    }

    static String wrapLink(String content, String url) {
        return """
                <a href="{url}">{content}</a>
                """.replace("{content}", content)
                .replace("{url}", url);
    }


    static String xmlPath(String label) {
        return "/badge/xml/" + label;
    }

}

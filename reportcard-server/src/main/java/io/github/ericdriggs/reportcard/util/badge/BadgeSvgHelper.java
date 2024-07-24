package io.github.ericdriggs.reportcard.util.badge;

import io.github.ericdriggs.reportcard.persist.StorageType;
import io.github.ericdriggs.reportcard.util.badge.dto.RunBadgeDTO;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static io.github.ericdriggs.reportcard.util.badge.BadgeHtmlHelper.wrapLink;

@SuppressWarnings("TrailingWhitespacesInTextBlock")
public enum BadgeSvgHelper {

    ;//static methods only

    private static final String PATTERN_FORMAT = "yyyy-MM-dd";
    private static final DateTimeFormatter ymdFormat = DateTimeFormatter.ofPattern(PATTERN_FORMAT).withZone(ZoneOffset.UTC);

    public static String storage(StorageType storageType, String label, URI uri) {
        if (StorageType.JUNIT.equals(storageType)) {
            return xml(label, uri);
        } else if (StorageType.XML.equals(storageType)) {
            return xml(label, uri);
        }
        return html(label, uri);
    }

    public static String html(String label, URI uri) {
        final String badgeSvg = badgeHtml(label);
        return wrapLink(badgeSvg, uri);
    }

    public static String lastSuccess(RunIdIDateShaUri r) {
        final String badgeSvg = badgeLastSuccessDateSha(r.getRunCount(), r.getRunDate(), r.getSha());
        return wrapLink(badgeSvg, r.getUri());
    }
    public static String status(BadgeStatusUri badgeStatusUri) {
        final String badgeSvg = badgeStatus(badgeStatusUri.getBadgeStatus());
        return wrapLink(badgeSvg, badgeStatusUri.getUri());
    }

    public static String statusDateSha(RunBadgeDTO r) {
        final String badgeSvg = badgeStatusDateSha(r.getBadgeStatus(), r.getRunDate(), r.getSha());
        return wrapLink(badgeSvg, r.getUri());
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
        final String badgeSvg = badgeXml(label);
        return wrapLink(badgeSvg, uri);
    }

    public static String trend(URI uri) {
        final String badgeSvg = badgeTrend();
        return wrapLink(badgeSvg, uri);
    }

    public static String badgeHtml(String label) {
        return htmlBase.replace("{label}", label);
    }

    public static String badgeXml(String label) {
        return xmlBase.replace("{label}", label);
    }

    public static String badgeStatusDateSha(BadgeStatus badgeStatus, Instant runDate, String sha) {
        return statusDateShaBase
                .replace("{statusColor}", badgeStatus.getColor())
                .replace("{statusText}", badgeStatus.getText())
                .replace("{date}", ymdString(runDate))
                .replace("{sha}", truncatedSha(sha)
                );
    }

    public static String badgeStatus(BadgeStatus badgeStatus) {
        return statusBase
                .replace("{statusColor}", badgeStatus.getColor())
                .replace("{statusText}", badgeStatus.getText()
                );
    }

    public static String badgeLastSuccessDateSha(int runCount, Instant runDate, String sha) {
        return statusDateShaBase
                .replace("{statusColor}", BadgeStatus.LAST_SUCCESS.getColor())
                .replace("{statusText}", Integer.toString(runCount))
                .replace("{date}", ymdString(runDate))
                .replace("{sha}", truncatedSha(sha));
    }

    public static String badgeTrend() {
        return trendBase;
    }

    static String truncatedSha(String sha) {
        if (sha == null) {
            return "null";
        }
        int end = Math.min(sha.length(), 6);
        return sha.substring(0, end) + "…";
    }

    public static String ymdString(Instant instant) {
        if (instant == null) {
            return "null";
        }
        return ymdFormat.format(instant);
    }

    final static String htmlBase =
            """
            <?xml version="1.0"?>
            <svg xmlns="http://www.w3.org/2000/svg" width="52" height="20">
                <title>Html badge {label}</title>
                        
                <linearGradient id="a" x2="0" y2="100%">
                    <stop offset="0" stop-color="#bbb" stop-opacity=".1"/>
                </linearGradient>
                        
                <rect x="0" rx="3" width="52" height="20" fill="#ddd"/>
                        
                <g fill="#00c" text-anchor="middle" font-family="Arial Black, sans-serif" font-size="12" font-weight="bolder">
                    <text x="21.5" y="13px">HTML</text>
                </g>
                <g text-anchor="middle" font-family="courier" font-size="8px" color="black">
                    <text x="46" y="11">🔗</text>
                </g>
                <g fill="#008" text-anchor="middle" font-family="arial" font-size="6px" font-weight="bold">
                    <text x="50%" y="92%" style="inline-size: 8px">{label}</text>
                </g>
            </svg>
            """;

    final static String xmlBase =
            """
            <?xml version="1.0"?>
            <svg xmlns="http://www.w3.org/2000/svg" width="54" height="20">
                <title>XML badge {label}</title>                               
                <rect x="0" rx="3" width="52" height="20" fill="#444"/>
                       
                <g fill="#fa0" text-anchor="middle" font-family="Arial Black, sans-serif" font-size="11" font-weight="bolder">
                    <text x="16.5" y="15px">XML</text>
                </g>
                   
                <g fill="#5c5" text-anchor="middle" font-family="arial" font-size="8px" font-weight="bold">
                    <text x="75%" y="14px" style="inline-size: 8px">{label}</text>
                </g>
            </svg>
            """;

    final static String statusBase =
            """
            <?xml version="1.0"?>
            <svg xmlns="http://www.w3.org/2000/svg" width="44" height="20">
                <title>{statusText} badge</title>
                        
                <linearGradient id="a" x2="0" y2="100%">
                    <stop offset="0" stop-color="#bbb" stop-opacity=".1"/>
                    <stop offset="1" stop-opacity=".1"/>
                </linearGradient>
                        
                <rect x="0" rx="3" width="44" height="20" fill="{statusColor}"/>
                <rect rx="3" width="44" height="20" fill="url(#a)"/>
                        
                 <g fill="#fff" text-anchor="middle" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" font-size="13">
                    <text x="22px" y="15" fill="#010101" fill-opacity=".3">{statusText}</text>
                    <text x="22px" y="14">{statusText}</text>
                </g>
            </svg>
            """;

    final static String statusDateShaBase =
            """
            <?xml version="1.0"?>
            <svg xmlns="http://www.w3.org/2000/svg" width="195" height="20">
                <title>Status date sha badge</title>
              
                <linearGradient id="a" x2="0" y2="100%">
                    <stop offset="0" stop-color="#bbb" stop-opacity=".1"/>
                    <stop offset="1" stop-opacity=".1"/>
                </linearGradient>
              
                <rect x="0" rx="3" width="44" height="20" fill="{statusColor}"/>
                <rect x="44" rx="3" width="86" height="20" fill="#666"/>
                <rect x="130" rx="3" width="65" height="20" fill="#999"/>
              
                <g fill="#fff" text-anchor="middle" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" font-size="13">
                    <text x="22px" y="15" fill="#010101" fill-opacity=".3">{statusText}</text>
                    <text x="22px" y="14">{statusText}</text>
                      
                    <text x="88px" y="15" fill="#000" fill-opacity=".3">{date}</text>
                    <text x="88px" y="14">{date}</text>
                      
                    <text x="162" y="15" fill="#000" fill-opacity=".3">{sha}</text>
                    <text x="162" y="14">{sha}</text>
                </g>
            </svg>
            """;

    final static String trendBase =
            """
            <?xml version="1.0"?>
            <svg xmlns="http://www.w3.org/2000/svg" width="54" height="20">
                <title>Trend badge</title>
                        
                <linearGradient id="a" x2="0" y2="100%">
                    <stop offset="0" stop-color="#bbb" stop-opacity=".1"/>
                </linearGradient>
                        
                <rect x="0" rx="3" width="54" height="20" fill="#433"/>
                <g fill="#bea" text-anchor="middle" font-family="Arial" font-size="12px" font-weight="bold">
                    <text x="21" y="15">Trend</text>
                </g>
                <g text-anchor="middle" font-family="sans-serif" font-size="10px" font-weight="bold">
                    <text x="44" y="15">📈</text>
                </g>
            </svg>
            """;

}

package io.github.ericdriggs.reportcard.util.badge;

import io.github.ericdriggs.reportcard.persist.StorageType;
import io.github.ericdriggs.reportcard.util.badge.dto.RunBadgeDTO;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("TrailingWhitespacesInTextBlock")
public enum BadgeSvgHelper {

    ;//static methods only

    private static final String PATTERN_FORMAT = "yyyy-MM-dd";
    private static final DateTimeFormatter ymdFormat = DateTimeFormatter.ofPattern(PATTERN_FORMAT).withZone(ZoneOffset.UTC);


    public static String storageBadge(StorageType storageType, String label) {
        if (StorageType.JUNIT.equals(storageType)) {
            return xmlBadge(label);
        } else if (StorageType.XML.equals(storageType)) {
            return xmlBadge(label);
        }
        return htmlBadge(label);
    }

    public static String htmlBadge(String label) {
        return htmlAnchorBase.replace("{label}", label);
    }

    public static String xmlBadge(String label) {
        return xmlBase.replace("{label}", label);
    }

    public static String statusDateSha(BadgeStatus badgeStatus, Instant runDate, String sha) {
        return statusDateShaBase
                .replace("{statusColor}", badgeStatus.getColor())
                .replace("{statusText}", badgeStatus.getText())
                .replace("{date}", ymdString(runDate))
                .replace("{sha}", truncatedSha(sha)
                );
    }

    public static String status(BadgeStatus badgeStatus) {
        return statusBase
                .replace("{statusColor}", badgeStatus.getColor())
                .replace("{statusText}", badgeStatus.getText()
                );
    }

    public static String lastSuccessDateSha(int runCount, Instant runDate, String sha) {
        return statusDateShaBase
                .replace("{statusColor}", BadgeStatus.LAST_SUCCESS.getColor())
                .replace("{statusText}", Integer.toString(runCount))
                .replace("{date}", ymdString(runDate))
                .replace("{sha}", truncatedSha(sha));
    }


    public static String trend() {
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

    final static String htmlAnchorBase =
            """
                    <?xml version="1.0"?>
                    <svg xmlns="http://www.w3.org/2000/svg" width="52" height="16">
                        <title>Html badge {label}</title>
                                
                        <linearGradient id="a" x2="0" y2="100%">
                            <stop offset="0" stop-color="#bbb" stop-opacity=".1"/>
                        </linearGradient>
                                
                        <rect x="0" rx="3" width="52" height="16" fill="#ddd"/>
                                
                        <g fill="#00c" text-anchor="middle" font-family="Arial Black, sans-serif" font-size="12" font-weight="bolder">
                            <text x="21.5" y="11px">HTML</text>
                        </g>
                        <g text-anchor="middle" font-family="courier" font-size="8px" color="black">
                            <text x="46" y="9">🔗</text>
                        </g>
                        <g fill="#008" text-anchor="middle" font-family="arial" font-size="4.5px" font-weight="bold">
                            <text x="50%" y="94%" style="inline-size: 8px">{label}</text>
                        </g>
                    </svg>        
                    """;

    final static String xmlBase =
            """
                    <?xml version="1.0"?>
                    <svg xmlns="http://www.w3.org/2000/svg" width="52" height="16">
                        <title>XML badge {label}</title>                               
                        <rect x="0" rx="3" width="52" height="16" fill="#444"/>
                               
                        <g fill="#fa0" text-anchor="middle" font-family="Arial Black, sans-serif" font-size="11" font-weight="bolder">
                            <text x="15.5" y="11.5px">XML</text>
                        </g>
                           
                        <g fill="#5c5" text-anchor="middle" font-family="arial" font-size="9px" font-weight="bold">
                            <text x="77%" y="12px" style="inline-size: 8px">{label}</text>
                        </g>
                    </svg>             
                      """;

    final static String statusBase =
            """
                    <?xml version="1.0"?>
                    <svg xmlns="http://www.w3.org/2000/svg" width="34" height="16">
                        <title>{statusText} badge</title>
                                
                        <linearGradient id="a" x2="0" y2="100%">
                            <stop offset="0" stop-color="#bbb" stop-opacity=".1"/>
                            <stop offset="1" stop-opacity=".1"/>
                        </linearGradient>
                                
                        <rect x="0" rx="3" width="34" height="16" fill="{statusColor}"/>
                        <rect rx="3" width="32" height="16" fill="url(#a)"/>
                                
                        <g fill="#fff" text-anchor="middle" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" font-size="12" letter-spacing=".02em">
                            <text x="18" y="13" fill="#000" fill-opacity=".3">{statusText}</text>
                            <text x="17" y="12">{statusText}</text>
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
                    <svg xmlns="http://www.w3.org/2000/svg" width="50" height="16">
                        <title>Trend badge</title>
                                
                        <linearGradient id="a" x2="0" y2="100%">
                            <stop offset="0" stop-color="#bbb" stop-opacity=".1"/>
                        </linearGradient>
                                
                        <rect x="0" rx="3" width="50" height="16" fill="#433"/>
                        <g fill="#bea" text-anchor="middle" font-family="Arial" font-size="12px" font-weight="bold">
                            <text x="19" y="13">Trend</text>
                        </g>
                        <g text-anchor="middle" font-family="sans-serif" font-size="10px" font-weight="bold">
                            <text x="42" y="13">📈</text>
                        </g>
                    </svg>  
                    """;

}

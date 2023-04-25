package io.github.ericdriggs.reportcard.client;

import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.*;

import static io.github.ericdriggs.reportcard.client.ClientArg.getToken;

@Data
public class PostRequest {

    private ReportMetaData reportMetaData;

    private String reportCardHost;
    private String reportCardUser;
    private String reportCardPass;

    private String testReportPath;
    private String testReportRegex;
    private Map<String, String> externalLinks;
    private Map<String, String> metadata;

    public String getPostUrl()  {
        return reportCardHost + "/api/v1/reports/";
    }
    public PostRequest(ReportMetaData reportMetaData) {
        this.reportMetaData = reportMetaData;
    }

    public PostRequest(Map<ClientArg, String> argMap) {
        this(new ReportMetaData(argMap));
    }

    public void prepare() {


        if (ObjectUtils.isEmpty(testReportRegex)) {
            testReportRegex = ".*[.]xml";
        }

        //Prepare errors
        Map<String, String> validationErrors = new TreeMap<>();
        validationErrors.putAll(reportMetaData.validate());
        if (ObjectUtils.isEmpty(testReportPath)) {
            validationErrors.put("testReportPath", "missing required field");
        }

        if (!validationErrors.isEmpty()) {
            throw new BadRequestException(validationErrors);
        }
    }

    protected Map<String, String> buildExternalLinkMap(String externalLinksArg, Map<ClientArg, String> argMap) {
        if (ObjectUtils.isEmpty(externalLinksArg)) {
            return Collections.emptyMap();
        }

        String externalLinksDetokenized = replaceTokens(externalLinksArg, argMap);

        Map<String, String> linkMap = new HashMap<>();
        String[] links = externalLinksDetokenized.split(",");
        int count = 0;
        for (String link : links) {
            count++;
            String key;
            String value;
            if (!link.contains("|")) {
                key = Integer.toString(count);
                value = link;
            } else {
                int pos = link.indexOf("|");
                key = link.substring(0, pos);
                value = link.substring(pos + 1);
            }

            linkMap.put(key, value);
        }
        return linkMap;

    }

    protected String replaceTokens(String externalLinksString, Map<ClientArg, String> argMap) {
        for (ClientArg scannerArg : ClientArg.values()) {
            if (scannerArg == ClientArg.EXTERNAL_LINKS) {
                continue;
            }

            final String token = getToken(scannerArg);
            if (externalLinksString.contains(token)) {
                externalLinksString = externalLinksString.replace(token, argMap.get(scannerArg));
            }
        }
        return externalLinksString;
    }


}

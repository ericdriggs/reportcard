package com.ericdriggs.reportcard.client;

import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.*;

import static com.ericdriggs.reportcard.client.ClientArg.getToken;

@Data
public class PostRequest {

    private ReportMetaData reportMetaData;

    private String reportCardHost;
    private String reportCardUser;
    private String reportCardPass;

    private String testReportPath;
    private String testReportRegex;
    private Map<String, String> externalLinks;

    public String getPostUrl()  {
        return reportCardHost + "/api/v1/reports/";
    }
    public PostRequest(ReportMetaData reportMetaData) {
        this.reportMetaData = reportMetaData;
    }

    public PostRequest(Map<ClientArg, String> argMap) {

        ClientArg.validateRequiredArgsPresent(argMap);

        if (argMap.get(ClientArg.REPORTCARD_HOST) != null) {
            this.reportCardHost = argMap.get(ClientArg.REPORTCARD_HOST);
        }
        if (argMap.get(ClientArg.REPORTCARD_USER) != null) {
            this.reportCardUser = argMap.get(ClientArg.REPORTCARD_USER);
        }
        if (argMap.get(ClientArg.REPORTCARD_PASS) != null) {
            this.reportCardPass = argMap.get(ClientArg.REPORTCARD_PASS);
        }

        if (argMap.get(ClientArg.TEST_REPORT_PATH) != null) {
            this.testReportPath = argMap.get(ClientArg.TEST_REPORT_PATH);
        }
        if (argMap.get(ClientArg.TEST_REPORT_REGEX) != null) {
            this.testReportRegex = argMap.get(ClientArg.TEST_REPORT_REGEX);
        }

        this.externalLinks = buildExternalLinkMap(argMap.get(ClientArg.EXTERNAL_LINKS), argMap);

        { //ReportMetadata
            this.reportMetaData = new ReportMetaData();

            //SCM Metadata
            if (argMap.get(ClientArg.SCM_ORG) != null) {
                reportMetaData.setOrg(argMap.get(ClientArg.SCM_ORG));
            }

            if (argMap.get(ClientArg.SCM_REPO) != null) {
                reportMetaData.setRepo(argMap.get(ClientArg.SCM_REPO));
            }

            if (argMap.get(ClientArg.SCM_BRANCH) != null) {
                reportMetaData.setBranch(argMap.get(ClientArg.SCM_BRANCH));
            }

            if (argMap.get(ClientArg.SCM_SHA) != null) {
                reportMetaData.setSha(argMap.get(ClientArg.SCM_SHA));
            }

            { //Context Host Application Pipeline
                HostApplicationPipeline hostApplicationPipeline = new HostApplicationPipeline();
                if (argMap.get(ClientArg.CONTEXT_HOST) != null) {
                    hostApplicationPipeline.setHost(argMap.get(ClientArg.CONTEXT_HOST));
                }
                if (argMap.get(ClientArg.CONTEXT_APPLICATION) != null) {
                    hostApplicationPipeline.setApplication(argMap.get(ClientArg.CONTEXT_APPLICATION));
                }
                if (argMap.get(ClientArg.CONTEXT_PIPELINE) != null) {
                    hostApplicationPipeline.setPipeline(argMap.get(ClientArg.CONTEXT_PIPELINE));
                }
                reportMetaData.setHostApplicationPipeline(hostApplicationPipeline);
            }
            { // External links
                if (argMap.get(ClientArg.EXTERNAL_LINKS) != null) {
                    Map<String, String> externalLinks = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                    final String externalLinksString = argMap.get(ClientArg.EXTERNAL_LINKS);

                    int linkCount = 1;
                    for (String externalLink : externalLinksString.split("|")) {

                        if (externalLink.contains(",")) {
                            String[] keyValueArray = externalLink.split(",");

                            externalLinks.put(keyValueArray[0], keyValueArray[1]);
                        } else {
                            externalLinks.put(String.valueOf(linkCount), externalLink);
                        }
                        linkCount++;
                    }
                    reportMetaData.setExternalLinks(externalLinks);
                }


            }


            this.reportMetaData.setExternalExecutionId(argMap.get(ClientArg.EXECUTION_EXTERNAL_ID));
            if (ObjectUtils.isEmpty(this.reportMetaData.getExternalExecutionId())) {

                this.reportMetaData.setExternalExecutionId(UUID.randomUUID().toString());

            }

            if (argMap.get(ClientArg.STAGE) != null) {
                reportMetaData.setStage(argMap.get(ClientArg.STAGE));
            }

            if (argMap.get(ClientArg.TEST_REPORT_PATH) != null) {
                reportMetaData.setTestReportPath(argMap.get(ClientArg.TEST_REPORT_PATH));
            }

            if (argMap.get(ClientArg.TEST_REPORT_REGEX) != null) {
                reportMetaData.setTestReportRegex(argMap.get(ClientArg.TEST_REPORT_REGEX));
            }

        }
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

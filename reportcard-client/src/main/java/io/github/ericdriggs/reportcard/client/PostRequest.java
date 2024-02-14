package io.github.ericdriggs.reportcard.client;

import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Data
public class PostRequest {

    private ReportCardServerData reportCardServerData;

    private ReportMetaData reportMetaData;

    public String getPostUrl() {
        return reportCardServerData.getReportCardHost() + "/v1/api/reports/";
    }

    public PostRequest(ReportMetaData reportMetaData, ReportCardServerData reportCardServerData) {
        this.reportMetaData = reportMetaData;
        this.reportCardServerData = reportCardServerData;
        prepare();
    }

    public PostRequest(Map<ClientArg, String> argMap) {
        ClientArg.validateRequiredArgsPresent(argMap);
        this.reportCardServerData = new ReportCardServerData(argMap);
        this.reportMetaData = new ReportMetaData(argMap);
        prepare();
    }

    void prepare() {

        if (ObjectUtils.isEmpty(reportMetaData.getTestReportRegex())) {
            reportMetaData.setTestReportRegex(".*[.]xml");
        }

        if (ObjectUtils.isEmpty(this.reportMetaData.getRunReference())) {
            reportMetaData.setRunReference(UUID.randomUUID().toString());
        }

        //Prepare errors
        Map<String, String> validationErrors = new TreeMap<>();
        validationErrors.putAll(reportMetaData.validate());
        if (ObjectUtils.isEmpty(reportMetaData.getTestReportPath())) {
            validationErrors.put("testReportPath", "missing required field");
        }

        if (!validationErrors.isEmpty()) {
            throw new BadRequestException(validationErrors);
        }
    }

}

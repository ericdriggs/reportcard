package io.github.ericdriggs.reportcard.client;

import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Data
public class ReportCardServerData {

    private String reportCardHost;
    private String reportCardUser;
    private String reportCardPass;

    ReportCardServerData() {

    }

    public ReportCardServerData(
            String reportCardHost, String reportCardUser, String reportCardPass
    ) {
        this.reportCardHost = reportCardHost;
        this.reportCardUser = reportCardUser;
        this.reportCardPass = reportCardPass;
        throwIfInvalid();
    }

    public ReportCardServerData(Map<ClientArg, String> argMap) {
        this(argMap.get(ClientArg.REPORTCARD_HOST),
                argMap.get(ClientArg.REPORTCARD_USER),
                argMap.get(ClientArg.REPORTCARD_PASS));
    }

    void throwIfInvalid() {
        Map<String,String> errors = validate();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(errors.toString());
        }
    }

    public Map<String, String> validate() {

        //Prepare errors
        Map<String, String> validationErrors = new ConcurrentSkipListMap<>();
        if (ObjectUtils.isEmpty(reportCardHost)) {
            validationErrors.put("reportCardHost", "missing required field");
        }
        if (ObjectUtils.isEmpty(reportCardUser)) {
            validationErrors.put("reportCardUser", "missing required field");
        }
        if (ObjectUtils.isEmpty(reportCardPass)) {
            validationErrors.put("reportCardPass", "missing required field");
        }

        return validationErrors;
    }
}

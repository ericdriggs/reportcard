package com.ericdriggs.reportcard.scanner;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.ericdriggs.reportcard.scanner.ScannerArg.getToken;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScannerPostRequestTest {

    private Map<ScannerArg, String> getArgMap() {
        Map<ScannerArg, String> argMap = new HashMap<>();
        for (ScannerArg scannerArg : ScannerArg.values()) {
            argMap.put(scannerArg, scannerArg.name());
        }
        argMap.put(ScannerArg.EXTERNAL_LINKS,
                "https://"
                        + getToken(ScannerArg.REPORTCARD_USER) + ":"
                        + getToken(ScannerArg.REPORTCARD_PASS) + "@"
                        + getToken(ScannerArg.REPORTCARD_HOST) + "/"
                        + getToken(ScannerArg.SCM_ORG) + "/"
                        + getToken(ScannerArg.SCM_REPO) + "/"
                        + getToken(ScannerArg.SCM_BRANCH) + "/"
                        + getToken(ScannerArg.BUILD_APP) + "/"
                        + getToken(ScannerArg.BUILD_STAGE) + "/"
                        + getToken(ScannerArg.BUILD_IDENTIFIER) + "/"
                        + getToken(ScannerArg.EXTERNAL_LINKS) + "/"
        );
        return argMap;
    }


    @Test
    public void constructorTest() {
        ScannerPostRequest scannerPostRequest = new ScannerPostRequest(getArgMap());

        assertEquals(ScannerArg.SCM_ORG.name(), scannerPostRequest.getOrg());
        assertEquals(ScannerArg.SCM_REPO.name(), scannerPostRequest.getRepo());
        assertEquals(ScannerArg.SCM_BRANCH.name(), scannerPostRequest.getBranch());

        assertEquals(ScannerArg.BUILD_APP.name(), scannerPostRequest.getApp());
        assertEquals(ScannerArg.BUILD_IDENTIFIER.name(), scannerPostRequest.getBuildIdentifier());
        assertEquals(ScannerArg.BUILD_STAGE.name(), scannerPostRequest.getStage());


        assertEquals(ScannerArg.REPORTCARD_HOST.name(), scannerPostRequest.getHost());
        assertEquals(ScannerArg.REPORTCARD_USER.name(), scannerPostRequest.getUser());
        assertEquals(ScannerArg.REPORTCARD_PASS.name(), scannerPostRequest.getPass());

        assertEquals(ScannerArg.TEST_REPORT_PATH.name(), scannerPostRequest.getTestReportPath());
        assertEquals(ScannerArg.TEST_REPORT_REGEX.name(), scannerPostRequest.getTestReportRegex());

        assertEquals("https://REPORTCARD_USER:REPORTCARD_PASS@REPORTCARD_HOST/" +
                        "SCM_ORG/SCM_REPO/SCM_BRANCH/" +
                        "BUILD_APP/BUILD_STAGE/BUILD_IDENTIFIER/" +
                        "<EXTERNAL_LINKS>/",
                scannerPostRequest.getExternalLinks());

    }
}

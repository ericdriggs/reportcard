package com.ericdriggs.reportcard.scanner;

import org.junit.jupiter.api.Test;

import java.util.*;

import static com.ericdriggs.reportcard.scanner.ScannerArg.getToken;
import static org.junit.jupiter.api.Assertions.*;

public class ScannerPostRequestTest {

    private Map<ScannerArg, String> getAllArgsNoExternalLinkDescription() {
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

    private Map<ScannerArg, String> getAllArgsWithDescription() {
        Map<ScannerArg, String> argMap = new HashMap<>();
        for (ScannerArg scannerArg : ScannerArg.values()) {
            argMap.put(scannerArg, scannerArg.name());
        }
        argMap.put(ScannerArg.EXTERNAL_LINKS,
                "foo|https://"
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

    private Map<ScannerArg, String> getRequiredArgs() {
        Map<ScannerArg, String> argMap = new HashMap<>();

        argMap.put(ScannerArg.REPORTCARD_HOST, ScannerArg.REPORTCARD_HOST.name());
        argMap.put(ScannerArg.REPORTCARD_PASS, ScannerArg.REPORTCARD_PASS.name());
        argMap.put(ScannerArg.REPORTCARD_USER, ScannerArg.REPORTCARD_USER.name());

        argMap.put(ScannerArg.SCM_ORG, ScannerArg.SCM_ORG.name());
        argMap.put(ScannerArg.SCM_REPO, ScannerArg.SCM_REPO.name());
        argMap.put(ScannerArg.SCM_BRANCH, ScannerArg.SCM_BRANCH.name());

        argMap.put(ScannerArg.BUILD_STAGE, ScannerArg.BUILD_STAGE.name());

        argMap.put(ScannerArg.TEST_REPORT_PATH, ScannerArg.TEST_REPORT_PATH.name());

        return argMap;
    }


    @Test
    public void constructorAllArgsNoExternalLinkDescriptionTest() {
        ScannerPostRequest scannerPostRequest = new ScannerPostRequest(getAllArgsNoExternalLinkDescription());

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

        assertEquals(Collections.singletonMap("1", "https://REPORTCARD_USER:REPORTCARD_PASS@REPORTCARD_HOST/" +
                        "SCM_ORG/SCM_REPO/SCM_BRANCH/" +
                        "BUILD_APP/BUILD_STAGE/BUILD_IDENTIFIER/" +
                        "<EXTERNAL_LINKS>/"),
                scannerPostRequest.getExternalLinks());

    }

    @Test
    public void constructorAllArgsExternalLinkDescriptionTest() {
        ScannerPostRequest scannerPostRequest = new ScannerPostRequest(getAllArgsWithDescription());

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

        assertEquals(Collections.singletonMap("foo", "https://REPORTCARD_USER:REPORTCARD_PASS@REPORTCARD_HOST/" +
                        "SCM_ORG/SCM_REPO/SCM_BRANCH/" +
                        "BUILD_APP/BUILD_STAGE/BUILD_IDENTIFIER/" +
                        "<EXTERNAL_LINKS>/"),
                scannerPostRequest.getExternalLinks());

    }

    @Test
    public void prepareRequiredArgsTest() {
        ScannerPostRequest scannerPostRequest = new ScannerPostRequest(getRequiredArgs());
        scannerPostRequest.prepare();

        assertEquals(ScannerArg.SCM_ORG.name(), scannerPostRequest.getOrg());
        assertEquals(ScannerArg.SCM_REPO.name(), scannerPostRequest.getRepo());
        assertEquals(ScannerArg.SCM_BRANCH.name(), scannerPostRequest.getBranch());

        assertEquals(ScannerArg.SCM_REPO.name(), scannerPostRequest.getApp());
        assertEquals(36, scannerPostRequest.getBuildIdentifier().length());
        assertEquals(ScannerArg.BUILD_STAGE.name(), scannerPostRequest.getStage());

        assertEquals(ScannerArg.REPORTCARD_HOST.name(), scannerPostRequest.getHost());
        assertEquals(ScannerArg.REPORTCARD_USER.name(), scannerPostRequest.getUser());
        assertEquals(ScannerArg.REPORTCARD_PASS.name(), scannerPostRequest.getPass());

        assertEquals(ScannerArg.TEST_REPORT_PATH.name(), scannerPostRequest.getTestReportPath());
        assertNull(scannerPostRequest.getTestReportRegex());

        assertNull(scannerPostRequest.getExternalLinks());

    }


    @Test
    public void prepareNoArgsTest() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            ScannerPostRequest scannerPostRequest = new ScannerPostRequest();
            scannerPostRequest.prepare();
        });

        assertTrue(ex.getMessage().contains("400 BAD_REQUEST"));
        final Map<String, String> validationErrors = ex.getValidationErrors();
        assertEquals(5, validationErrors.size());
        final Set<String> actualValidationErrorKeys = validationErrors.keySet();

        final Set<String> expectedValidationErrorKeys =
                new HashSet<>(Arrays.asList("branch", "org", "repo", "stage", "testReportPath"));
        assertEquals(expectedValidationErrorKeys, actualValidationErrorKeys);

    }
}

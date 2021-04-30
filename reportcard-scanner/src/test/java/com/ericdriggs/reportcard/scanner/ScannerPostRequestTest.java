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
                "https://" + getDummyRelativePath());
        return argMap;
    }

    private Map<ScannerArg, String> getAllArgsWithDescription() {
        Map<ScannerArg, String> argMap = new HashMap<>();
        for (ScannerArg scannerArg : ScannerArg.values()) {
            argMap.put(scannerArg, scannerArg.name());
        }
        argMap.put(ScannerArg.EXTERNAL_LINKS,
                "foo|https://" + getDummyRelativePath());
        return argMap;
    }

    public String getDummyRelativePath() {
        return getToken(ScannerArg.REPORTCARD_USER) + ":"
                + getToken(ScannerArg.REPORTCARD_PASS) + "@"
                + getToken(ScannerArg.REPORTCARD_HOST) + "/"
                + getToken(ScannerArg.SCM_ORG) + "/"
                + getToken(ScannerArg.SCM_REPO) + "/"
                + getToken(ScannerArg.SCM_BRANCH) + "/"
                + getToken(ScannerArg.SCM_REPO) + "/"
                + getToken(ScannerArg.CONTEXT_HOST) + "/"
                + getToken(ScannerArg.CONTEXT_APPLICATION) + "/"
                + getToken(ScannerArg.CONTEXT_PIPELINE) + "/"
                + getToken(ScannerArg.EXECUTION_EXTERNAL_ID) + "/"
                + getToken(ScannerArg.STAGE) + "/"
                + getToken(ScannerArg.EXTERNAL_LINKS) + "/";
    }

    private Map<ScannerArg, String> getRequiredArgs() {
        Map<ScannerArg, String> argMap = new HashMap<>();

        argMap.put(ScannerArg.REPORTCARD_HOST, ScannerArg.REPORTCARD_HOST.name());
        argMap.put(ScannerArg.REPORTCARD_PASS, ScannerArg.REPORTCARD_PASS.name());
        argMap.put(ScannerArg.REPORTCARD_USER, ScannerArg.REPORTCARD_USER.name());

        argMap.put(ScannerArg.SCM_ORG, ScannerArg.SCM_ORG.name());
        argMap.put(ScannerArg.SCM_REPO, ScannerArg.SCM_REPO.name());
        argMap.put(ScannerArg.SCM_BRANCH, ScannerArg.SCM_BRANCH.name());

        argMap.put(ScannerArg.CONTEXT_HOST, ScannerArg.CONTEXT_HOST.name());
        argMap.put(ScannerArg.STAGE, ScannerArg.STAGE.name());

        argMap.put(ScannerArg.TEST_REPORT_PATH, ScannerArg.TEST_REPORT_PATH.name());

        return argMap;
    }

    @Test
    public void constructorNoArgsTest() {
        ScannerPostRequest scannerPostRequest = new ScannerPostRequest(new HashMap<>());

        assertNull(scannerPostRequest.getReportCardHost());
        assertNull(scannerPostRequest.getReportCardUser());
        assertNull(scannerPostRequest.getReportCardPass());

        assertNull(scannerPostRequest.getOrg());
        assertNull(scannerPostRequest.getRepo());
        assertNull(scannerPostRequest.getBranch());
        assertNull(scannerPostRequest.getSha());

        assertNull(scannerPostRequest.getContextHost());
        assertNull(scannerPostRequest.getContextApplication());
        assertNull(scannerPostRequest.getContextPipeline());

        assertNull(scannerPostRequest.getExecutionExternalId());
        assertNull(scannerPostRequest.getStage());

        assertNull(scannerPostRequest.getTestReportPath());
        assertNull(scannerPostRequest.getTestReportRegex());
        assertEquals(Collections.emptyMap(), scannerPostRequest.getExternalLinks());
    }

    @Test
    public void constructorAllArgsNoExternalLinkDescriptionTest() {
        ScannerPostRequest scannerPostRequest = new ScannerPostRequest(getAllArgsNoExternalLinkDescription());

       validateAllArgsFixture(scannerPostRequest);

        assertEquals(Collections.singletonMap("1", "https://REPORTCARD_USER:REPORTCARD_PASS@REPORTCARD_HOST/" +
                        "SCM_ORG/SCM_REPO/SCM_BRANCH/" +
                        "BUILD_APP/BUILD_STAGE/BUILD_IDENTIFIER/" +
                        "<EXTERNAL_LINKS>/"),
                scannerPostRequest.getExternalLinks());

    }

    @Test
    public void constructorAllArgsExternalLinkDescriptionTest() {
        ScannerPostRequest scannerPostRequest = new ScannerPostRequest(getAllArgsWithDescription());

        validateAllArgsFixture(scannerPostRequest);

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


        assertEquals(36, scannerPostRequest.getExecutionExternalId().length());
        assertEquals(ScannerArg.STAGE.name(), scannerPostRequest.getStage());

        assertEquals(ScannerArg.REPORTCARD_HOST.name(), scannerPostRequest.getReportCardHost());
        assertEquals(ScannerArg.REPORTCARD_USER.name(), scannerPostRequest.getReportCardUser());
        assertEquals(ScannerArg.REPORTCARD_PASS.name(), scannerPostRequest.getReportCardPass());

        assertEquals(ScannerArg.TEST_REPORT_PATH.name(), scannerPostRequest.getTestReportPath());
        assertEquals(".*[.]xml", scannerPostRequest.getTestReportRegex());
        assertEquals(Collections.emptyMap(), scannerPostRequest.getExternalLinks());

    }


    @Test
    public void prepareNoArgsTest() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            ScannerPostRequest scannerPostRequest = new ScannerPostRequest();
            scannerPostRequest.prepare();
        });

        String message = ex.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("400 BAD_REQUEST"));
        final Map<String, String> validationErrors = ex.getValidationErrors();
        assertEquals(5, validationErrors.size());
        final Set<String> actualValidationErrorKeys = validationErrors.keySet();

        final Set<String> expectedValidationErrorKeys =
                new HashSet<>(Arrays.asList("branch", "org", "repo", "stage", "testReportPath"));
        assertEquals(expectedValidationErrorKeys, actualValidationErrorKeys);

    }

    private void validateAllArgsFixture(ScannerPostRequest scannerPostRequest) {
        assertEquals(ScannerArg.REPORTCARD_HOST.name(), scannerPostRequest.getReportCardHost());
        assertEquals(ScannerArg.REPORTCARD_USER.name(), scannerPostRequest.getReportCardUser());
        assertEquals(ScannerArg.REPORTCARD_PASS.name(), scannerPostRequest.getReportCardPass());

        assertEquals(ScannerArg.SCM_ORG.name(), scannerPostRequest.getOrg());
        assertEquals(ScannerArg.SCM_REPO.name(), scannerPostRequest.getRepo());
        assertEquals(ScannerArg.SCM_BRANCH.name(), scannerPostRequest.getBranch());
        assertEquals(ScannerArg.SCM_SHA.name(), scannerPostRequest.getSha());

        assertEquals(ScannerArg.CONTEXT_HOST.name(), scannerPostRequest.getContextHost());
        assertEquals(ScannerArg.CONTEXT_APPLICATION.name(), scannerPostRequest.getContextApplication());
        assertEquals(ScannerArg.CONTEXT_PIPELINE.name(), scannerPostRequest.getContextPipeline());

        assertEquals(ScannerArg.EXECUTION_EXTERNAL_ID.name(), scannerPostRequest.getExecutionExternalId());
        assertEquals(ScannerArg.STAGE.name(), scannerPostRequest.getStage());

        assertEquals(ScannerArg.TEST_REPORT_PATH.name(), scannerPostRequest.getTestReportPath());
        assertEquals(ScannerArg.TEST_REPORT_REGEX.name(), scannerPostRequest.getTestReportRegex());
    }
}

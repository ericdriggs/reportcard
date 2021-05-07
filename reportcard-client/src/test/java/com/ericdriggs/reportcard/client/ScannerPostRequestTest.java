package com.ericdriggs.reportcard.client;

import org.junit.jupiter.api.Test;

import java.util.*;

import static com.ericdriggs.reportcard.client.ClientArg.EXTERNAL_LINKS;
import static org.junit.jupiter.api.Assertions.*;

public class ScannerPostRequestTest {


    private Map<ClientArg, String> getAllArgsNoExternalLinkDescription() {
        Map<ClientArg, String> argMap = new HashMap<>();
        for (ClientArg scannerArg : ClientArg.values()) {
            argMap.put(scannerArg, scannerArg.name());
        }
        argMap.put(ClientArg.EXTERNAL_LINKS,
                "https://" + EXTERNAL_LINKS.name());
        return argMap;
    }

    private Map<ClientArg, String> getAllArgsWithDescription() {
        Map<ClientArg, String> argMap = new HashMap<>();
        for (ClientArg scannerArg : ClientArg.values()) {
            argMap.put(scannerArg, scannerArg.name());
        }
        argMap.put(ClientArg.EXTERNAL_LINKS,
                "foo|https://" + EXTERNAL_LINKS.name());
        return argMap;
    }

    private Map<ClientArg, String> getRequiredArgs() {
        Map<ClientArg, String> argMap = new HashMap<>();

        argMap.put(ClientArg.REPORTCARD_HOST, ClientArg.REPORTCARD_HOST.name());
        argMap.put(ClientArg.REPORTCARD_PASS, ClientArg.REPORTCARD_PASS.name());
        argMap.put(ClientArg.REPORTCARD_USER, ClientArg.REPORTCARD_USER.name());

        argMap.put(ClientArg.SCM_ORG, ClientArg.SCM_ORG.name());
        argMap.put(ClientArg.SCM_REPO, ClientArg.SCM_REPO.name());
        argMap.put(ClientArg.SCM_BRANCH, ClientArg.SCM_BRANCH.name());

        argMap.put(ClientArg.CONTEXT_HOST, ClientArg.CONTEXT_HOST.name());
        argMap.put(ClientArg.STAGE, ClientArg.STAGE.name());

        argMap.put(ClientArg.TEST_REPORT_PATH, ClientArg.TEST_REPORT_PATH.name());

        return argMap;
    }

    @Test
    public void constructorNoArgsTest() {
        PostRequest scannerPostRequest = new PostRequest(new HashMap<>());

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
        PostRequest scannerPostRequest = new PostRequest(getAllArgsNoExternalLinkDescription());

       validateAllArgsFixture(scannerPostRequest);

        assertEquals(Collections.singletonMap("1", "https://EXTERNAL_LINKS"),
                scannerPostRequest.getExternalLinks());

    }

    @Test
    public void constructorAllArgsExternalLinkDescriptionTest() {
        PostRequest scannerPostRequest = new PostRequest(getAllArgsWithDescription());

        validateAllArgsFixture(scannerPostRequest);

        assertEquals(Collections.singletonMap("foo","https://EXTERNAL_LINKS"),
                scannerPostRequest.getExternalLinks());

    }

    @Test
    public void prepareRequiredArgsTest() {
        PostRequest scannerPostRequest = new PostRequest(getRequiredArgs());
        scannerPostRequest.prepare();

        assertEquals(ClientArg.SCM_ORG.name(), scannerPostRequest.getOrg());
        assertEquals(ClientArg.SCM_REPO.name(), scannerPostRequest.getRepo());
        assertEquals(ClientArg.SCM_BRANCH.name(), scannerPostRequest.getBranch());


        assertEquals(36, scannerPostRequest.getExecutionExternalId().length());
        assertEquals(ClientArg.STAGE.name(), scannerPostRequest.getStage());

        assertEquals(ClientArg.REPORTCARD_HOST.name(), scannerPostRequest.getReportCardHost());
        assertEquals(ClientArg.REPORTCARD_USER.name(), scannerPostRequest.getReportCardUser());
        assertEquals(ClientArg.REPORTCARD_PASS.name(), scannerPostRequest.getReportCardPass());

        assertEquals(ClientArg.TEST_REPORT_PATH.name(), scannerPostRequest.getTestReportPath());
        assertEquals(".*[.]xml", scannerPostRequest.getTestReportRegex());
        assertEquals(Collections.emptyMap(), scannerPostRequest.getExternalLinks());

    }


    @Test
    public void prepareNoArgsTest() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            PostRequest scannerPostRequest = new PostRequest();
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

    private void validateAllArgsFixture(PostRequest scannerPostRequest) {
        assertEquals(ClientArg.REPORTCARD_HOST.name(), scannerPostRequest.getReportCardHost());
        assertEquals(ClientArg.REPORTCARD_USER.name(), scannerPostRequest.getReportCardUser());
        assertEquals(ClientArg.REPORTCARD_PASS.name(), scannerPostRequest.getReportCardPass());

        assertEquals(ClientArg.SCM_ORG.name(), scannerPostRequest.getOrg());
        assertEquals(ClientArg.SCM_REPO.name(), scannerPostRequest.getRepo());
        assertEquals(ClientArg.SCM_BRANCH.name(), scannerPostRequest.getBranch());
        assertEquals(ClientArg.SCM_SHA.name(), scannerPostRequest.getSha());

        assertEquals(ClientArg.CONTEXT_HOST.name(), scannerPostRequest.getContextHost());
        assertEquals(ClientArg.CONTEXT_APPLICATION.name(), scannerPostRequest.getContextApplication());
        assertEquals(ClientArg.CONTEXT_PIPELINE.name(), scannerPostRequest.getContextPipeline());

        assertEquals(ClientArg.EXECUTION_EXTERNAL_ID.name(), scannerPostRequest.getExecutionExternalId());
        assertEquals(ClientArg.STAGE.name(), scannerPostRequest.getStage());

        assertEquals(ClientArg.TEST_REPORT_PATH.name(), scannerPostRequest.getTestReportPath());
        assertEquals(ClientArg.TEST_REPORT_REGEX.name(), scannerPostRequest.getTestReportRegex());
    }
}

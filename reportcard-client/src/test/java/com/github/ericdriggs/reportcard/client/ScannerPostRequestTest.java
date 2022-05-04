package com.github.ericdriggs.reportcard.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.github.ericdriggs.reportcard.client.ClientArg.EXTERNAL_LINKS;
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
        argMap.put(ClientArg.SCM_SHA, ClientArg.SCM_SHA.name());

        argMap.put(ClientArg.CONTEXT_HOST, ClientArg.CONTEXT_HOST.name());
        argMap.put(ClientArg.STAGE, ClientArg.STAGE.name());

        argMap.put(ClientArg.TEST_REPORT_PATH, ClientArg.TEST_REPORT_PATH.name());

        return argMap;
    }


    @Test
    public void constructorNoArgsTest() {
        BadRequestException badRequestException = Assertions.assertThrows(BadRequestException.class, () -> {
            new PostRequest(new HashMap<>());
        });

        Map<String,String> validationErrors = badRequestException.getValidationErrors();
        final Set<String> actualValidationErrorKeys = validationErrors.keySet();

        final Set<String> expectedValidationErrorKeys =
                new TreeSet<>(Arrays.asList("CONTEXT_HOST", "REPORTCARD_HOST", "REPORTCARD_PASS", "REPORTCARD_USER", "SCM_BRANCH", "SCM_ORG", "SCM_REPO", "SCM_SHA", "STAGE", "TEST_REPORT_PATH"));
        assertEquals(expectedValidationErrorKeys, actualValidationErrorKeys);
    }

    @Test
    public void constructorAllArgsNoExternalLinkDescriptionTest() {
        Map<ClientArg, String> clientArgMap = getAllArgsNoExternalLinkDescription();
        PostRequest scannerPostRequest = new PostRequest(clientArgMap);

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
        Map<ClientArg, String> clientArgMap = getRequiredArgs();
        PostRequest scannerPostRequest = new PostRequest(clientArgMap);
        scannerPostRequest.prepare();

        ReportMetaData reportMetaData = scannerPostRequest.getReportMetaData();

        HostApplicationPipeline hostApplicationPipeline = reportMetaData.getHostApplicationPipeline();
        assertEquals(ClientArg.CONTEXT_HOST.name(), hostApplicationPipeline.getHost());
        assertNull(hostApplicationPipeline.getApplication());
        assertNull(hostApplicationPipeline.getPipeline());

        assertEquals(ClientArg.SCM_ORG.name(), reportMetaData.getOrg());
        assertEquals(ClientArg.SCM_REPO.name(), reportMetaData.getRepo());
        assertEquals(ClientArg.SCM_BRANCH.name(), reportMetaData.getBranch());
        assertEquals(ClientArg.SCM_SHA.name(), reportMetaData.getSha());

        assertEquals(36, reportMetaData.getExternalExecutionId().length());
        assertEquals(ClientArg.STAGE.name(), reportMetaData.getStage());

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
            PostRequest scannerPostRequest = new PostRequest(new ReportMetaData());
            scannerPostRequest.prepare();
        });

        String message = ex.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("400 BAD_REQUEST"));
        final Map<String, String> validationErrors = ex.getValidationErrors();
        assertEquals(7, validationErrors.size());
        final Set<String> actualValidationErrorKeys = validationErrors.keySet();

        final Set<String> expectedValidationErrorKeys =
                new TreeSet<>(Arrays.asList("branch", "org", "repo", "sha", "stage", "testReportPath", "hostApplicationPipeline.getHost()"));
        assertEquals(expectedValidationErrorKeys, actualValidationErrorKeys);

    }

    private void validateAllArgsFixture(PostRequest scannerPostRequest) {
        assertEquals(ClientArg.REPORTCARD_HOST.name(), scannerPostRequest.getReportCardHost());
        assertEquals(ClientArg.REPORTCARD_USER.name(), scannerPostRequest.getReportCardUser());
        assertEquals(ClientArg.REPORTCARD_PASS.name(), scannerPostRequest.getReportCardPass());

        ReportMetaData reportMetaData = scannerPostRequest.getReportMetaData();
        assertNotNull(reportMetaData);
        assertEquals(ClientArg.SCM_ORG.name(), reportMetaData.getOrg());
        assertEquals(ClientArg.SCM_REPO.name(), reportMetaData.getRepo());
        assertEquals(ClientArg.SCM_BRANCH.name(), reportMetaData.getBranch());
        assertEquals(ClientArg.SCM_SHA.name(), reportMetaData.getSha());

        HostApplicationPipeline hostApplicationPipeline = reportMetaData.getHostApplicationPipeline();
        assertEquals(ClientArg.CONTEXT_HOST.name(), hostApplicationPipeline.getHost());
        assertEquals(ClientArg.CONTEXT_APPLICATION.name(), hostApplicationPipeline.getApplication());
        assertEquals(ClientArg.CONTEXT_PIPELINE.name(), hostApplicationPipeline.getPipeline());

        assertEquals(ClientArg.EXECUTION_EXTERNAL_ID.name(), reportMetaData.getExternalExecutionId());
        assertEquals(ClientArg.STAGE.name(), reportMetaData.getStage());

        assertEquals(ClientArg.TEST_REPORT_PATH.name(), scannerPostRequest.getTestReportPath());
        assertEquals(ClientArg.TEST_REPORT_REGEX.name(), scannerPostRequest.getTestReportRegex());
    }
}

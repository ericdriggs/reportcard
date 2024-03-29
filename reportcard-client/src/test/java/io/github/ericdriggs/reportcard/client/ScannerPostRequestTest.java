package io.github.ericdriggs.reportcard.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static io.github.ericdriggs.reportcard.client.ClientArg.EXTERNAL_LINKS;
import static io.github.ericdriggs.reportcard.client.ClientArg.METADATA;
import static org.junit.jupiter.api.Assertions.*;

public class ScannerPostRequestTest {


    final String hostMetadata = "{ \"host\": \"http://www.foojenkins.com\" }";


    private Map<ClientArg, String> getAllArgsWithDescription() {
        Map<ClientArg, String> argMap = new HashMap<>();
        for (ClientArg scannerArg : ClientArg.values()) {
            argMap.put(scannerArg, scannerArg.name());
        }
        argMap.put(EXTERNAL_LINKS,
                "{\"foo\":\"https://EXTERNAL_LINKS\"}");
        argMap.put(METADATA, hostMetadata);
        return argMap;
    }

    private Map<ClientArg, String> getRequiredArgs() {
        Map<ClientArg, String> argMap = new HashMap<>();

        argMap.put(ClientArg.REPORTCARD_HOST, ClientArg.REPORTCARD_HOST.name());
        argMap.put(ClientArg.REPORTCARD_PASS, ClientArg.REPORTCARD_PASS.name());
        argMap.put(ClientArg.REPORTCARD_USER, ClientArg.REPORTCARD_USER.name());

        argMap.put(ClientArg.SCM_COMPANY, ClientArg.SCM_COMPANY.name());
        argMap.put(ClientArg.SCM_ORG, ClientArg.SCM_ORG.name());
        argMap.put(ClientArg.SCM_REPO, ClientArg.SCM_REPO.name());
        argMap.put(ClientArg.SCM_BRANCH, ClientArg.SCM_BRANCH.name());
        argMap.put(ClientArg.SCM_SHA, ClientArg.SCM_SHA.name());

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
                new TreeSet<>(Arrays.asList("REPORTCARD_HOST", "REPORTCARD_PASS", "REPORTCARD_USER", "SCM_BRANCH", "SCM_COMPANY", "SCM_ORG", "SCM_REPO", "SCM_SHA", "STAGE", "TEST_REPORT_PATH"));
        assertEquals(expectedValidationErrorKeys, actualValidationErrorKeys);
    }

    @Test
    public void constructorAllArgsExternalLinkDescriptionTest() {
        PostRequest scannerPostRequest = new PostRequest(getAllArgsWithDescription());

        validateAllArgsFixture(scannerPostRequest);

        assertEquals(Collections.singletonMap("foo","https://EXTERNAL_LINKS"),
                scannerPostRequest.getReportMetaData().getExternalLinks());

    }

    @Test
    public void prepareRequiredArgsTest() {
        Map<ClientArg, String> clientArgMap = getRequiredArgs();
        PostRequest scannerPostRequest = new PostRequest(clientArgMap);
        scannerPostRequest.prepare();

        ReportMetaData reportMetaData = scannerPostRequest.getReportMetaData();

        assertEquals(ClientArg.SCM_COMPANY.name(), reportMetaData.getCompany());
        assertEquals(ClientArg.SCM_ORG.name(), reportMetaData.getOrg());
        assertEquals(ClientArg.SCM_REPO.name(), reportMetaData.getRepo());
        assertEquals(ClientArg.SCM_BRANCH.name(), reportMetaData.getBranch());
        assertEquals(ClientArg.SCM_SHA.name(), reportMetaData.getSha());

        assertEquals(36, reportMetaData.getRunReference().length());
        assertEquals(ClientArg.STAGE.name(), reportMetaData.getStage());

        assertEquals(ClientArg.REPORTCARD_HOST.name(), scannerPostRequest.getReportCardServerData().getReportCardHost());
        assertEquals(ClientArg.REPORTCARD_USER.name(), scannerPostRequest.getReportCardServerData().getReportCardUser());
        assertEquals(ClientArg.REPORTCARD_PASS.name(), scannerPostRequest.getReportCardServerData().getReportCardPass());

        assertEquals(ClientArg.TEST_REPORT_PATH.name(), scannerPostRequest.getReportMetaData().getTestReportPath());
        assertEquals(".*[.]xml", scannerPostRequest.getReportMetaData().getTestReportRegex());
        assertEquals(Collections.emptyMap(), scannerPostRequest.getReportMetaData().getExternalLinks());

    }


    @Test
    public void prepareNoArgsTest() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            PostRequest scannerPostRequest = new PostRequest(new ReportMetaData(), new ReportCardServerData());
            scannerPostRequest.prepare();
        });

        String message = ex.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("400 BAD_REQUEST"));
        final Map<String, String> validationErrors = ex.getValidationErrors();
        assertEquals(7, validationErrors.size());
        final Set<String> actualValidationErrorKeys = validationErrors.keySet();

        final Set<String> expectedValidationErrorKeys =
                new TreeSet<>(Arrays.asList("branch", "company", "org", "repo", "sha", "stage", "testReportPath"));
        assertEquals(expectedValidationErrorKeys, actualValidationErrorKeys);

    }

    private void validateAllArgsFixture(PostRequest scannerPostRequest) {
        assertEquals(ClientArg.REPORTCARD_HOST.name(), scannerPostRequest.getReportCardServerData().getReportCardHost());
        assertEquals(ClientArg.REPORTCARD_USER.name(), scannerPostRequest.getReportCardServerData().getReportCardUser());
        assertEquals(ClientArg.REPORTCARD_PASS.name(), scannerPostRequest.getReportCardServerData().getReportCardPass());

        ReportMetaData reportMetaData = scannerPostRequest.getReportMetaData();
        assertNotNull(reportMetaData);
        assertEquals(ClientArg.SCM_COMPANY.name(), reportMetaData.getCompany());
        assertEquals(ClientArg.SCM_ORG.name(), reportMetaData.getOrg());
        assertEquals(ClientArg.SCM_REPO.name(), reportMetaData.getRepo());
        assertEquals(ClientArg.SCM_BRANCH.name(), reportMetaData.getBranch());
        assertEquals(ClientArg.SCM_SHA.name(), reportMetaData.getSha());

        assertEquals("{ \"host\": \"http://www.foojenkins.com\" }", hostMetadata);

        assertEquals(ClientArg.RUN_REFERENCE.name(), reportMetaData.getRunReference());
        assertEquals(ClientArg.STAGE.name(), reportMetaData.getStage());

        assertEquals(ClientArg.TEST_REPORT_PATH.name(), scannerPostRequest.getReportMetaData().getTestReportPath());
        assertEquals(ClientArg.TEST_REPORT_REGEX.name(), scannerPostRequest.getReportMetaData().getTestReportRegex());
    }
}

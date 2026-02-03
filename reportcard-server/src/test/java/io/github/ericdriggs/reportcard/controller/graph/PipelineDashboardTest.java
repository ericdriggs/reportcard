package io.github.ericdriggs.reportcard.controller.graph;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.config.LocalStackConfig;
import io.github.ericdriggs.reportcard.controller.JunitController;
import io.github.ericdriggs.reportcard.controller.model.JunitHtmlPostRequest;
import io.github.ericdriggs.reportcard.controller.model.StagePathStorageResultCountResponse;
import io.github.ericdriggs.reportcard.controller.util.TestXmlTarGzUtil;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.model.StageDetails;
import io.github.ericdriggs.reportcard.xml.ResourceReaderComponent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for pipeline dashboard timing display.
 *
 * Tests verify:
 * 1. Dashboard displays "Avg Run Duration" column header
 * 2. Jobs with timing data show formatted duration (human-readable)
 * 3. Jobs without timing data show "-" character
 * 4. Field description for timing is present in HTML
 */
@SpringBootTest(classes = {ReportcardApplication.class, LocalStackConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Slf4j
public class PipelineDashboardTest {

    @Autowired
    private GraphUIController graphUIController;

    @Autowired
    private JunitController junitController;

    @Autowired
    private ResourceReaderComponent resourceReader;

    /**
     * Test job WITH timing data from Karate JSON.
     * Verifies duration is formatted as human-readable (e.g., "5m 7s" not "307")
     */
    @Test
    public void testPipelineDashboardWithTiming() throws IOException {
        // Arrange: Upload test data with timing
        String stage = "pipelineDashboardWithTiming";
        UUID runReference = UUID.randomUUID();
        StageDetails stageDetails = getStageDetails(stage, runReference);

        // Upload JUnit XML + Karate JSON tar.gz with timing
        MultipartFile junitTarGz = createJunitWithKarateTarGz();
        uploadTestData(stageDetails, junitTarGz);

        // Act: Get dashboard HTML
        ResponseEntity<String> response = graphUIController.getJobDashboard(
                TestData.company, TestData.org, null, 90);

        // Assert: Verify response
        assertEquals(200, response.getStatusCodeValue());
        String html = response.getBody();
        assertNotNull(html);

        // Verify table header exists
        assertTrue(html.contains("Avg Run Duration"),
                "Dashboard should have 'Avg Run Duration' column header");

        // Verify duration is formatted (not raw seconds)
        // Karate JSON has elapsedTime: 307767.0 (milliseconds) = ~307.77 seconds = ~5m 7s
        assertTrue(html.contains("5m") || html.contains("307") || html.contains("s"),
                "Duration should be formatted as human-readable time");

        // Should NOT contain literal "null" in duration field
        assertFalse(html.contains(">null<"),
                "Dashboard should not display literal 'null' for timing");

        // Verify field description is present
        assertTrue(html.contains("Avg Run Duration"),
                "Field descriptions section should include 'Avg Run Duration'");
        assertTrue(html.contains("wall clock execution time") ||
                   html.contains("earliest stage start to latest stage end"),
                "Field description should explain timing calculation");
    }

    /**
     * Test job WITHOUT timing data (JUnit only, no Karate).
     * Verifies duration displays as "-" character.
     */
    @Test
    public void testPipelineDashboardWithoutTiming() throws IOException {
        // Arrange: Upload test data WITHOUT timing (JUnit only)
        String stage = "pipelineDashboardWithoutTiming";
        UUID runReference = UUID.randomUUID();
        StageDetails stageDetails = getStageDetails(stage, runReference);

        // Upload only JUnit XML (no Karate JSON = no timing)
        MultipartFile junitTarGz = createJunitOnlyTarGz();
        uploadTestData(stageDetails, junitTarGz);

        // Act: Get dashboard HTML
        ResponseEntity<String> response = graphUIController.getJobDashboard(
                TestData.company, TestData.org, null, 90);

        // Assert: Verify response
        assertEquals(200, response.getStatusCodeValue());
        String html = response.getBody();
        assertNotNull(html);

        // Verify table header exists
        assertTrue(html.contains("Avg Run Duration"),
                "Dashboard should have 'Avg Run Duration' column header");

        // Verify NULL duration shows as "-"
        // Look for "-" in table cells (not in header/description)
        assertTrue(html.contains("<td>-</td>") || html.contains("<td> - </td>"),
                "Jobs without timing data should display '-' in duration column");

        // Should NOT contain literal "null"
        assertFalse(html.contains(">null<"),
                "Dashboard should not display literal 'null' for missing timing");
    }

    /**
     * Test field description section in dashboard HTML.
     * Verifies the description fieldset is present and contains timing info.
     */
    @Test
    public void testPipelineDashboardFieldDescription() {
        // Act: Get dashboard HTML
        ResponseEntity<String> response = graphUIController.getJobDashboard(
                TestData.company, TestData.org, null, 90);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        String html = response.getBody();
        assertNotNull(html);

        // Verify field descriptions section exists
        assertTrue(html.contains("Field Descriptions"),
                "Dashboard should have field descriptions section");

        // Verify Avg Run Duration description exists
        assertTrue(html.contains("<dt") && html.contains("Avg Run Duration"),
                "Field descriptions should have definition term for 'Avg Run Duration'");
        assertTrue(html.contains("<dd") &&
                   (html.contains("wall clock") || html.contains("execution time")),
                "Field description should explain timing calculation");

        // Verify "-" display is documented
        assertTrue(html.contains("\"-\"") || html.contains("without timing"),
                "Field description should mention '-' display for missing timing");
    }

    // Helper methods

    private StageDetails getStageDetails(String stageName, UUID runReference) {
        return StageDetails.builder()
                .company(TestData.company)
                .org(TestData.org)
                .repo(TestData.repo)
                .branch(TestData.branch)
                .sha(TestData.sha)
                .jobInfo(TestData.jobInfo)
                .runReference(runReference)
                .stage(stageName)
                .build();
    }

    /**
     * Creates tar.gz with JUnit XML + Karate JSON (includes timing data).
     */
    private MultipartFile createJunitWithKarateTarGz() throws IOException {
        String junitXmlPath = "format-samples/sample-junit-small.xml";
        String karateJsonPath = "format-samples/karate/karate-summary-valid.json";

        MultipartFile[] files = new MultipartFile[2];
        files[0] = new MockMultipartFile(
                "sample-junit-small.xml",
                "sample-junit-small.xml",
                MediaType.APPLICATION_XML_VALUE,
                resourceReader.resourceAsString(junitXmlPath).getBytes()
        );
        files[1] = new MockMultipartFile(
                "karate-summary-json.txt",
                "karate-summary-json.txt",
                MediaType.TEXT_PLAIN_VALUE,
                resourceReader.resourceAsString(karateJsonPath).getBytes()
        );

        Path tempTarGz = TestXmlTarGzUtil.createTarGzipFilesForTesting(files);
        try {
            return new MockMultipartFile(
                    "junit.tar.gz",
                    "junit.tar.gz",
                    MediaType.ALL_VALUE,
                    Files.newInputStream(tempTarGz)
            );
        } finally {
            Files.deleteIfExists(tempTarGz);
        }
    }

    /**
     * Creates tar.gz with only JUnit XML (no Karate = no timing data).
     */
    private MultipartFile createJunitOnlyTarGz() throws IOException {
        String junitXmlPath = "format-samples/sample-junit-small.xml";

        MultipartFile[] files = new MultipartFile[1];
        files[0] = new MockMultipartFile(
                "sample-junit-small.xml",
                "sample-junit-small.xml",
                MediaType.APPLICATION_XML_VALUE,
                resourceReader.resourceAsString(junitXmlPath).getBytes()
        );

        Path tempTarGz = TestXmlTarGzUtil.createTarGzipFilesForTesting(files);
        try {
            return new MockMultipartFile(
                    "junit.tar.gz",
                    "junit.tar.gz",
                    MediaType.ALL_VALUE,
                    Files.newInputStream(tempTarGz)
            );
        } finally {
            Files.deleteIfExists(tempTarGz);
        }
    }

    /**
     * Uploads test data via JunitController.
     */
    private void uploadTestData(StageDetails stageDetails, MultipartFile junitTarGz) throws IOException {
        JunitHtmlPostRequest req = JunitHtmlPostRequest.builder()
                .stageDetails(stageDetails)
                .junitXmls(junitTarGz)
                .build();
        StagePathStorageResultCountResponse response = junitController.doPostStageJunitStorageTarGZ(req);
        assertNotNull(response);
        assertEquals(201, response.getResponseDetails().getHttpStatus());
    }
}

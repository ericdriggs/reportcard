package io.github.ericdriggs.reportcard.controller.util;

import io.github.ericdriggs.reportcard.util.tar.TarCompressor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KarateTarGzUtilTest {

    @TempDir
    Path tempDir;

    @Test
    void extractKarateSummaryJson_nullInput_returnsNull() {
        String result = KarateTarGzUtil.extractKarateSummaryJson((MultipartFile) null);
        assertNull(result);
    }

    @Test
    void extractKarateSummaryJson_emptyFile_returnsNull() {
        MultipartFile emptyFile = new MockMultipartFile(
                "file",
                "karate.tar.gz",
                "application/gzip",
                new byte[0]
        );
        String result = KarateTarGzUtil.extractKarateSummaryJson(emptyFile);
        assertNull(result);
    }

    @Test
    void extractKarateSummaryJson_validTarGzWithKarateSummary_returnsContent() throws Exception {
        // Create a karate-summary-json.txt file with test content
        String expectedContent = "{\"featuresPassed\":10,\"featuresFailed\":0}";
        Path karateSummaryFile = tempDir.resolve("karate-summary-json.txt");
        Files.writeString(karateSummaryFile, expectedContent);

        // Create tar.gz containing the file
        Path tarGzPath = Files.createTempFile(tempDir, "karate-", ".tar.gz");
        TarCompressor.createTarGzipFiles(List.of(karateSummaryFile), tarGzPath);

        // Read tar.gz bytes and create MultipartFile
        byte[] tarGzBytes = Files.readAllBytes(tarGzPath);
        MultipartFile tarGzFile = new MockMultipartFile(
                "karateTarGz",
                "karate-reports.tar.gz",
                "application/gzip",
                tarGzBytes
        );

        // Extract and verify
        String result = KarateTarGzUtil.extractKarateSummaryJson(tarGzFile);
        assertEquals(expectedContent, result);
    }

    @Test
    void extractKarateSummaryJson_tarGzWithoutKarateSummary_returnsNull() throws Exception {
        // Create a different file (not karate-summary-json.txt)
        Path otherFile = tempDir.resolve("other-file.txt");
        Files.writeString(otherFile, "some other content");

        // Create tar.gz containing only the other file
        Path tarGzPath = Files.createTempFile(tempDir, "karate-", ".tar.gz");
        TarCompressor.createTarGzipFiles(List.of(otherFile), tarGzPath);

        // Read tar.gz bytes and create MultipartFile
        byte[] tarGzBytes = Files.readAllBytes(tarGzPath);
        MultipartFile tarGzFile = new MockMultipartFile(
                "karateTarGz",
                "karate-reports.tar.gz",
                "application/gzip",
                tarGzBytes
        );

        // Extract and verify returns null when file not found
        String result = KarateTarGzUtil.extractKarateSummaryJson(tarGzFile);
        assertNull(result);
    }

    @Test
    void extractKarateNativeJsons_returnsOnlyNativeFiles() throws Exception {
        Path tempDir = Files.createTempDirectory("karate-native-extract-");
        try {
            Files.writeString(tempDir.resolve("karate-summary-json.txt"), "{\"version\":\"1.2.0\"}");
            Files.writeString(tempDir.resolve("feature-a.json"), "[]");
            Files.writeString(tempDir.resolve("feature-a.karate-json.txt"), "{\"packageQualifiedName\":\"feature-a\"}");
            Files.writeString(tempDir.resolve("feature-b.karate-json.txt"), "{\"packageQualifiedName\":\"feature-b\"}");

            Path tarGz = TestXmlTarGzUtil.createTarGzipFilesForTesting(List.of(
                    tempDir.resolve("karate-summary-json.txt"),
                    tempDir.resolve("feature-a.json"),
                    tempDir.resolve("feature-a.karate-json.txt"),
                    tempDir.resolve("feature-b.karate-json.txt")));
            byte[] bytes = Files.readAllBytes(tarGz);
            Files.delete(tarGz);
            MockMultipartFile multipart = new MockMultipartFile("karate.tar.gz", bytes);

            List<String> natives = KarateTarGzUtil.extractKarateNativeJsons(multipart);
            assertEquals(2, natives.size(), "exactly the two *.karate-json.txt files");
            assertTrue(natives.stream().anyMatch(s -> s.contains("\"feature-a\"")));
            assertTrue(natives.stream().anyMatch(s -> s.contains("\"feature-b\"")));
        } finally {
            org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory(tempDir.toFile());
        }
    }

    @Test
    void extractCucumberJson_oneMalformedFileAmongValidOnes_mergesOnlyValidFiles() throws Exception {
        Path tempDir = Files.createTempDirectory("karate-cucumber-extract-");
        try {
            String validFeatureA = "[{\"keyword\":\"Feature\",\"name\":\"feature-a\",\"elements\":[]}]";
            String validFeatureB = "[{\"keyword\":\"Feature\",\"name\":\"feature-b\",\"elements\":[]}]";
            String malformed = "not valid json at all {{{";

            Files.writeString(tempDir.resolve("feature-a.json"), validFeatureA);
            Files.writeString(tempDir.resolve("feature-b.json"), validFeatureB);
            Files.writeString(tempDir.resolve("feature-c.json"), malformed);

            Path tarGz = TestXmlTarGzUtil.createTarGzipFilesForTesting(List.of(
                    tempDir.resolve("feature-a.json"),
                    tempDir.resolve("feature-b.json"),
                    tempDir.resolve("feature-c.json")));
            byte[] bytes = Files.readAllBytes(tarGz);
            Files.delete(tarGz);
            MockMultipartFile multipart = new MockMultipartFile("karate.tar.gz", bytes);

            String merged = KarateTarGzUtil.extractCucumberJson(multipart);

            assertNotNull(merged, "valid files' data must survive even though one file among them is malformed");
            assertTrue(merged.contains("feature-a"), "merged: " + merged);
            assertTrue(merged.contains("feature-b"), "merged: " + merged);
        } finally {
            org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory(tempDir.toFile());
        }
    }

    @Test
    void extractKarateSummaryJson_fromInputStream() throws Exception {
        Path tempDir = Files.createTempDirectory("karate-summary-stream-");
        try {
            Files.writeString(tempDir.resolve("karate-summary-json.txt"), "{\"version\":\"1.2.0\"}");
            Path tarGz = TestXmlTarGzUtil.createTarGzipFilesForTesting(
                    List.of(tempDir.resolve("karate-summary-json.txt")));
            byte[] bytes = Files.readAllBytes(tarGz);
            Files.delete(tarGz);

            String summary = KarateTarGzUtil.extractKarateSummaryJson(new ByteArrayInputStream(bytes));
            assertNotNull(summary);
            assertTrue(summary.contains("1.2.0"));
        } finally {
            org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory(tempDir.toFile());
        }
    }
}

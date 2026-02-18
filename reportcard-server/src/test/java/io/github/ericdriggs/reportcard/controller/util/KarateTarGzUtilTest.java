package io.github.ericdriggs.reportcard.controller.util;

import io.github.ericdriggs.reportcard.util.tar.TarCompressor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KarateTarGzUtilTest {

    @TempDir
    Path tempDir;

    @Test
    void extractKarateSummaryJson_nullInput_returnsNull() {
        String result = KarateTarGzUtil.extractKarateSummaryJson(null);
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
}

package io.github.ericdriggs.reportcard.client;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostWebClientTest {

    private static List<String> tarEntryNames(Path tarGz) throws Exception {
        List<String> names = new ArrayList<>();
        try (InputStream fis = Files.newInputStream(tarGz);
             TarArchiveInputStream tis = new TarArchiveInputStream(
                     new GzipCompressorInputStream(new BufferedInputStream(fis)))) {
            TarArchiveEntry entry;
            while ((entry = tis.getNextTarEntry()) != null) {
                names.add(entry.getName());
            }
        }
        return names;
    }

    @Test
    void buildKarateTarGz_nullOrEmptyPath_returnsNull() {
        assertNull(PostWebClient.INSTANCE.buildKarateTarGz(null));
        assertNull(PostWebClient.INSTANCE.buildKarateTarGz(""));
    }

    @Test
    void buildKarateTarGz_noMatchingFiles_returnsNull() throws Exception {
        Path dir = Files.createTempDirectory("karate-postwebclient-empty-");
        try {
            Files.writeString(dir.resolve("notes.txt"), "no karate files here");
            assertNull(PostWebClient.INSTANCE.buildKarateTarGz(dir.toString()));
        } finally {
            org.apache.commons.io.FileUtils.deleteDirectory(dir.toFile());
        }
    }

    @Test
    void buildKarateTarGz_includesNativeAndSummaryFiles() throws Exception {
        Path dir = Files.createTempDirectory("karate-postwebclient-");
        try {
            Files.writeString(dir.resolve("feature-a.json"), "[]");
            Files.writeString(dir.resolve("feature-a.karate-json.txt"), "{}");
            Files.writeString(dir.resolve("karate-summary-json.txt"), "{}");
            Files.writeString(dir.resolve("feature-a.html"), "<html/>");

            Path karateTarGz = PostWebClient.INSTANCE.buildKarateTarGz(dir.toString());
            assertNotNull(karateTarGz, "karate tar.gz must be built when matching files exist");

            List<String> names = tarEntryNames(karateTarGz);
            assertTrue(names.contains("feature-a.json"), names.toString());
            assertTrue(names.contains("feature-a.karate-json.txt"), "native karate json must be included: " + names);
            assertTrue(names.contains("karate-summary-json.txt"), "summary must be included: " + names);
            assertFalse(names.contains("feature-a.html"), names.toString());

            Files.deleteIfExists(karateTarGz);
        } finally {
            org.apache.commons.io.FileUtils.deleteDirectory(dir.toFile());
        }
    }
}

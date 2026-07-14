package io.github.ericdriggs.reportcard.client.util;

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

class TarGzUtilTest {

    static List<String> tarEntryNames(Path tarGz) throws Exception {
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
    void karateReportRegex_includesNativeAndSummary_excludesOthers() throws Exception {
        Path dir = Files.createTempDirectory("karate-report-regex-");
        try {
            Files.writeString(dir.resolve("feature-a.json"), "[]");
            Files.writeString(dir.resolve("feature-a.karate-json.txt"), "{}");
            Files.writeString(dir.resolve("karate-summary-json.txt"), "{}");
            Files.writeString(dir.resolve("karate-progress-json.txt"), "{}");
            Files.writeString(dir.resolve("feature-a.html"), "<html/>");
            Files.writeString(dir.resolve("karate.log"), "log");

            Path tarGz = TarGzUtil.createTarGzFromDirectory(dir, TarGzUtil.KARATE_REPORT_FILE_REGEX);
            List<String> names = tarEntryNames(tarGz);

            assertTrue(names.contains("feature-a.json"), names.toString());
            assertTrue(names.contains("feature-a.karate-json.txt"), "native karate json must be published: " + names);
            assertTrue(names.contains("karate-summary-json.txt"), "summary must be published: " + names);
            assertTrue(names.contains("karate-progress-json.txt"), names.toString());
            assertFalse(names.contains("feature-a.html"), names.toString());
            assertFalse(names.contains("karate.log"), names.toString());
        } finally {
            org.apache.commons.io.FileUtils.deleteDirectory(dir.toFile());
        }
    }
}

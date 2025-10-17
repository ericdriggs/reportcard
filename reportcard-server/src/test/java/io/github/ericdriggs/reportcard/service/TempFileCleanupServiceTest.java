package io.github.ericdriggs.reportcard.service;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TempFileCleanupServiceTest {

    @Test
    void testCleanupOldFiles() throws IOException, InterruptedException {
        TempFileCleanupService service = new TempFileCleanupService();
        
        // Create old temp files in system temp directory
        String tempDir = System.getProperty("java.io.tmpdir");
        Path oldFile = Files.createTempFile(Paths.get(tempDir), "reportcard-", ".tmp");
        Path oldDir = Files.createTempDirectory(Paths.get(tempDir), "reportcard-");
        
        // Make them appear old (2 hours ago)
        Instant twoHoursAgo = Instant.now().minus(2, ChronoUnit.HOURS);
        Files.setLastModifiedTime(oldFile, FileTime.from(twoHoursAgo));
        Files.setLastModifiedTime(oldDir, FileTime.from(twoHoursAgo));
        
        assertTrue(Files.exists(oldFile));
        assertTrue(Files.exists(oldDir));
        
        // Run cleanup
        service.cleanupTempFiles();
        
        // Verify files are deleted
        assertFalse(Files.exists(oldFile));
        assertFalse(Files.exists(oldDir));
    }
}
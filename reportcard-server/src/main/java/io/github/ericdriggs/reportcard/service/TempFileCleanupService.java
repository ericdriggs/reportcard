package io.github.ericdriggs.reportcard.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TempFileCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(TempFileCleanupService.class);

    private static final int MAX_AGE_HOURS = 1;

    private static final String PREFIX = "reportcard-";

    @Scheduled(fixedRate = 1800000) // 30 minutes in milliseconds
    public void cleanupTempFiles() {
        String tempDir = System.getProperty("java.io.tmpdir");
        logger.info("Starting cleanup of reportcard temp files older than {} hours", MAX_AGE_HOURS);
        
        Path tempPath = Paths.get(tempDir);
        Instant cutoffTime = Instant.now().minus(MAX_AGE_HOURS, ChronoUnit.HOURS);
        int deletedCount = 0;

        try {
            deletedCount = (int) Files.list(tempPath)
                .filter(path -> path.getFileName().toString().startsWith(PREFIX))
                .filter(path -> {
                    try {
                        return Files.getLastModifiedTime(path).toInstant().isBefore(cutoffTime);
                    } catch (IOException e) {
                        return false;
                    }
                })
                .mapToInt(path -> {
                    try {
                        if (Files.isDirectory(path)) {
                            org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory(path.toFile());
                        } else {
                            Files.delete(path);
                        }
                        logger.debug("Deleted: {}", path);
                        return 1;
                    } catch (IOException e) {
                        logger.warn("Failed to delete: {}", path, e);
                        return 0;
                    }
                })
                .sum();
        } catch (IOException e) {
            logger.error("Error during temp file cleanup", e);
        }

        logger.info("Cleanup completed. Deleted {} items", deletedCount);
    }
}
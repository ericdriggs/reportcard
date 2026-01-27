package io.github.ericdriggs.reportcard.controller.util;

import io.github.ericdriggs.reportcard.util.tar.TarExtractorCommonsCompress;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public enum KarateTarGzUtil {
    ; //static methods only

    private static final String KARATE_SUMMARY_FILENAME = "karate-summary-json.txt";

    /**
     * Extracts karate-summary-json.txt content from a tar.gz archive.
     *
     * @param tarGz the tar.gz file containing karate reports
     * @return the content of karate-summary-json.txt, or null if not found or input is null/empty
     */
    @SneakyThrows(IOException.class)
    public static String extractKarateSummaryJson(MultipartFile tarGz) {
        if (tarGz == null || tarGz.isEmpty()) {
            return null;
        }
        Path tempDir = Files.createTempDirectory("reportcard-karate-");
        try {
            InputStream inputStream = tarGz.getInputStream();
            TarExtractorCommonsCompress tarExtractor =
                    new TarExtractorCommonsCompress(inputStream, true, tempDir);
            tarExtractor.untar();

            // Find karate-summary-json.txt recursively (may be in subdirectory)
            return findFileRecursively(tempDir, KARATE_SUMMARY_FILENAME)
                    .map(KarateTarGzUtil::readFileContent)
                    .orElse(null);
        } finally {
            if (tempDir != null) {
                org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory(tempDir.toFile());
            }
        }
    }

    /**
     * Recursively searches for a file by exact name within the given directory.
     *
     * @param directory the directory to search
     * @param fileName the exact file name to find
     * @return Optional containing the path if found, empty otherwise
     */
    @SneakyThrows(IOException.class)
    private static Optional<Path> findFileRecursively(Path directory, String fileName) {
        try (Stream<Path> walk = Files.walk(directory)) {
            return walk
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equals(fileName))
                    .findFirst();
        }
    }

    /**
     * Reads the content of a file as a string.
     *
     * @param path the path to the file
     * @return the file content
     */
    @SneakyThrows(IOException.class)
    private static String readFileContent(Path path) {
        return Files.readString(path);
    }
}

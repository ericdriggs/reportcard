package io.github.ericdriggs.reportcard.controller.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.github.ericdriggs.reportcard.mappers.SharedObjectMappers;
import io.github.ericdriggs.reportcard.util.tar.TarExtractorCommonsCompress;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public enum KarateTarGzUtil {
    ; //static methods only

    private static final String KARATE_SUMMARY_FILENAME = "karate-summary-json.txt";
    private static final String KARATE_NATIVE_SUFFIX = ".karate-json.txt";

    /**
     * Extracts karate-summary-json.txt content from a tar.gz archive.
     *
     * @param tarGz the tar.gz file containing karate reports
     * @return the content of karate-summary-json.txt, or null if not found or input is null/empty
     */
    public static String extractKarateSummaryJson(MultipartFile tarGz) {
        if (tarGz == null || tarGz.isEmpty()) {
            return null;
        }
        return extractKarateSummaryJson(getInputStream(tarGz));
    }

    /**
     * Extracts karate-summary-json.txt content from a tar.gz input stream.
     *
     * @param tarGzInputStream the tar.gz input stream containing karate reports
     * @return the content of karate-summary-json.txt, or null if not found
     */
    @SneakyThrows(IOException.class)
    public static String extractKarateSummaryJson(InputStream tarGzInputStream) {
        Path tempDir = Files.createTempDirectory("reportcard-karate-");
        try {
            TarExtractorCommonsCompress tarExtractor =
                    new TarExtractorCommonsCompress(tarGzInputStream, true, tempDir);
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
     * Extracts the content of every native Karate report file (*.karate-json.txt) from a tar.gz archive.
     *
     * @param tarGz the tar.gz file containing karate reports
     * @return the content of each *.karate-json.txt file, or an empty list if none/null input
     */
    public static List<String> extractKarateNativeJsons(MultipartFile tarGz) {
        if (tarGz == null || tarGz.isEmpty()) {
            return new ArrayList<>();
        }
        return extractKarateNativeJsons(getInputStream(tarGz));
    }

    /**
     * Extracts the content of every native Karate report file (*.karate-json.txt) from a tar.gz input stream.
     *
     * @param tarGzInputStream the tar.gz input stream containing karate reports
     * @return the content of each *.karate-json.txt file, or an empty list if none found
     */
    @SneakyThrows(IOException.class)
    public static List<String> extractKarateNativeJsons(InputStream tarGzInputStream) {
        Path tempDir = Files.createTempDirectory("reportcard-karate-native-");
        try {
            TarExtractorCommonsCompress tarExtractor =
                    new TarExtractorCommonsCompress(tarGzInputStream, true, tempDir);
            tarExtractor.untar();

            try (Stream<Path> walk = Files.walk(tempDir)) {
                return walk
                        .filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().endsWith(KARATE_NATIVE_SUFFIX))
                        .map(KarateTarGzUtil::readFileContent)
                        .collect(Collectors.toList());
            }
        } finally {
            if (tempDir != null) {
                org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory(tempDir.toFile());
            }
        }
    }

    @SneakyThrows(IOException.class)
    private static InputStream getInputStream(MultipartFile file) {
        return file.getInputStream();
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

    /**
     * Extracts Cucumber JSON content from karate.tar.gz.
     * Looks for .json files that are NOT karate-summary-json.txt.
     * Returns combined JSON array of all feature results.
     *
     * @param tarGz the uploaded tar.gz file
     * @return JSON array string of feature results, or null if not found
     */
    public static String extractCucumberJson(MultipartFile tarGz) {
        if (tarGz == null || tarGz.isEmpty()) {
            return null;
        }
        return extractCucumberJson(getInputStream(tarGz));
    }

    /**
     * Extracts Cucumber JSON content from a karate.tar.gz input stream.
     * Looks for .json files that are NOT karate-summary-json.txt.
     * Returns combined JSON array of all feature results.
     *
     * @param tarGzInputStream the tar.gz input stream
     * @return JSON array string of feature results, or null if not found
     */
    @SneakyThrows(IOException.class)
    public static String extractCucumberJson(InputStream tarGzInputStream) {
        Path tempDir = Files.createTempDirectory("reportcard-karate-cucumber-");
        try {
            TarExtractorCommonsCompress tarExtractor =
                    new TarExtractorCommonsCompress(tarGzInputStream, true, tempDir);
            tarExtractor.untar();

            // Find all .json files (excluding summary file)
            List<Path> jsonFiles = findCucumberJsonFiles(tempDir);
            if (jsonFiles.isEmpty()) {
                return null;
            }

            // If single file, return as-is (already an array)
            if (jsonFiles.size() == 1) {
                return readFileContent(jsonFiles.get(0));
            }

            // Multiple files: parse and merge independently, so one malformed file doesn't discard the rest
            return mergeJsonArrays(jsonFiles);

        } finally {
            if (tempDir != null) {
                org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory(tempDir.toFile());
            }
        }
    }

    /**
     * Recursively finds all Cucumber JSON files (*.json excluding summary).
     */
    @SneakyThrows(IOException.class)
    private static List<Path> findCucumberJsonFiles(Path directory) {
        try (Stream<Path> walk = Files.walk(directory)) {
            return walk
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String fileName = path.getFileName().toString();
                        return fileName.endsWith(".json") && !fileName.contains("karate-summary");
                    })
                    .collect(Collectors.toList());
        }
    }

    /**
     * Merges multiple Cucumber JSON arrays into a single array.
     * Each file is a JSON array of features; combine into one array.
     * Each file is parsed independently: a malformed file is logged (with its name) and
     * skipped, so it doesn't discard the data from the other, valid files.
     */
    private static String mergeJsonArrays(List<Path> jsonFiles) {
        ArrayNode combined = SharedObjectMappers.ignoreUnknownObjectMapper.createArrayNode();
        for (Path jsonFile : jsonFiles) {
            try {
                String json = readFileContent(jsonFile);
                JsonNode node = SharedObjectMappers.ignoreUnknownObjectMapper.readTree(json);
                if (node.isArray()) {
                    for (JsonNode elem : node) {
                        combined.add(elem);
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to parse Cucumber JSON file {}, skipping", jsonFile.getFileName(), e);
            }
        }
        try {
            return SharedObjectMappers.ignoreUnknownObjectMapper.writeValueAsString(combined);
        } catch (Exception e) {
            log.warn("Failed to serialize merged Cucumber JSON array", e);
            return null;
        }
    }
}

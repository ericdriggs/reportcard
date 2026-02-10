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

    /**
     * Extracts Cucumber JSON content from karate.tar.gz.
     * Looks for .json files that are NOT karate-summary-json.txt.
     * Returns combined JSON array of all feature results.
     *
     * @param tarGz the uploaded tar.gz file
     * @return JSON array string of feature results, or null if not found
     */
    @SneakyThrows(IOException.class)
    public static String extractCucumberJson(MultipartFile tarGz) {
        if (tarGz == null || tarGz.isEmpty()) {
            return null;
        }

        Path tempDir = Files.createTempDirectory("reportcard-karate-cucumber-");
        try {
            InputStream inputStream = tarGz.getInputStream();
            TarExtractorCommonsCompress tarExtractor =
                    new TarExtractorCommonsCompress(inputStream, true, tempDir);
            tarExtractor.untar();

            // Find all .json files (excluding summary file)
            List<Path> jsonFiles = findCucumberJsonFiles(tempDir);
            if (jsonFiles.isEmpty()) {
                return null;
            }

            // Read and combine JSON contents
            List<String> jsonContents = new ArrayList<>();
            for (Path jsonFile : jsonFiles) {
                jsonContents.add(readFileContent(jsonFile));
            }

            // If single file, return as-is (already an array)
            if (jsonContents.size() == 1) {
                return jsonContents.get(0);
            }

            // Multiple files: merge arrays
            return mergeJsonArrays(jsonContents);

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
     */
    private static String mergeJsonArrays(List<String> jsonContents) {
        try {
            ArrayNode combined = SharedObjectMappers.ignoreUnknownObjectMapper.createArrayNode();
            for (String json : jsonContents) {
                JsonNode node = SharedObjectMappers.ignoreUnknownObjectMapper.readTree(json);
                if (node.isArray()) {
                    for (JsonNode elem : node) {
                        combined.add(elem);
                    }
                }
            }
            return SharedObjectMappers.ignoreUnknownObjectMapper.writeValueAsString(combined);
        } catch (Exception e) {
            log.warn("Failed to merge Cucumber JSON arrays", e);
            return null;
        }
    }
}

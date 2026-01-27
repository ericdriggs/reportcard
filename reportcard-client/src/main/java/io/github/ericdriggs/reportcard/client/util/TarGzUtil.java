package io.github.ericdriggs.reportcard.client.util;

import lombok.SneakyThrows;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for creating tar.gz archives from directories.
 */
public class TarGzUtil {

    /**
     * Creates a tar.gz archive from files in a directory matching a regex pattern.
     * Only processes files in the top level of the directory (does not recurse into subdirectories).
     *
     * @param directory the directory containing files to archive
     * @param fileRegex regex pattern to match filenames (e.g., ".*\\.xml$" for XML files)
     * @return Path to the created temporary tar.gz file
     * @throws IllegalArgumentException if directory doesn't exist, is not a directory, or contains no matching files
     * @throws IOException if an I/O error occurs during archive creation
     */
    @SneakyThrows(IOException.class)
    public static Path createTarGzFromDirectory(Path directory, String fileRegex) {
        // Validate directory exists
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("Path is not a directory: " + directory);
        }

        // List files in directory (single level only, not recursive)
        Pattern pattern = Pattern.compile(fileRegex);
        List<Path> matchingFiles;
        try {
            matchingFiles = Files.list(directory)
                    .filter(Files::isRegularFile)
                    .filter(path -> pattern.matcher(path.getFileName().toString()).matches())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IOException("Failed to list files in directory: " + directory, e);
        }

        // Validate at least one file matched
        if (matchingFiles.isEmpty()) {
            throw new IllegalArgumentException(
                    "No files matching pattern '" + fileRegex + "' found in directory: " + directory);
        }

        // Create temporary tar.gz file
        Path tempTarGz = Files.createTempFile("reportcard-", ".tar.gz");

        // Create tar.gz archive
        try (OutputStream fOut = Files.newOutputStream(tempTarGz);
             BufferedOutputStream buffOut = new BufferedOutputStream(fOut);
             GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(buffOut);
             TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)) {

            for (Path filePath : matchingFiles) {
                // Create tar entry with filename only (not full path)
                TarArchiveEntry tarEntry = new TarArchiveEntry(
                        filePath.toFile(),
                        filePath.getFileName().toString()
                );
                tOut.putArchiveEntry(tarEntry);

                // Copy file content to archive
                Files.copy(filePath, tOut);
                tOut.closeArchiveEntry();
            }

            tOut.finish();
        } catch (IOException e) {
            // Clean up temp file on error
            try {
                Files.deleteIfExists(tempTarGz);
            } catch (IOException cleanupException) {
                // Suppress cleanup exception, throw original
                e.addSuppressed(cleanupException);
            }
            throw new IOException("Failed to create tar.gz archive", e);
        }

        return tempTarGz;
    }
}

package io.github.ericdriggs.reportcard.util.tar;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import static io.github.ericdriggs.file.FileUtils.isJunkFile;

public class TarExtractorCommonsCompress extends TarExtractor {

    public TarExtractorCommonsCompress(InputStream in, boolean gzip, Path destination) throws IOException {
        super(in, gzip, destination);
    }

    @Override
    public void untar() throws IOException {
        Set<Path> folderPaths = new HashSet<>();
        try (BufferedInputStream inputStream = new BufferedInputStream(getTarStream()); //
            TarArchiveInputStream tar = new TarArchiveInputStream( //
                isGzip() ? new GzipCompressorInputStream(inputStream) : inputStream)) {
            ArchiveEntry entry;
            while ((entry = tar.getNextEntry()) != null) {
                final String entryName = entry.getName();
                if (isJunkFile(entryName)) {
                    continue;
                }
                Path extractTo = getDestination().resolve(entryName);
                if (entry.isDirectory()) {
                    Files.createDirectories(extractTo);
                } else {
                    final Path parent = extractTo.getParent();
                    if (!folderPaths.contains(parent)) {
                        Files.createDirectories(parent);
                        folderPaths.add(parent);
                    }
                    Files.copy(tar, extractTo, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}
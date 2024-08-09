package io.github.ericdriggs.reportcard.util.tar;

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

public class TarCompressor {


        @SneakyThrows(IOException.class)
    public static void createTarGzipFiles(List<Path> paths, Path output) {
        try (OutputStream fOut = Files.newOutputStream(output);
             BufferedOutputStream buffOut = new BufferedOutputStream(fOut);
             GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(buffOut);
             TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)) {

            for (Path path : paths) {
                if (!Files.isRegularFile(path)) {
                    throw new IOException("Support only file!");
                }
                TarArchiveEntry tarEntry = new TarArchiveEntry(path.toFile(), path.getFileName().toString());
                tOut.putArchiveEntry(tarEntry);

                // copy file to TarArchiveOutputStream
                Files.copy(path, tOut);
                tOut.closeArchiveEntry();
            }
            tOut.finish();
        }
    }


}

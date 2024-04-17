package io.github.ericdriggs.reportcard.controller.util;

import io.github.ericdriggs.file.FileUtils;
import io.github.ericdriggs.reportcard.util.tar.TarCompressor;
import io.github.ericdriggs.reportcard.util.tar.TarExtractorCommonsCompress;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static io.github.ericdriggs.file.FileUtils.regexForExtension;

public enum TestXmlTarGzUtil {
    ;
    //static methods only

    @SneakyThrows(IOException.class)
    public static List<String> getFileContentsFromTarGz(MultipartFile tarGz) {
        Path tempDir = Files.createTempDirectory("tar-gz");
        try {
            extractTarGz(tempDir, tarGz);
            return FileUtils.fileContentsFromPathAndRegex(tempDir, regexForExtension("xml"));
        } finally {
            if (tempDir != null) {
                org.apache.tomcat.util.http.fileupload.FileUtils.deleteDirectory(tempDir.toFile());
            }
        }
    }

    /**
     * Important! The caller *MUST* delete the tempDir after finishing to prevent potential disk space leak
     *
     * @param tarGz
     * @return the path to the temporary director
     */
    @SneakyThrows(IOException.class)
    static Path extractTarGz(Path tempDir, MultipartFile tarGz) {
        InputStream inputStream = tarGz.getInputStream();
        TarExtractorCommonsCompress tarExtractor = new TarExtractorCommonsCompress(inputStream, true, tempDir);
        tarExtractor.untar();
        return tempDir;
    }

    /**
     * Important! The caller *MUST* delete the returned .tar.gz after finishing to prevent potential disk space leak
     * @param filePaths the file paths
     * @return the temporary tar.gz for testing
     */
    @SneakyThrows(IOException.class)
    public static Path createTarGzipFilesForTesting(List<Path> filePaths) {
        Path tarGzOutput = Files.createTempFile("tar-gz", ".tar.gz");
        try {
            TarCompressor.createTarGzipFiles(filePaths, tarGzOutput);
        } catch (Exception ex) {
            ex.printStackTrace();
            Files.delete(tarGzOutput);
        }
        return tarGzOutput;
    }

    /**
     * Important! The caller *MUST* delete the returned .tar.gz after finishing to prevent potential disk space leak
     * @param files the files
     * @return the temporary tar.gz for testing
     */
    @SneakyThrows(IOException.class)
    public static Path createTarGzipFilesForTesting(MultipartFile[] files) {

        Path tmpDir = Files.createTempDirectory("tar-gz");
        try {
            List<Path> filePaths = new ArrayList<>();
            for (MultipartFile file : files) {
                Path localPath = Files.createTempFile(tmpDir, file.getName(), "");
                file.transferTo(localPath);
                filePaths.add(localPath);
            }
            return createTarGzipFilesForTesting(filePaths);
        } finally {
            Files.delete(tmpDir);
        }
    }
}

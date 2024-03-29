package io.github.ericdriggs.reportcard.controller.util;

import io.github.ericdriggs.file.FileUtils;
import io.github.ericdriggs.reportcard.util.tar.TarExtractorCommonsCompress;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
}

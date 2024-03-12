package io.github.ericdriggs.reportcard.controller.util;

import io.github.ericdriggs.reportcard.model.converter.junit.JunitFileMergerUtil;
import io.github.ericdriggs.reportcard.util.tar.TarExtractorCommonsCompress;
import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public enum JunitTarGzMergeUtil {
    ;
    //static methods only



    @SneakyThrows(IOException.class)
    public static Path mergeJunitTarGz(MultipartFile tarGz) {
        final String prefix = "junit-merged.";
        Path mergedFile = Files.createTempFile(prefix,".xml");
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory(prefix);
            InputStream inputStream = tarGz.getInputStream();
            TarExtractorCommonsCompress tarExtractor = new TarExtractorCommonsCompress(inputStream, true, tempDir);

            tarExtractor.untar();

            JunitFileMergerUtil.writeMergedJunitXml(tempDir.toString(), mergedFile.toString());
            return mergedFile;
        } finally {
            if (tempDir != null) {
                FileUtils.deleteDirectory(tempDir.toFile());
            }
        }
    }
}

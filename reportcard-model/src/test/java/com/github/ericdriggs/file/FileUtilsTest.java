package com.github.ericdriggs.file;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileUtilsTest {

    final static String relativePath = "src/test/resources/format-samples/surefire-reports";
    final static String absolutePath = FileUtils.absolutePathFromRelativePath(relativePath);
    final static String xmlRegex = FileUtils.regexForExtension("xml");
    @Test
    public void findFilesTest() {


        List<String> fileNames = FileUtils.filePathsForPathAndRegex(absolutePath,xmlRegex );
        assertEquals(fileNames.size(), 3);
        for (String s : fileNames) {
            assertTrue(s.contains(relativePath));
            assertTrue(s.endsWith("xml"));
        }
    }

    @Test
    public void readFilesTest() {
        List<String> fileContents = FileUtils.fileContentsFromPathAndRegex(relativePath,xmlRegex );
        assertEquals(fileContents.size(), 3);
        for (String s : fileContents) {
            assertTrue(s.contains("testsuite"));
        }
    }
}

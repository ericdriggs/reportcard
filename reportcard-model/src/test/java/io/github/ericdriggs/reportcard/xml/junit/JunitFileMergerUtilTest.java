package io.github.ericdriggs.reportcard.xml.junit;


import io.github.ericdriggs.reportcard.model.converter.merge.JunitFileMergerUtil;
import org.junit.jupiter.api.Test;

import static io.github.ericdriggs.file.FileUtils.absolutePathFromRelativePath;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("deprecation")
public class JunitFileMergerUtilTest {

    @Test
    void mergeJsonTest() {
        Testsuites testsuites = JunitFileMergerUtil.mergeJunitFiles(absolutePathFromRelativePath("src/test/resources/format-samples/junit"));
        assertEquals(8, testsuites.getTestsuite().size());

        int testCount = 0;
        for (Testsuite testsuite : testsuites.getTestsuite()) {
            testCount += testsuite.getTests();
        }
        assertEquals(12, testCount);
    }

    @Test
    void writeMergeJsonTest() {
        JunitFileMergerUtil.writeMergedJunitXml("src/test/resources/format-samples/junit", "build/junit-merged.xml");

    }

}

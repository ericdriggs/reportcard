package com.github.ericdriggs.reportcard.model;

import com.github.ericdriggs.file.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ResultParserUtilTest {


    @Test
    public void resultTest() {
        final String relativePath = "src/test/resources/format-samples/surefire-reports";
        final String absolutePath = FileUtils.absolutePathFromRelativePath(relativePath);

        TestResult testResult = ResultParserUtil.fromSurefirePath(absolutePath);
        assertEquals(3, testResult.getTestSuites().size());
        Assertions.assertEquals(24, testResult.getTests());
        Assertions.assertEquals(2, testResult.getSkipped());
        Assertions.assertEquals(3, testResult.getError());
        Assertions.assertEquals(9, testResult.getFailure());
        assertEquals(false, testResult.getIsSuccess());
        assertEquals(true, testResult.getHasSkip());

        //These values are null because they are generated when persisted
        assertNull(testResult.getTestResultId());
        assertNull(testResult.getStageFk());
        assertNull(testResult.getExternalLinks());
        assertNull(testResult.getTestResultCreated());

        Assertions.assertEquals(new BigDecimal(6).setScale(1), testResult.getTime().setScale(1));
    }

}

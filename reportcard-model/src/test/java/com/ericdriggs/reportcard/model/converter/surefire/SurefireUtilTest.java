package com.ericdriggs.reportcard.model.converter.surefire;

import com.ericdriggs.file.FileUtils;
import com.ericdriggs.reportcard.model.TestResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SurefireUtilTest {

    final static String relativePath = "src/test/resources/format-samples/surefire-reports";
    final static String xmlRegex = FileUtils.regexForExtension("xml");

    @Test
    public void resultTest() {
        TestResult testResult = SurefireUtil.fromRelativePath(relativePath, xmlRegex);
        assertEquals(3, testResult.getTestSuites().size());
        assertEquals(24, testResult.getTests());
        assertEquals(2, testResult.getSkipped());
        assertEquals(3, testResult.getError());
        assertEquals(9, testResult.getFailure());
        assertEquals(false, testResult.getIsSuccess());
        assertEquals(true, testResult.getHasSkip());

        //These values are null because they are generated when persisted
        assertNull(testResult.getTestResultId());
        assertNull(testResult.getStageFk());
        assertNull(testResult.getExternalLinks());
        assertNull(testResult.getTestResultCreated());

        assertEquals(new BigDecimal(6).setScale(1), testResult.getTime().setScale(1));

    }
}

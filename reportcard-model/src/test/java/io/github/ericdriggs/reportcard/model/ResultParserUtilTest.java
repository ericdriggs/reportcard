package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.file.FileUtils;
import io.github.ericdriggs.reportcard.xml.ResultCount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.NoSuchFileException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ResultParserUtilTest {


    private final static int ERROR_COUNT = 3;
    private final static int FAILURE_COUNT = 9;
    private final static int SKIPPED_COUNT = 2;
    private final static int SUCCESS_COUNT = 10;
    private final static int TEST_COUNT = 24;

    private final static BigDecimal PASSED_PERCENTAGE = new BigDecimal("45.45");
    private final static BigDecimal TIME_TOTAL = new BigDecimal(TEST_COUNT - SKIPPED_COUNT).setScale(1);
    //private final static BigDecimal TIME_TOTAL = new BigDecimal("0.112229");

    @Test
    public void resultTest() {
        final String relativePath = "src/test/resources/format-samples/surefire-reports";
        final String absolutePath = FileUtils.absolutePathFromRelativePath(relativePath);

        TestResult testResult = ResultParserUtil.fromSurefirePath(absolutePath);
        assertEquals(3, testResult.getTestSuites().size());

        Assertions.assertEquals(ERROR_COUNT, testResult.getError());
        Assertions.assertEquals(FAILURE_COUNT, testResult.getFailure());
        Assertions.assertEquals(SKIPPED_COUNT, testResult.getSkipped());
        Assertions.assertEquals(TEST_COUNT, testResult.getTests());

        assertEquals(false, testResult.getIsSuccess());
        assertEquals(true, testResult.getHasSkip());

        //These values are null because they are generated when persisted
        assertNull(testResult.getTestResultId());
        assertNull(testResult.getStageFk());
        assertNull(testResult.getExternalLinks());
        assertNull(testResult.getTestResultCreated());

        Assertions.assertEquals(TIME_TOTAL, testResult.getTime().setScale(1));

        ResultCount resultCount = testResult.getResultCount();
        assertEquals(ERROR_COUNT, resultCount.getErrors());
        assertEquals(FAILURE_COUNT, resultCount.getFailures());
        assertEquals(PASSED_PERCENTAGE, resultCount.getPassedPercent());
        assertEquals(SKIPPED_COUNT, resultCount.getSkipped());
        assertEquals(SUCCESS_COUNT, resultCount.getSuccesses());
        assertEquals(TEST_COUNT, resultCount.getTests());
        assertEquals(TIME_TOTAL, resultCount.getTime());
    }

    @Test
    public void invalidPathTest() {

        final String invalidRelativePath = "src/test/resources/invalid/path";
        final String invalidAbsolutePath = FileUtils.absolutePathFromRelativePath(invalidRelativePath);

        NoSuchFileException thrown = Assertions.assertThrows(NoSuchFileException.class, () -> {
            ResultParserUtil.fromSurefirePath(invalidAbsolutePath);
        });

    }
}

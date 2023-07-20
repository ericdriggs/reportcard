package io.github.ericdriggs.reportcard.model;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Comparator;

import static io.github.ericdriggs.reportcard.util.CompareUtil.chainCompare;
import static io.github.ericdriggs.reportcard.util.CompareUtil.compareLong;

public class ModelComparators {

    public static int compareTestResultModel(io.github.ericdriggs.reportcard.model.TestResult val1, io.github.ericdriggs.reportcard.model.TestResult val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                compareLong(val1.getStageFk(), val2.getStageFk()),
                ObjectUtils.compare(val1.getTestResultId(), val2.getTestResultId())
        );
    }

    public static final Comparator<TestResult> TEST_RESULT_MODEL_CASE_INSENSITIVE_ORDER
            = new ModelComparators.TestResultModelCaseInsensitiveComparator();

    public static int compareTestResult(TestResult val1, TestResult val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                compareLong(val1.getStageFk(), val2.getStageFk()),
                ObjectUtils.compare(val1.getTestResultId(), val2.getTestResultId())
        );
    }

    public static int compareTestSuite(TestSuite val1, TestSuite val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                compareLong(val1.getTestSuiteId(), val2.getTestSuiteId()),
                ObjectUtils.compare(val1.getName(), val2.getName())
        );
    }

    public static int compareTestCase(TestCase val1, TestCase val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                compareLong(val1.getTestCaseId(), val2.getTestCaseId()),
                ObjectUtils.compare(val1.getName(), val2.getName())
        );
    }

    private static class TestResultModelCaseInsensitiveComparator
            implements Comparator<io.github.ericdriggs.reportcard.model.TestResult>, java.io.Serializable {
        private static final long serialVersionUID = 8426777269373042386L;

        public int compare(io.github.ericdriggs.reportcard.model.TestResult val1, io.github.ericdriggs.reportcard.model.TestResult val2) {
            return compareTestResultModel(val1, val2);
        }
    }

}

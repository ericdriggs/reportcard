package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.util.CompareUtil;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serial;
import java.util.Comparator;

public class ModelComparators {

    public static int compareTestResultModel(TestResult val1, TestResult val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return CompareUtil.chainCompare(
                CompareUtil.compareLong(val1.getStageFk(), val2.getStageFk()),
                CompareUtil.compareLong(val1.getTestResultId(), val2.getTestResultId())
        );
    }

    public static final Comparator<TestResult> TEST_RESULT_MODEL_CASE_INSENSITIVE_ORDER
            = new ModelComparators.TestResultModelCaseInsensitiveComparator();

    public static final Comparator<TestCase> TEST_CASE_INSENSITIVE_ORDER
            = new ModelComparators.TestCaseModelCaseInsensitiveComparator();

    public static final Comparator<TestCaseFault> TEST_CASE_FAULT_BY_ID
            = new ModelComparators.TestCaseFaultIdComparator();


    public static int compareTestResult(TestResult val1, TestResult val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return CompareUtil.chainCompare(
                CompareUtil.compareLong(val1.getStageFk(), val2.getStageFk()),
                CompareUtil.compareLong(val1.getTestResultId(), val2.getTestResultId())
        );
    }

    public static int compareTestSuite(TestSuite val1, TestSuite val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return CompareUtil.chainCompare(
                CompareUtil.compareLong(val1.getTestSuiteId(), val2.getTestSuiteId()),
                CompareUtil.compareLowerNullSafe(val1.getName(), val2.getName())
        );
    }

    public static int compareTestCase(TestCase val1, TestCase val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return CompareUtil.chainCompare(
                CompareUtil.compareLowerNullSafe(val1.getName(), val2.getName()),
                CompareUtil.compareLong(val1.getTestCaseId(), val2.getTestCaseId())
        );
    }

    public static int compareTestCaseFaultById(TestCaseFault val1, TestCaseFault val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return CompareUtil.chainCompare(
                CompareUtil.compareLong(val1.getTestCaseFaultId(), val2.getTestCaseFaultId())
        );
    }

    private static class TestResultModelCaseInsensitiveComparator
            implements Comparator<TestResult>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 8426777269373042386L;

        public int compare(TestResult val1, TestResult val2) {
            return compareTestResultModel(val1, val2);
        }
    }

    private static class TestCaseModelCaseInsensitiveComparator
            implements Comparator<TestCase>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 7483717170227460027L;

        public int compare(TestCase val1, TestCase val2) {
            return compareTestCase(val1, val2);
        }
    }

    private static class TestCaseFaultIdComparator
            implements Comparator<TestCaseFault>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 8290122986310483551L;

        public int compare(TestCaseFault val1, TestCaseFault val2) {
            return compareTestCaseFaultById(val1, val2);
        }
    }

}

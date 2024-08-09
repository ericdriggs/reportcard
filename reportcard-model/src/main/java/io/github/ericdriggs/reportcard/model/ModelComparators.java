package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.util.CompareUtil;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serial;
import java.util.Comparator;

public class ModelComparators {

    public static final Comparator<TestResultModel> TEST_RESULT_MODEL_CASE_INSENSITIVE_ORDER
            = new ModelComparators.TestResultModelCaseInsensitiveComparator();

    public static final Comparator<TestSuiteModel> TEST_SUITE_CASE_INSENSITIVE_ORDER
            = new ModelComparators.TestSuiteCaseInsensitiveComparator();


    public static final Comparator<TestCaseModel> TEST_CASE_INSENSITIVE_ORDER
            = new ModelComparators.TestCaseModelCaseInsensitiveComparator();

    public static final Comparator<TestCaseFaultModel> TEST_CASE_FAULT_BY_ID
            = new ModelComparators.TestCaseFaultIdComparator();

    public static int compareTestResult(TestResultModel val1, TestResultModel val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(!ObjectUtils.isEmpty(val1), !ObjectUtils.isEmpty(val2));
        }
        return CompareUtil.chainCompare(
                CompareUtil.compareLong(val1.getStageFk(), val2.getStageFk()),
                CompareUtil.compareLong(val1.getTestResultId(), val2.getTestResultId())
        );
    }

    public static int compareTestSuiteModelByName(TestSuiteModel val1, TestSuiteModel val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(!ObjectUtils.isEmpty(val1), !ObjectUtils.isEmpty(val2));
        }
        return CompareUtil.chainCompare(
                CompareUtil.compareLowerNullSafe(val1.getName(), val2.getName()),
                CompareUtil.compareLong(val1.getTestSuiteId(), val2.getTestSuiteId()),
                CompareUtil.compareLowerNullSafe(val1.getPackageName(), val2.getPackageName()),
                val1.getResultCount().compareTo(val2.getResultCount())
        );
    }

    public static int compareTestResultModel(TestResultModel val1, TestResultModel val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(!ObjectUtils.isEmpty(val1), !ObjectUtils.isEmpty(val2));
        }
        return CompareUtil.chainCompare(
                CompareUtil.compareLong(val1.getStageFk(), val2.getStageFk()),
                CompareUtil.compareLong(val1.getTestResultId(), val2.getTestResultId())
        );
    }

    public static int compareTestSuite(TestSuiteModel val1, TestSuiteModel val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(!ObjectUtils.isEmpty(val1), !ObjectUtils.isEmpty(val2));
        }
        return CompareUtil.chainCompare(
                CompareUtil.compareLong(val1.getTestSuiteId(), val2.getTestSuiteId()),
                CompareUtil.compareLowerNullSafe(val1.getName(), val2.getName())
        );
    }

    public static int compareTestCase(TestCaseModel val1, TestCaseModel val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(!ObjectUtils.isEmpty(val1), !ObjectUtils.isEmpty(val2));
        }
        return CompareUtil.chainCompare(
                CompareUtil.compareLowerNullSafe(val1.getName(), val2.getName()),
                CompareUtil.compareLong(val1.getTestCaseId(), val2.getTestCaseId())
        );
    }

    public static int compareTestCaseFaultById(TestCaseFaultModel val1, TestCaseFaultModel val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(!ObjectUtils.isEmpty(val1), !ObjectUtils.isEmpty(val2));
        }
        return CompareUtil.chainCompare(
                CompareUtil.compareLong(val1.getTestCaseFaultId(), val2.getTestCaseFaultId())
        );
    }

    private static class TestResultModelCaseInsensitiveComparator
            implements Comparator<TestResultModel>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 8426777269373042386L;

        public int compare(TestResultModel val1, TestResultModel val2) {
            return compareTestResultModel(val1, val2);
        }
    }

    private static class TestSuiteCaseInsensitiveComparator
            implements Comparator<TestSuiteModel>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = -4834022062900145966L;

        public int compare(TestSuiteModel val1, TestSuiteModel val2) {
            return compareTestSuiteModelByName(val1, val2);
        }
    }



    private static class TestCaseModelCaseInsensitiveComparator
            implements Comparator<TestCaseModel>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 7483717170227460027L;

        public int compare(TestCaseModel val1, TestCaseModel val2) {
            return compareTestCase(val1, val2);
        }
    }

    private static class TestCaseFaultIdComparator
            implements Comparator<TestCaseFaultModel>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 8290122986310483551L;

        public int compare(TestCaseFaultModel val1, TestCaseFaultModel val2) {
            return compareTestCaseFaultById(val1, val2);
        }
    }

}

package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import io.github.ericdriggs.reportcard.util.JsonCompare;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Comparator;

public class Comparators {

    public static final Comparator<Org> ORG_CASE_INSENSITIVE_ORDER
            = new Comparators.OrgCaseInsensitiveComparator();

    public static final Comparator<Repo> REPO_CASE_INSENSITIVE_ORDER
            = new Comparators.RepoCaseInsensitiveComparator();

    public static final Comparator<Branch> BRANCH_CASE_INSENSITIVE_ORDER
            = new Comparators.BranchCaseInsensitiveComparator();

    public static final Comparator<Job> JOB_CASE_INSENSITIVE_ORDER
            = new Comparators.ContextCaseInsensitiveComparator();

    public static final Comparator<Execution> EXECUTION_CASE_INSENSITIVE_ORDER
            = new Comparators.ExecutionCaseInsensitiveComparator();

    public static final Comparator<Stage> STAGE_CASE_INSENSITIVE_ORDER
            = new Comparators.StageCaseInsensitiveComparator();

    public static final Comparator<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult> TEST_RESULT_CASE_INSENSITIVE_ORDER
            = new Comparators.TestResultCaseInsensitiveComparator();

    public static final Comparator<TestResult> TEST_RESULT_MODEL_CASE_INSENSITIVE_ORDER
            = new Comparators.TestResultModelCaseInsensitiveComparator();

    public static final Comparator<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite> TEST_SUITE_CASE_INSENSITIVE_ORDER
            = new Comparators.TestSuiteCaseInsensitiveComparator();

    public static final Comparator<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCase> TEST_CASE_CASE_INSENSITIVE_ORDER
            = new Comparators.TestCaseCaseInsensitiveComparator();

    private static class OrgCaseInsensitiveComparator
            implements Comparator<Org>, java.io.Serializable {
        private static final long serialVersionUID = 7807917410507365390L;

        public int compare(Org val1, Org val2) {
            return compareOrg(val1, val2);
        }
    }

    private static class RepoCaseInsensitiveComparator
            implements Comparator<Repo>, java.io.Serializable {
        private static final long serialVersionUID = 1499664932611968428L;

        public int compare(Repo val1, Repo val2) {
            return compareRepo(val1, val2);
        }

    }

    private static class BranchCaseInsensitiveComparator
            implements Comparator<Branch>, java.io.Serializable {
        private static final long serialVersionUID = 6449752623071242729L;

        public int compare(Branch val1, Branch val2) {
            return compareBranch(val1, val2);
        }
    }

    private static class ContextCaseInsensitiveComparator
            implements Comparator<Job>, java.io.Serializable {
        private static final long serialVersionUID = 9214266396218473015L;

        public int compare(Job val1, Job val2) {
            return compareJob(val1, val2);
        }
    }

    private static class ExecutionCaseInsensitiveComparator
            implements Comparator<Execution>, java.io.Serializable {
        private static final long serialVersionUID = 414949285086649733L;

        public int compare(Execution val1, Execution val2) {
            return compareExecution(val1, val2);
        }
    }

    private static class StageCaseInsensitiveComparator
            implements Comparator<Stage>, java.io.Serializable {
        private static final long serialVersionUID = 4341786563640814257L;

        public int compare(Stage val1, Stage val2) {
            return compareStage(val1, val2);
        }
    }

    public static int compareOrg(Org val1, Org val2) {
        return chainCompare(
                compareLowerNullSafe(val1.getOrgName(), val2.getOrgName()),
                Integer.compare(val1.getOrgId(), val2.getOrgId())
        );
    }

    public static int compareRepo(Repo val1, Repo val2) {
        return chainCompare(
                Integer.compare(val1.getOrgFk(), val2.getOrgFk()),
                compareLowerNullSafe(val1.getRepoName(), val2.getRepoName()),
                Integer.compare(val1.getRepoId(), val2.getRepoId())
        );
    }

    public static int compareBranch(Branch val1, Branch val2) {
        return chainCompare(
                Integer.compare(val1.getRepoFk(), val2.getRepoFk()),
                compareLowerNullSafe(val1.getBranchName(), val2.getBranchName()),
                Integer.compare(val1.getBranchId(), val2.getBranchId())
        );
    }

    public static int compareJob(Job val1, Job val2) {
        return chainCompare(
                JsonCompare.compareTo(val1.getJobInfo(), val2.getJobInfo()),
                compareLowerNullSafe(val1.getJobInfo(), val2.getJobInfo()),
                Long.compare(val1.getJobId(), val2.getJobId())
        );
    }

    public static int compareExecution(Execution val1, Execution val2) {
        return chainCompare(
                Long.compare(val1.getJobFk(), val2.getJobFk()),
                ObjectUtils.compare(val1.getExecutionReference(), val2.getExecutionReference()),
                ObjectUtils.compare(val1.getExecutionId(), val2.getExecutionId()),
                ObjectUtils.compare(val1.getSha(), val2.getSha()),
                Long.compare(val1.getExecutionId(), val2.getExecutionId())
        );
    }

    public static int compareStage(Stage val1, Stage val2) {
        return chainCompare(
                Long.compare(val1.getExecutionFk(), val2.getExecutionFk()),
                ObjectUtils.compare(val1.getStageName(), val2.getStageName()),
                ObjectUtils.compare(val1.getStageId(), val2.getStageId())
        );
    }

    public static int compareTestResult(io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult val1, io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult val2) {
        return chainCompare(
                Long.compare(val1.getStageFk(), val2.getStageFk()),
                ObjectUtils.compare(val1.getTestResultId(), val2.getTestResultId())
        );
    }

    public static int compareTestSuite(io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite val1, io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite val2) {
        return chainCompare(
                Long.compare(val1.getTestSuiteId(), val2.getTestSuiteId()),
                ObjectUtils.compare(val1.getName(), val2.getName())
        );
    }

    public static int compareTestCase(io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCase val1, io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCase val2) {
        return chainCompare(
                Long.compare(val1.getTestCaseId(), val2.getTestCaseId()),
                ObjectUtils.compare(val1.getName(), val2.getName())
        );
    }

    public static int compareTestResultModel(TestResult val1, TestResult val2) {
        return chainCompare(
                Long.compare(val1.getStageFk(), val2.getStageFk()),
                ObjectUtils.compare(val1.getTestResultId(), val2.getTestResultId())
        );
    }

    public static int chainCompare(int... compares) {
        for (int compare : compares) {
            if (compare != 0) {
                return compare;
            }
        }
        return 0;
    }

    public static int compareLowerNullSafe(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return ObjectUtils.compare(s1, s2);
        }
        return s1.toLowerCase().compareTo(s2.toLowerCase());

    }

    public static String toLower(String string) {
        if (string == null) {
            return null;
        } else {
            return string.toLowerCase();
        }
    }

    private static class TestResultCaseInsensitiveComparator
            implements Comparator<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult>, java.io.Serializable {
        private static final long serialVersionUID = -859866670502292563L;

        public int compare(io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult val1, io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestResult val2) {
            return compareTestResult(val1, val2);
        }
    }

    private static class TestResultModelCaseInsensitiveComparator
            implements Comparator<TestResult>, java.io.Serializable {
        private static final long serialVersionUID = 8426777269373042386L;

        public int compare(TestResult val1, TestResult val2) {
            return compareTestResultModel(val1, val2);
        }
    }

    private static class TestSuiteCaseInsensitiveComparator
            implements Comparator<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite>, java.io.Serializable {
        private static final long serialVersionUID = -5672061856924641442L;

        public int compare(io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite val1, io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestSuite val2) {
            return compareTestSuite(val1, val2);
        }
    }

    private static class TestCaseCaseInsensitiveComparator
            implements Comparator<io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCase>, java.io.Serializable {
        private static final long serialVersionUID = 5737556829091435969L;

        public int compare(io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCase val1, io.github.ericdriggs.reportcard.gen.db.tables.pojos.TestCase val2) {
            return compareTestCase(val1, val2);
        }
    }
}

package io.github.ericdriggs.reportcard.model;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.*;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Comparator;

public class Comparators {

    public static final Comparator<Org> ORG_CASE_INSENSITIVE_ORDER
            = new Comparators.OrgCaseInsensitiveComparator();

    public static final Comparator<Repo> REPO_CASE_INSENSITIVE_ORDER
            = new Comparators.RepoCaseInsensitiveComparator();

    public static final Comparator<Branch> BRANCH_CASE_INSENSITIVE_ORDER
            = new Comparators.BranchCaseInsensitiveComparator();

    public static final Comparator<Sha> SHA
            = new Comparators.ShaComparator();

    public static final Comparator<Context> CONTEXT_CASE_INSENSITIVE_ORDER
            = new Comparators.ContextCaseInsensitiveComparator();

    public static final Comparator<Execution> EXECUTION_CASE_INSENSITIVE_ORDER
            = new Comparators.ExecutionCaseInsensitiveComparator();

    public static final Comparator<Stage> STAGE_CASE_INSENSITIVE_ORDER
            = new Comparators.StageCaseInsensitiveComparator();

    public static final Comparator<TestResult> TEST_RESULT_CASE_INSENSITIVE_ORDER
            = new Comparators.TestResultCaseInsensitiveComparator();

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

    private static class ShaComparator
            implements Comparator<Sha>, java.io.Serializable {
        private static final long serialVersionUID = 4527771070609844606L;
        public int compare(Sha val1, Sha val2) {
            return compareSha(val1, val2);
        }
    }

    private static class ContextCaseInsensitiveComparator
            implements Comparator<Context>, java.io.Serializable {
        private static final long serialVersionUID = 9214266396218473015L;

        public int compare(Context val1, Context val2) {
            return compareContext(val1, val2);
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

    public static int compareSha(Sha val1, Sha val2) {
        return chainCompare(
                Integer.compare(val1.getRepoFk(), val2.getRepoFk()),
                ObjectUtils.compare(val1.getSha(), val2.getSha()),
                Long.compare(val1.getShaId(), val2.getShaId())
        );
    }

    public static int compareContext(Context val1, Context val2) {
        return chainCompare(
                Long.compare(val1.getShaFk(), val2.getShaFk()),
                compareLowerNullSafe(val1.getMetadata(), val2.getMetadata()),
                Long.compare(val1.getContextId(), val2.getContextId())
        );
    }

    public static int compareExecution(Execution val1, Execution val2) {
        return chainCompare(
                Long.compare(val1.getContextFk(), val2.getContextFk()),
                ObjectUtils.compare(val1.getExecutionExternalId(), val2.getExecutionExternalId()),
                ObjectUtils.compare(val1.getExecutionId(), val2.getExecutionId()),
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

    public static int compareTestResult(TestResult val1, TestResult val2) {
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
            implements Comparator<TestResult>, java.io.Serializable {
        private static final long serialVersionUID = -859866670502292563L;

        public int compare(TestResult val1, TestResult val2) {
            return compareTestResult(val1, val2);
        }
    }
}

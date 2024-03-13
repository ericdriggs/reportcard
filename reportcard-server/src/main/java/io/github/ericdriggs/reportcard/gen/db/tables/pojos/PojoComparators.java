package io.github.ericdriggs.reportcard.gen.db.tables.pojos;

import io.github.ericdriggs.reportcard.cache.model.JobRun;
import io.github.ericdriggs.reportcard.cache.model.StageTestResult;
import io.github.ericdriggs.reportcard.util.JsonCompare;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Comparator;

import static io.github.ericdriggs.reportcard.util.CompareUtil.*;

public class PojoComparators {

    public static final Comparator<Company> COMPANY_CASE_INSENSITIVE_ORDER
            = new PojoComparators.CompanyCaseInsensitiveComparator();

    public static final Comparator<Org> ORG_CASE_INSENSITIVE_ORDER
            = new PojoComparators.OrgCaseInsensitiveComparator();

    public static final Comparator<Repo> REPO_CASE_INSENSITIVE_ORDER
            = new PojoComparators.RepoCaseInsensitiveComparator();

    public static final Comparator<Branch> BRANCH_CASE_INSENSITIVE_ORDER
            = new PojoComparators.BranchCaseInsensitiveComparator();

    public static final Comparator<Job> JOB_CASE_INSENSITIVE_ORDER
            = new JobCaseInsensitiveComparator();

    public static final Comparator<JobRun> JOB_RUN_DATE_DESCENDING_ORDER
            = new JobRunDateDescendingComparator();

    public static final Comparator<Run> RUN_CASE_INSENSITIVE_ORDER
            = new PojoComparators.RunCaseInsensitiveComparator();

    public static final Comparator<Run> RUN_DESCENDING
            = new PojoComparators.RunDescendingComparator();

    public static final Comparator<Stage> STAGE_CASE_INSENSITIVE_ORDER
            = new PojoComparators.StageCaseInsensitiveComparator();

    public static final Comparator<Stage> STAGE_ASCENDING
            = new PojoComparators.StageAscendingComparator();
    public static final Comparator<StageTestResult> STAGE_TEST_RESULT_COMPARATOR_CASE_INSENSITIVE_ORDER
            = new StageTestResultCaseInsensitiveComparator();

    public static final Comparator<StageTestResult> STAGE_TEST_RESULT_DATE_DESCENDING
            = new StageTestResultDateDescendingCaseInsensitiveComparator();


    public static final Comparator<TestResult> TEST_RESULT_CASE_INSENSITIVE_ORDER
            = new PojoComparators.TestResultCaseInsensitiveComparator();

    public static final Comparator<TestSuite> TEST_SUITE_CASE_INSENSITIVE_ORDER
            = new PojoComparators.TestSuiteCaseInsensitiveComparator();

    public static final Comparator<TestCase> TEST_CASE_CASE_INSENSITIVE_ORDER
            = new PojoComparators.TestCaseCaseInsensitiveComparator();


    public static final Comparator<Storage> STORAGE_CASE_INSENSITIVE_ORDER
            = new PojoComparators.StorageCaseInsensitiveComparator();

    private static class CompanyCaseInsensitiveComparator
            implements Comparator<Company>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 1546298733674170266L;

        public int compare(Company val1, Company val2) {
            return compareCompany(val1, val2);
        }
    }

    private static class OrgCaseInsensitiveComparator
            implements Comparator<Org>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 7807917410507365390L;

        public int compare(Org val1, Org val2) {
            return compareOrg(val1, val2);
        }
    }

    private static class RepoCaseInsensitiveComparator
            implements Comparator<Repo>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 1499664932611968428L;

        public int compare(Repo val1, Repo val2) {
            return compareRepo(val1, val2);
        }

    }

    private static class BranchCaseInsensitiveComparator
            implements Comparator<Branch>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 6449752623071242729L;

        public int compare(Branch val1, Branch val2) {
            return compareBranch(val1, val2);
        }
    }

    private static class JobCaseInsensitiveComparator
            implements Comparator<Job>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 9214266396218473015L;

        public int compare(Job val1, Job val2) {
            return compareJob(val1, val2);
        }
    }

    private static class JobRunDateDescendingComparator
            implements Comparator<JobRun>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 3672789840075706826L;

        public int compare(JobRun val1, JobRun val2) {
            return compareJobRunDateDescending(val1, val2);
        }
    }

    private static class RunCaseInsensitiveComparator
            implements Comparator<Run>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 414949285086649733L;

        public int compare(Run val1, Run val2) {
            return compareRun(val1, val2);
        }
    }

    private static class RunDescendingComparator
            implements Comparator<Run>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 8191789853614000953L;

        public int compare(Run val1, Run val2) {
            return compareRunDescending(val1, val2);
        }
    }

    private static class StageCaseInsensitiveComparator
            implements Comparator<Stage>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 4341786563640814257L;

        public int compare(Stage val1, Stage val2) {
            return compareStage(val1, val2);
        }
    }

    private static class StageAscendingComparator
            implements Comparator<Stage>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 5908227731873983860L;

        public int compare(Stage val1, Stage val2) {
            return compareStageAscending(val1, val2);
        }
    }

    private static class StageTestResultDateDescendingCaseInsensitiveComparator implements Comparator<StageTestResult>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 4582110728330687953L;

        public int compare(StageTestResult val1, StageTestResult val2) {
            return compareStageTestResultDateDescending(val1, val2);
        }
    }
    private static class StageTestResultCaseInsensitiveComparator
            implements Comparator<StageTestResult>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 3819926002811665135L;

        public int compare(StageTestResult val1, StageTestResult val2) {
            return compareStageTestResult(val1, val2);
        }
    }

    public static int compareStageTestResultDateDescending(StageTestResult val1, StageTestResult val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                compareTestResultDateDescending(val1.getTestResult(), val2.getTestResult()),
                compareStage(val1.getStage(), val2.getStage())
        );
    }

    public static int compareStageTestResult(StageTestResult val1, StageTestResult val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                compareStage(val1.getStage(), val2.getStage()),
                compareTestResult(val1.getTestResult(), val2.getTestResult())
        );
    }

    public static int compareCompany(Company val1, Company val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                compareLowerNullSafe(val1.getCompanyName(), val2.getCompanyName()),
                compareIntegers(val1.getCompanyId(), val2.getCompanyId())
        );
    }


    public static int compareOrg(Org val1, Org val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                compareLowerNullSafe(val1.getOrgName(), val2.getOrgName()),
                compareIntegers(val1.getOrgId(), val2.getOrgId())
        );
    }

    public static int compareRepo(Repo val1, Repo val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                compareIntegers(val1.getOrgFk(), val2.getOrgFk()),
                compareLowerNullSafe(val1.getRepoName(), val2.getRepoName()),
                compareIntegers(val1.getRepoId(), val2.getRepoId())
        );
    }

    public static int compareBranch(Branch val1, Branch val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                compareIntegers(val1.getRepoFk(), val2.getRepoFk()),
                compareLowerNullSafe(val1.getBranchName(), val2.getBranchName()),
                compareIntegers(val1.getBranchId(), val2.getBranchId())
        );
    }

    public static int compareJob(Job val1, Job val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                JsonCompare.compareTo(val1.getJobInfo(), val2.getJobInfo()),
                compareLowerNullSafe(val1.getJobInfo(), val2.getJobInfo()),
                compareLong(val1.getJobId(), val2.getJobId())
        );
    }

    public static int compareJobDateDescending(Job val1, Job val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                compareLocalDateTimeDescending(val1.getLastRun(), val2.getLastRun()),
                compareJob(val1, val2)
        );
    }

    public static int compareJobRunDateDescending(JobRun val1, JobRun val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                //compare run before job since it will be more recent
                compareRunDateDescending(val1.getRun(), val2.getRun()),
                compareJobDateDescending(val1.getJob(), val2.getJob())
        );
    }

    public static int compareLocalDateTimeDescending(LocalDateTime val1, LocalDateTime val2) {
        if (val1 == null || val2 == null) {
            //val2 then val1 since descending
            return ObjectUtils.compare(ObjectUtils.isEmpty(val2), ObjectUtils.isEmpty(val1));
        }
        return val2.compareTo(val1);
    }

    public static int compareRun(Run val1, Run val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                compareLong(val1.getJobFk(), val2.getJobFk()),
                ObjectUtils.compare(val1.getRunReference(), val2.getRunReference()),
                ObjectUtils.compare(val1.getRunId(), val2.getRunId()),
                ObjectUtils.compare(val1.getSha(), val2.getSha()),
                compareLong(val1.getRunId(), val2.getRunId())
        );
    }

    public static int compareRunDescending(Run val1, Run val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }

        //val2 then val 1 since descending
        return compareLong(val2.getRunId(), val1.getRunId());
    }

    public static int compareRunDateDescending(Run val1, Run val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                compareLocalDateTimeDescending(val1.getRunDate(), val2.getRunDate()),
                compareRun(val1, val2)
        );
    }

    public static int compareStage(Stage val1, Stage val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                compareLong(val1.getRunFk(), val2.getRunFk()),
                Long.compare(val1.getStageId(), val2.getStageId()),
                ObjectUtils.compare(val1.getStageName(), val2.getStageName()) //redundant
        );
    }

    public static int compareStageAscending(Stage val1, Stage val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return Long.compare(val1.getStageId(), val2.getStageId());

    }

    public static int compareStorage(Storage val1, Storage val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                compareLong(val1.getStageFk(), val2.getStageFk()),
                ObjectUtils.compare(val1.getLabel(), val2.getLabel()),
                compareLong(val1.getStorageId(), val2.getStorageId())
        );
    }

    public static int compareTestResult(TestResult val1, TestResult val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                compareLong(val1.getStageFk(), val2.getStageFk()),
                ObjectUtils.compare(val1.getTestResultId(), val2.getTestResultId())
        );
    }

    public static int compareTestResultDateDescending(TestResult val1, TestResult val2) {
        if (val1 == null || val2 == null) {
            return ObjectUtils.compare(ObjectUtils.isEmpty(val1), ObjectUtils.isEmpty(val2));
        }
        return chainCompare(
                compareLocalDateTimeDescending(val1.getTestResultCreated(), val2.getTestResultCreated()),
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

    private static class TestResultCaseInsensitiveComparator
            implements Comparator<TestResult>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = -859866670502292563L;

        public int compare(TestResult val1, TestResult val2) {
            return compareTestResult(val1, val2);
        }
    }

    private static class TestSuiteCaseInsensitiveComparator
            implements Comparator<TestSuite>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = -5672061856924641442L;

        public int compare(TestSuite val1, TestSuite val2) {
            return compareTestSuite(val1, val2);
        }
    }

    private static class TestCaseCaseInsensitiveComparator
            implements Comparator<TestCase>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = 5737556829091435969L;

        public int compare(TestCase val1, TestCase val2) {
            return compareTestCase(val1, val2);
        }
    }

    private static class StorageCaseInsensitiveComparator
            implements Comparator<Storage>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = -3674131088370959367L;

        public int compare(Storage val1, Storage val2) {
            return compareStorage(val1, val2);
        }
    }
    
    public final static int compareIntegers(Integer i1, Integer i2) {
        if (i1 == null) {
            if (i2 == null) {
                return 0;
            }
            return -1;
        } else if (i2 == null) {
            return 1;
        }
        return Integer.compare(i1, i2);
    }
}

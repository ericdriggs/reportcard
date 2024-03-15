/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db;


import io.github.ericdriggs.reportcard.gen.db.tables.Branch;
import io.github.ericdriggs.reportcard.gen.db.tables.Company;
import io.github.ericdriggs.reportcard.gen.db.tables.FaultContext;
import io.github.ericdriggs.reportcard.gen.db.tables.Job;
import io.github.ericdriggs.reportcard.gen.db.tables.Org;
import io.github.ericdriggs.reportcard.gen.db.tables.Repo;
import io.github.ericdriggs.reportcard.gen.db.tables.Run;
import io.github.ericdriggs.reportcard.gen.db.tables.Stage;
import io.github.ericdriggs.reportcard.gen.db.tables.Storage;
import io.github.ericdriggs.reportcard.gen.db.tables.StorageType;
import io.github.ericdriggs.reportcard.gen.db.tables.TestCase;
import io.github.ericdriggs.reportcard.gen.db.tables.TestCaseFault;
import io.github.ericdriggs.reportcard.gen.db.tables.TestResult;
import io.github.ericdriggs.reportcard.gen.db.tables.TestStatus;
import io.github.ericdriggs.reportcard.gen.db.tables.TestSuite;
import io.github.ericdriggs.reportcard.gen.db.tables.TestSuiteFault;

import java.util.Arrays;
import java.util.List;

import lombok.Generated;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Reportcard extends SchemaImpl {

    private static final long serialVersionUID = -1258354965;

    /**
     * The reference instance of <code>reportcard</code>
     */
    public static final Reportcard REPORTCARD = new Reportcard();

    /**
     * The table <code>reportcard.branch</code>.
     */
    public final Branch BRANCH = Branch.BRANCH;

    /**
     * The table <code>reportcard.company</code>.
     */
    public final Company COMPANY = Company.COMPANY;

    /**
     * The table <code>reportcard.fault_context</code>.
     */
    public final FaultContext FAULT_CONTEXT = FaultContext.FAULT_CONTEXT;

    /**
     * The table <code>reportcard.job</code>.
     */
    public final Job JOB = Job.JOB;

    /**
     * The table <code>reportcard.org</code>.
     */
    public final Org ORG = Org.ORG;

    /**
     * The table <code>reportcard.repo</code>.
     */
    public final Repo REPO = Repo.REPO;

    /**
     * The table <code>reportcard.run</code>.
     */
    public final Run RUN = Run.RUN;

    /**
     * The table <code>reportcard.stage</code>.
     */
    public final Stage STAGE = Stage.STAGE;

    /**
     * The table <code>reportcard.storage</code>.
     */
    public final Storage STORAGE = Storage.STORAGE;

    /**
     * The table <code>reportcard.storage_type</code>.
     */
    public final StorageType STORAGE_TYPE = StorageType.STORAGE_TYPE;

    /**
     * The table <code>reportcard.test_case</code>.
     */
    public final TestCase TEST_CASE = TestCase.TEST_CASE;

    /**
     * The table <code>reportcard.test_case_fault</code>.
     */
    public final TestCaseFault TEST_CASE_FAULT = TestCaseFault.TEST_CASE_FAULT;

    /**
     * The table <code>reportcard.test_result</code>.
     */
    public final TestResult TEST_RESULT = TestResult.TEST_RESULT;

    /**
     * The table <code>reportcard.test_status</code>.
     */
    public final TestStatus TEST_STATUS = TestStatus.TEST_STATUS;

    /**
     * The table <code>reportcard.test_suite</code>.
     */
    public final TestSuite TEST_SUITE = TestSuite.TEST_SUITE;

    /**
     * The table <code>reportcard.test_suite_fault</code>.
     */
    public final TestSuiteFault TEST_SUITE_FAULT = TestSuiteFault.TEST_SUITE_FAULT;

    /**
     * No further instances allowed
     */
    private Reportcard() {
        super("reportcard", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Branch.BRANCH,
            Company.COMPANY,
            FaultContext.FAULT_CONTEXT,
            Job.JOB,
            Org.ORG,
            Repo.REPO,
            Run.RUN,
            Stage.STAGE,
            Storage.STORAGE,
            StorageType.STORAGE_TYPE,
            TestCase.TEST_CASE,
            TestCaseFault.TEST_CASE_FAULT,
            TestResult.TEST_RESULT,
            TestStatus.TEST_STATUS,
            TestSuite.TEST_SUITE,
            TestSuiteFault.TEST_SUITE_FAULT
        );
    }
}

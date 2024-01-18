/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db;


import io.github.ericdriggs.reportcard.gen.db.tables.Branch;
import io.github.ericdriggs.reportcard.gen.db.tables.Company;
import io.github.ericdriggs.reportcard.gen.db.tables.Job;
import io.github.ericdriggs.reportcard.gen.db.tables.Org;
import io.github.ericdriggs.reportcard.gen.db.tables.Repo;
import io.github.ericdriggs.reportcard.gen.db.tables.Run;
import io.github.ericdriggs.reportcard.gen.db.tables.Stage;
import io.github.ericdriggs.reportcard.gen.db.tables.Storage;
import io.github.ericdriggs.reportcard.gen.db.tables.TestCase;
import io.github.ericdriggs.reportcard.gen.db.tables.TestResult;
import io.github.ericdriggs.reportcard.gen.db.tables.TestStatus;
import io.github.ericdriggs.reportcard.gen.db.tables.TestSuite;

import lombok.Generated;


/**
 * Convenience access to all tables in reportcard.
 */
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>reportcard.branch</code>.
     */
    public static final Branch BRANCH = Branch.BRANCH;

    /**
     * The table <code>reportcard.company</code>.
     */
    public static final Company COMPANY = Company.COMPANY;

    /**
     * The table <code>reportcard.job</code>.
     */
    public static final Job JOB = Job.JOB;

    /**
     * The table <code>reportcard.org</code>.
     */
    public static final Org ORG = Org.ORG;

    /**
     * The table <code>reportcard.repo</code>.
     */
    public static final Repo REPO = Repo.REPO;

    /**
     * The table <code>reportcard.run</code>.
     */
    public static final Run RUN = Run.RUN;

    /**
     * The table <code>reportcard.stage</code>.
     */
    public static final Stage STAGE = Stage.STAGE;

    /**
     * The table <code>reportcard.storage</code>.
     */
    public static final Storage STORAGE = Storage.STORAGE;

    /**
     * The table <code>reportcard.test_case</code>.
     */
    public static final TestCase TEST_CASE = TestCase.TEST_CASE;

    /**
     * The table <code>reportcard.test_result</code>.
     */
    public static final TestResult TEST_RESULT = TestResult.TEST_RESULT;

    /**
     * The table <code>reportcard.test_status</code>.
     */
    public static final TestStatus TEST_STATUS = TestStatus.TEST_STATUS;

    /**
     * The table <code>reportcard.test_suite</code>.
     */
    public static final TestSuite TEST_SUITE = TestSuite.TEST_SUITE;
}

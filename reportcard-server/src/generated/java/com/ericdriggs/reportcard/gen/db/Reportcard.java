/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.gen.db;


import com.ericdriggs.reportcard.gen.db.tables.Branch;
import com.ericdriggs.reportcard.gen.db.tables.Context;
import com.ericdriggs.reportcard.gen.db.tables.Execution;
import com.ericdriggs.reportcard.gen.db.tables.Org;
import com.ericdriggs.reportcard.gen.db.tables.Repo;
import com.ericdriggs.reportcard.gen.db.tables.Sha;
import com.ericdriggs.reportcard.gen.db.tables.Stage;
import com.ericdriggs.reportcard.gen.db.tables.TestCase;
import com.ericdriggs.reportcard.gen.db.tables.TestResult;
import com.ericdriggs.reportcard.gen.db.tables.TestStatus;
import com.ericdriggs.reportcard.gen.db.tables.TestSuite;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.14.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Reportcard extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>reportcard</code>
     */
    public static final Reportcard REPORTCARD = new Reportcard();

    /**
     * The table <code>reportcard.branch</code>.
     */
    public final Branch BRANCH = Branch.BRANCH;

    /**
     * The table <code>reportcard.context</code>.
     */
    public final Context CONTEXT = Context.CONTEXT;

    /**
     * The table <code>reportcard.execution</code>.
     */
    public final Execution EXECUTION = Execution.EXECUTION;

    /**
     * The table <code>reportcard.org</code>.
     */
    public final Org ORG = Org.ORG;

    /**
     * The table <code>reportcard.repo</code>.
     */
    public final Repo REPO = Repo.REPO;

    /**
     * The table <code>reportcard.sha</code>.
     */
    public final Sha SHA = Sha.SHA;

    /**
     * The table <code>reportcard.stage</code>.
     */
    public final Stage STAGE = Stage.STAGE;

    /**
     * The table <code>reportcard.test_case</code>.
     */
    public final TestCase TEST_CASE = TestCase.TEST_CASE;

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
        return Arrays.<Table<?>>asList(
            Branch.BRANCH,
            Context.CONTEXT,
            Execution.EXECUTION,
            Org.ORG,
            Repo.REPO,
            Sha.SHA,
            Stage.STAGE,
            TestCase.TEST_CASE,
            TestResult.TEST_RESULT,
            TestStatus.TEST_STATUS,
            TestSuite.TEST_SUITE);
    }
}
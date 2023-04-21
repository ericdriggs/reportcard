/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db;


import io.github.ericdriggs.reportcard.gen.db.tables.Branch;
import io.github.ericdriggs.reportcard.gen.db.tables.Context;
import io.github.ericdriggs.reportcard.gen.db.tables.Execution;
import io.github.ericdriggs.reportcard.gen.db.tables.Org;
import io.github.ericdriggs.reportcard.gen.db.tables.Repo;
import io.github.ericdriggs.reportcard.gen.db.tables.Sha;
import io.github.ericdriggs.reportcard.gen.db.tables.Stage;
import io.github.ericdriggs.reportcard.gen.db.tables.TestCase;
import io.github.ericdriggs.reportcard.gen.db.tables.TestResult;
import io.github.ericdriggs.reportcard.gen.db.tables.TestStatus;
import io.github.ericdriggs.reportcard.gen.db.tables.TestSuite;

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

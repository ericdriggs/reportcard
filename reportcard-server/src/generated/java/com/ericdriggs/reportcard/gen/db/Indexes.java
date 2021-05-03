/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.gen.db;


import com.ericdriggs.reportcard.gen.db.tables.Branch;
import com.ericdriggs.reportcard.gen.db.tables.Execution;
import com.ericdriggs.reportcard.gen.db.tables.Repo;
import com.ericdriggs.reportcard.gen.db.tables.Sha;
import com.ericdriggs.reportcard.gen.db.tables.Stage;
import com.ericdriggs.reportcard.gen.db.tables.TestCase;
import com.ericdriggs.reportcard.gen.db.tables.TestResult;
import com.ericdriggs.reportcard.gen.db.tables.TestSuite;

import javax.annotation.processing.Generated;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables in reportcard.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.14.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index BRANCH_BRANCH_REPO_IDX = Internal.createIndex(DSL.name("branch_repo_idx"), Branch.BRANCH, new OrderField[] { Branch.BRANCH.REPO_FK }, false);
    public static final Index SHA_BUILD_CREATED = Internal.createIndex(DSL.name("build_created"), Sha.SHA, new OrderField[] { Sha.SHA.SHA_CREATED }, false);
    public static final Index EXECUTION_EXECUTION_CONTEXT_FK_IDX = Internal.createIndex(DSL.name("execution_context_fk_idx"), Execution.EXECUTION, new OrderField[] { Execution.EXECUTION.CONTEXT_FK }, false);
    public static final Index TEST_CASE_FK_TEST_CASE_STATUS_IDX = Internal.createIndex(DSL.name("fk_test_case_status_idx"), TestCase.TEST_CASE, new OrderField[] { TestCase.TEST_CASE.TEST_STATUS_FK }, false);
    public static final Index TEST_CASE_FK_TEST_CASE_TEST_SUITE_IDX = Internal.createIndex(DSL.name("fk_test_case_test_suite_idx"), TestCase.TEST_CASE, new OrderField[] { TestCase.TEST_CASE.TEST_SUITE_FK }, false);
    public static final Index REPO_ORG_IDX = Internal.createIndex(DSL.name("org_idx"), Repo.REPO, new OrderField[] { Repo.REPO.ORG_FK }, false);
    public static final Index SHA_SHA_BRANCH_FK_IDX = Internal.createIndex(DSL.name("sha_branch_fk_idx"), Sha.SHA, new OrderField[] { Sha.SHA.BRANCH_FK }, false);
    public static final Index STAGE_STAGE_EXECUTION_FK_IDX = Internal.createIndex(DSL.name("stage_execution_fk_idx"), Stage.STAGE, new OrderField[] { Stage.STAGE.EXECUTION_FK }, false);
    public static final Index TEST_SUITE_TEST_RESULT_FK_IDX = Internal.createIndex(DSL.name("test_result_fk_idx"), TestSuite.TEST_SUITE, new OrderField[] { TestSuite.TEST_SUITE.TEST_RESULT_FK }, false);
    public static final Index TEST_RESULT_TEST_RESULT_STAGE_FK_IDX = Internal.createIndex(DSL.name("test_result_stage_fk_idx"), TestResult.TEST_RESULT, new OrderField[] { TestResult.TEST_RESULT.STAGE_FK }, false);
}

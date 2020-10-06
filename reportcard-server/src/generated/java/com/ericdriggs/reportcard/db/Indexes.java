/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.db;


import com.ericdriggs.reportcard.db.tables.App;
import com.ericdriggs.reportcard.db.tables.AppBranch;
import com.ericdriggs.reportcard.db.tables.Branch;
import com.ericdriggs.reportcard.db.tables.Build;
import com.ericdriggs.reportcard.db.tables.BuildStage;
import com.ericdriggs.reportcard.db.tables.Repo;
import com.ericdriggs.reportcard.db.tables.Stage;
import com.ericdriggs.reportcard.db.tables.TestCase;
import com.ericdriggs.reportcard.db.tables.TestResult;
import com.ericdriggs.reportcard.db.tables.TestSuite;

import javax.annotation.Generated;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables of the <code>reportcard</code> schema.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.13.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index APP_APP_REPO_IDX = Indexes0.APP_APP_REPO_IDX;
    public static final Index APP_BRANCH_APP_BRANCH_APP_FK_IDX = Indexes0.APP_BRANCH_APP_BRANCH_APP_FK_IDX;
    public static final Index APP_BRANCH_APP_BRANCH_BRANCH_FK_IDX = Indexes0.APP_BRANCH_APP_BRANCH_BRANCH_FK_IDX;
    public static final Index BRANCH_BRANCH_REPO_IDX = Indexes0.BRANCH_BRANCH_REPO_IDX;
    public static final Index BUILD_APP_IDX = Indexes0.BUILD_APP_IDX;
    public static final Index BUILD_BUILD_CREATED = Indexes0.BUILD_BUILD_CREATED;
    public static final Index BUILD_STAGE_BUILD_IDX = Indexes0.BUILD_STAGE_BUILD_IDX;
    public static final Index BUILD_STAGE_BUILD_STAGE_FK_STAGE_IDX = Indexes0.BUILD_STAGE_BUILD_STAGE_FK_STAGE_IDX;
    public static final Index REPO_ORG_IDX = Indexes0.REPO_ORG_IDX;
    public static final Index STAGE_STAGE_APP_BRANCH_IDX = Indexes0.STAGE_STAGE_APP_BRANCH_IDX;
    public static final Index TEST_CASE_FK_TEST_CASE_STATUS_IDX = Indexes0.TEST_CASE_FK_TEST_CASE_STATUS_IDX;
    public static final Index TEST_CASE_FK_TEST_CASE_TEST_SUITE_IDX = Indexes0.TEST_CASE_FK_TEST_CASE_TEST_SUITE_IDX;
    public static final Index TEST_RESULT_TEST_RESULT_FK_BUILD_STAGE_IDX = Indexes0.TEST_RESULT_TEST_RESULT_FK_BUILD_STAGE_IDX;
    public static final Index TEST_SUITE_TEST_RESULT_FK_IDX = Indexes0.TEST_SUITE_TEST_RESULT_FK_IDX;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 {
        public static Index APP_APP_REPO_IDX = Internal.createIndex("app_repo_idx", App.APP, new OrderField[] { App.APP.REPO_FK }, false);
        public static Index APP_BRANCH_APP_BRANCH_APP_FK_IDX = Internal.createIndex("app_branch_app_fk_idx", AppBranch.APP_BRANCH, new OrderField[] { AppBranch.APP_BRANCH.APP_FK }, false);
        public static Index APP_BRANCH_APP_BRANCH_BRANCH_FK_IDX = Internal.createIndex("app_branch_branch_fk_idx", AppBranch.APP_BRANCH, new OrderField[] { AppBranch.APP_BRANCH.BRANCH_FK }, false);
        public static Index BRANCH_BRANCH_REPO_IDX = Internal.createIndex("branch_repo_idx", Branch.BRANCH, new OrderField[] { Branch.BRANCH.REPO_FK }, false);
        public static Index BUILD_APP_IDX = Internal.createIndex("app_idx", Build.BUILD, new OrderField[] { Build.BUILD.APP_BRANCH_FK }, false);
        public static Index BUILD_BUILD_CREATED = Internal.createIndex("build_created", Build.BUILD, new OrderField[] { Build.BUILD.BUILD_CREATED }, false);
        public static Index BUILD_STAGE_BUILD_IDX = Internal.createIndex("build_idx", BuildStage.BUILD_STAGE, new OrderField[] { BuildStage.BUILD_STAGE.BUILD_FK }, false);
        public static Index BUILD_STAGE_BUILD_STAGE_FK_STAGE_IDX = Internal.createIndex("build_stage_fk_stage_idx", BuildStage.BUILD_STAGE, new OrderField[] { BuildStage.BUILD_STAGE.STAGE_FK }, false);
        public static Index REPO_ORG_IDX = Internal.createIndex("org_idx", Repo.REPO, new OrderField[] { Repo.REPO.ORG_FK }, false);
        public static Index STAGE_STAGE_APP_BRANCH_IDX = Internal.createIndex("stage_app_branch_idx", Stage.STAGE, new OrderField[] { Stage.STAGE.APP_BRANCH_FK }, false);
        public static Index TEST_CASE_FK_TEST_CASE_STATUS_IDX = Internal.createIndex("fk_test_case_status_idx", TestCase.TEST_CASE, new OrderField[] { TestCase.TEST_CASE.TEST_STATUS_FK }, false);
        public static Index TEST_CASE_FK_TEST_CASE_TEST_SUITE_IDX = Internal.createIndex("fk_test_case_test_suite_idx", TestCase.TEST_CASE, new OrderField[] { TestCase.TEST_CASE.TEST_SUITE_FK }, false);
        public static Index TEST_RESULT_TEST_RESULT_FK_BUILD_STAGE_IDX = Internal.createIndex("test_result_fk_build_stage_idx", TestResult.TEST_RESULT, new OrderField[] { TestResult.TEST_RESULT.BUILD_STAGE_FK }, false);
        public static Index TEST_SUITE_TEST_RESULT_FK_IDX = Internal.createIndex("test_result_fk_idx", TestSuite.TEST_SUITE, new OrderField[] { TestSuite.TEST_SUITE.TEST_RESULT_FK }, false);
    }
}

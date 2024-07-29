/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db;


import io.github.ericdriggs.reportcard.gen.db.tables.BranchTable;
import io.github.ericdriggs.reportcard.gen.db.tables.CompanyTable;
import io.github.ericdriggs.reportcard.gen.db.tables.FaultContextTable;
import io.github.ericdriggs.reportcard.gen.db.tables.JobTable;
import io.github.ericdriggs.reportcard.gen.db.tables.OrgTable;
import io.github.ericdriggs.reportcard.gen.db.tables.RepoTable;
import io.github.ericdriggs.reportcard.gen.db.tables.RunTable;
import io.github.ericdriggs.reportcard.gen.db.tables.StageTable;
import io.github.ericdriggs.reportcard.gen.db.tables.StorageTable;
import io.github.ericdriggs.reportcard.gen.db.tables.StorageTypeTable;
import io.github.ericdriggs.reportcard.gen.db.tables.TestCaseFaultTable;
import io.github.ericdriggs.reportcard.gen.db.tables.TestCaseTable;
import io.github.ericdriggs.reportcard.gen.db.tables.TestResultTable;
import io.github.ericdriggs.reportcard.gen.db.tables.TestStatusTable;
import io.github.ericdriggs.reportcard.gen.db.tables.TestSuiteTable;
import io.github.ericdriggs.reportcard.gen.db.tables.records.BranchRecord;
import io.github.ericdriggs.reportcard.gen.db.tables.records.CompanyRecord;
import io.github.ericdriggs.reportcard.gen.db.tables.records.FaultContextRecord;
import io.github.ericdriggs.reportcard.gen.db.tables.records.JobRecord;
import io.github.ericdriggs.reportcard.gen.db.tables.records.OrgRecord;
import io.github.ericdriggs.reportcard.gen.db.tables.records.RepoRecord;
import io.github.ericdriggs.reportcard.gen.db.tables.records.RunRecord;
import io.github.ericdriggs.reportcard.gen.db.tables.records.StageRecord;
import io.github.ericdriggs.reportcard.gen.db.tables.records.StorageRecord;
import io.github.ericdriggs.reportcard.gen.db.tables.records.StorageTypeRecord;
import io.github.ericdriggs.reportcard.gen.db.tables.records.TestCaseFaultRecord;
import io.github.ericdriggs.reportcard.gen.db.tables.records.TestCaseRecord;
import io.github.ericdriggs.reportcard.gen.db.tables.records.TestResultRecord;
import io.github.ericdriggs.reportcard.gen.db.tables.records.TestStatusRecord;
import io.github.ericdriggs.reportcard.gen.db.tables.records.TestSuiteRecord;

import lombok.Generated;

import org.jooq.ForeignKey;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in
 * reportcard.
 */
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<BranchRecord> KEY_BRANCH_PRIMARY = Internal.createUniqueKey(BranchTable.BRANCH, DSL.name("KEY_branch_PRIMARY"), new TableField[] { BranchTable.BRANCH.BRANCH_ID }, true);
    public static final UniqueKey<CompanyRecord> KEY_COMPANY_COMPANY_NAME_IDX = Internal.createUniqueKey(CompanyTable.COMPANY, DSL.name("KEY_company_company_name_idx"), new TableField[] { CompanyTable.COMPANY.COMPANY_NAME }, true);
    public static final UniqueKey<CompanyRecord> KEY_COMPANY_PRIMARY = Internal.createUniqueKey(CompanyTable.COMPANY, DSL.name("KEY_company_PRIMARY"), new TableField[] { CompanyTable.COMPANY.COMPANY_ID }, true);
    public static final UniqueKey<FaultContextRecord> KEY_FAULT_CONTEXT_PRIMARY = Internal.createUniqueKey(FaultContextTable.FAULT_CONTEXT, DSL.name("KEY_fault_context_PRIMARY"), new TableField[] { FaultContextTable.FAULT_CONTEXT.FAULT_CONTEXT_ID }, true);
    public static final UniqueKey<JobRecord> KEY_JOB_PRIMARY = Internal.createUniqueKey(JobTable.JOB, DSL.name("KEY_job_PRIMARY"), new TableField[] { JobTable.JOB.JOB_ID }, true);
    public static final UniqueKey<JobRecord> KEY_JOB_UQ_BRANCH_FK_JOB_INFO_STR = Internal.createUniqueKey(JobTable.JOB, DSL.name("KEY_job_UQ_BRANCH_FK_JOB_INFO_STR"), new TableField[] { JobTable.JOB.JOB_INFO_STR, JobTable.JOB.BRANCH_FK }, true);
    public static final UniqueKey<OrgRecord> KEY_ORG_ORG_NAME_IDX = Internal.createUniqueKey(OrgTable.ORG, DSL.name("KEY_org_org_name_idx"), new TableField[] { OrgTable.ORG.ORG_NAME }, true);
    public static final UniqueKey<OrgRecord> KEY_ORG_PRIMARY = Internal.createUniqueKey(OrgTable.ORG, DSL.name("KEY_org_PRIMARY"), new TableField[] { OrgTable.ORG.ORG_ID }, true);
    public static final UniqueKey<RepoRecord> KEY_REPO_PRIMARY = Internal.createUniqueKey(RepoTable.REPO, DSL.name("KEY_repo_PRIMARY"), new TableField[] { RepoTable.REPO.REPO_ID }, true);
    public static final UniqueKey<RepoRecord> KEY_REPO_REPO_NAME_IDX = Internal.createUniqueKey(RepoTable.REPO, DSL.name("KEY_repo_repo_name_idx"), new TableField[] { RepoTable.REPO.ORG_FK, RepoTable.REPO.REPO_NAME }, true);
    public static final UniqueKey<RunRecord> KEY_RUN_PRIMARY = Internal.createUniqueKey(RunTable.RUN, DSL.name("KEY_run_PRIMARY"), new TableField[] { RunTable.RUN.RUN_ID }, true);
    public static final UniqueKey<RunRecord> KEY_RUN_RUN_ID_UNIQUE = Internal.createUniqueKey(RunTable.RUN, DSL.name("KEY_run_run_id_unique"), new TableField[] { RunTable.RUN.RUN_ID }, true);
    public static final UniqueKey<RunRecord> KEY_RUN_UQ_RUN_JOB_REFERENCE = Internal.createUniqueKey(RunTable.RUN, DSL.name("KEY_run_uq_run_job_reference"), new TableField[] { RunTable.RUN.JOB_FK, RunTable.RUN.RUN_REFERENCE }, true);
    public static final UniqueKey<StageRecord> KEY_STAGE_PRIMARY = Internal.createUniqueKey(StageTable.STAGE, DSL.name("KEY_stage_PRIMARY"), new TableField[] { StageTable.STAGE.STAGE_ID }, true);
    public static final UniqueKey<StageRecord> KEY_STAGE_STAGE_ID_UNIQUE = Internal.createUniqueKey(StageTable.STAGE, DSL.name("KEY_stage_stage_id_unique"), new TableField[] { StageTable.STAGE.STAGE_ID }, true);
    public static final UniqueKey<StageRecord> KEY_STAGE_UQ_RUN_STAGE_NAME = Internal.createUniqueKey(StageTable.STAGE, DSL.name("KEY_stage_uq_run_stage_name"), new TableField[] { StageTable.STAGE.STAGE_NAME, StageTable.STAGE.RUN_FK }, true);
    public static final UniqueKey<StorageRecord> KEY_STORAGE_PRIMARY = Internal.createUniqueKey(StorageTable.STORAGE, DSL.name("KEY_storage_PRIMARY"), new TableField[] { StorageTable.STORAGE.STORAGE_ID }, true);
    public static final UniqueKey<StorageRecord> KEY_STORAGE_UQ_STABLE_LABEL = Internal.createUniqueKey(StorageTable.STORAGE, DSL.name("KEY_storage_UQ_STABLE_LABEL"), new TableField[] { StorageTable.STORAGE.STAGE_FK, StorageTable.STORAGE.LABEL }, true);
    public static final UniqueKey<StorageTypeRecord> KEY_STORAGE_TYPE_PRIMARY = Internal.createUniqueKey(StorageTypeTable.STORAGE_TYPE, DSL.name("KEY_storage_type_PRIMARY"), new TableField[] { StorageTypeTable.STORAGE_TYPE.STORAGE_TYPE_ID }, true);
    public static final UniqueKey<TestCaseRecord> KEY_TEST_CASE_PRIMARY = Internal.createUniqueKey(TestCaseTable.TEST_CASE, DSL.name("KEY_test_case_PRIMARY"), new TableField[] { TestCaseTable.TEST_CASE.TEST_CASE_ID }, true);
    public static final UniqueKey<TestCaseFaultRecord> KEY_TEST_CASE_FAULT_PRIMARY = Internal.createUniqueKey(TestCaseFaultTable.TEST_CASE_FAULT, DSL.name("KEY_test_case_fault_PRIMARY"), new TableField[] { TestCaseFaultTable.TEST_CASE_FAULT.TEST_CASE_FAULT_ID }, true);
    public static final UniqueKey<TestResultRecord> KEY_TEST_RESULT_PRIMARY = Internal.createUniqueKey(TestResultTable.TEST_RESULT, DSL.name("KEY_test_result_PRIMARY"), new TableField[] { TestResultTable.TEST_RESULT.TEST_RESULT_ID }, true);
    public static final UniqueKey<TestResultRecord> KEY_TEST_RESULT_STAGE_FK_UNIQUE = Internal.createUniqueKey(TestResultTable.TEST_RESULT, DSL.name("KEY_test_result_stage_fk_UNIQUE"), new TableField[] { TestResultTable.TEST_RESULT.STAGE_FK }, true);
    public static final UniqueKey<TestStatusRecord> KEY_TEST_STATUS_PRIMARY = Internal.createUniqueKey(TestStatusTable.TEST_STATUS, DSL.name("KEY_test_status_PRIMARY"), new TableField[] { TestStatusTable.TEST_STATUS.TEST_STATUS_ID }, true);
    public static final UniqueKey<TestSuiteRecord> KEY_TEST_SUITE_PRIMARY = Internal.createUniqueKey(TestSuiteTable.TEST_SUITE, DSL.name("KEY_test_suite_PRIMARY"), new TableField[] { TestSuiteTable.TEST_SUITE.TEST_SUITE_ID }, true);

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<BranchRecord, RepoRecord> BRANCH_REPO_FK = Internal.createForeignKey(BranchTable.BRANCH, DSL.name("branch_repo_fk"), new TableField[] { BranchTable.BRANCH.REPO_FK }, Keys.KEY_REPO_PRIMARY, new TableField[] { RepoTable.REPO.REPO_ID }, true);
    public static final ForeignKey<JobRecord, BranchRecord> FK_JOB_BRANCH = Internal.createForeignKey(JobTable.JOB, DSL.name("FK_JOB_BRANCH"), new TableField[] { JobTable.JOB.BRANCH_FK }, Keys.KEY_BRANCH_PRIMARY, new TableField[] { BranchTable.BRANCH.BRANCH_ID }, true);
    public static final ForeignKey<OrgRecord, CompanyRecord> FK_COMPANY_ORG = Internal.createForeignKey(OrgTable.ORG, DSL.name("FK_COMPANY_ORG"), new TableField[] { OrgTable.ORG.COMPANY_FK }, Keys.KEY_COMPANY_PRIMARY, new TableField[] { CompanyTable.COMPANY.COMPANY_ID }, true);
    public static final ForeignKey<RepoRecord, OrgRecord> REPO_ORG_FK = Internal.createForeignKey(RepoTable.REPO, DSL.name("repo_org_fk"), new TableField[] { RepoTable.REPO.ORG_FK }, Keys.KEY_ORG_PRIMARY, new TableField[] { OrgTable.ORG.ORG_ID }, true);
    public static final ForeignKey<RunRecord, JobRecord> RUN_JOB_FK = Internal.createForeignKey(RunTable.RUN, DSL.name("run_job_fk"), new TableField[] { RunTable.RUN.JOB_FK }, Keys.KEY_JOB_PRIMARY, new TableField[] { JobTable.JOB.JOB_ID }, true);
    public static final ForeignKey<StageRecord, RunRecord> STAGE_RUN_FK = Internal.createForeignKey(StageTable.STAGE, DSL.name("stage_run_fk"), new TableField[] { StageTable.STAGE.RUN_FK }, Keys.KEY_RUN_PRIMARY, new TableField[] { RunTable.RUN.RUN_ID }, true);
    public static final ForeignKey<StorageRecord, StageRecord> STAGE_FK = Internal.createForeignKey(StorageTable.STORAGE, DSL.name("stage_fk"), new TableField[] { StorageTable.STORAGE.STAGE_FK }, Keys.KEY_STAGE_PRIMARY, new TableField[] { StageTable.STAGE.STAGE_ID }, true);
    public static final ForeignKey<StorageRecord, StorageTypeRecord> STORAGE_TYPE_FK = Internal.createForeignKey(StorageTable.STORAGE, DSL.name("storage_type_fk"), new TableField[] { StorageTable.STORAGE.STORAGE_TYPE }, Keys.KEY_STORAGE_TYPE_PRIMARY, new TableField[] { StorageTypeTable.STORAGE_TYPE.STORAGE_TYPE_ID }, true);
    public static final ForeignKey<TestCaseRecord, TestStatusRecord> FK_TEST_CASE_TEST_STATUS = Internal.createForeignKey(TestCaseTable.TEST_CASE, DSL.name("fk_test_case_test_status"), new TableField[] { TestCaseTable.TEST_CASE.TEST_STATUS_FK }, Keys.KEY_TEST_STATUS_PRIMARY, new TableField[] { TestStatusTable.TEST_STATUS.TEST_STATUS_ID }, true);
    public static final ForeignKey<TestCaseRecord, TestSuiteRecord> FK_TEST_CASE_TEST_SUITE = Internal.createForeignKey(TestCaseTable.TEST_CASE, DSL.name("fk_test_case_test_suite"), new TableField[] { TestCaseTable.TEST_CASE.TEST_SUITE_FK }, Keys.KEY_TEST_SUITE_PRIMARY, new TableField[] { TestSuiteTable.TEST_SUITE.TEST_SUITE_ID }, true);
    public static final ForeignKey<TestCaseFaultRecord, FaultContextRecord> FK_FAULT_CONTEXT = Internal.createForeignKey(TestCaseFaultTable.TEST_CASE_FAULT, DSL.name("fk_fault_context"), new TableField[] { TestCaseFaultTable.TEST_CASE_FAULT.FAULT_CONTEXT_FK }, Keys.KEY_FAULT_CONTEXT_PRIMARY, new TableField[] { FaultContextTable.FAULT_CONTEXT.FAULT_CONTEXT_ID }, true);
    public static final ForeignKey<TestCaseFaultRecord, TestCaseRecord> FK_TEST_CASE = Internal.createForeignKey(TestCaseFaultTable.TEST_CASE_FAULT, DSL.name("fk_test_case"), new TableField[] { TestCaseFaultTable.TEST_CASE_FAULT.TEST_CASE_FK }, Keys.KEY_TEST_CASE_PRIMARY, new TableField[] { TestCaseTable.TEST_CASE.TEST_CASE_ID }, true);
    public static final ForeignKey<TestResultRecord, StageRecord> TEST_RESULT_STAGE_FK = Internal.createForeignKey(TestResultTable.TEST_RESULT, DSL.name("test_result_stage_fk"), new TableField[] { TestResultTable.TEST_RESULT.STAGE_FK }, Keys.KEY_STAGE_PRIMARY, new TableField[] { StageTable.STAGE.STAGE_ID }, true);
    public static final ForeignKey<TestSuiteRecord, TestResultRecord> TEST_RESULT_FK = Internal.createForeignKey(TestSuiteTable.TEST_SUITE, DSL.name("test_result_fk"), new TableField[] { TestSuiteTable.TEST_SUITE.TEST_RESULT_FK }, Keys.KEY_TEST_RESULT_PRIMARY, new TableField[] { TestResultTable.TEST_RESULT.TEST_RESULT_ID }, true);
}

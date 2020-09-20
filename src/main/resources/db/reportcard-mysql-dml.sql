INSERT IGNORE `reportcard`.`org`
    (`org_id`, `org_name`)
VALUES (1, 'default');

INSERT IGNORE `reportcard`.`repo`
    (`repo_id`, `repo_name`, `org_fk`)
VALUES (1, 'default', 1);

INSERT IGNORE `reportcard`.`app`
    (`app_id`, `app_name`, `repo_fk`)
VALUES (1, 'app1', 1);

INSERT IGNORE `reportcard`.`branch`
    (`branch_id`, `branch_name`, `repo_fk`)
VALUES (1, 'master', '1');

INSERT IGNORE `reportcard`.`app_branch`
    (`app_branch_id`, `app_fk`, `branch_fk`)
VALUES (1, 1, 1);

INSERT IGNORE `reportcard`.`build`
    (`build_id`, `app_branch_fk`, `build_unique_string`)
VALUES (1, 1, '9282be75-6ca5-424b-a7ec-13d13370ba90');

INSERT IGNORE `reportcard`.`stage`
    (`stage_id`, `stage_name`, `app_branch_fk`)
VALUES (1, 'unit', 1);

INSERT IGNORE `reportcard`.`build_stage`
    (`build_stage_id`, `build_fk`, `stage_fk`)
VALUES (1, 1, 1);

INSERT IGNORE `reportcard`.`test_status`
    (`test_status_id`, `test_status_name`)
VALUES (1, 'SUCCESS'),
       (2, 'SKIPPED'),
       (3, 'FAILURE'),
       (4, 'ERROR'),
       (5, 'FLAKY_FAILURE'),
       (6, 'RERUN_FAILURE'),
       (7, 'FLAKY_ERROR'),
       (8, 'RERUN_ERROR');

INSERT IGNORE `reportcard`.`test_result`
(`build_stage_fk`, `tests`, `skipped`, `error`, `failure`, `time`)
values (1, 70, 30, 10, 20, 3.300);

INSERT IGNORE `reportcard`.`test_suite`
(`test_result_fk`, `package`, `tests`, `skipped`, `error`, `failure`, `time`)
values (1, 'com.foo.baz', 8, 7, 5, 6, 1.010);

INSERT IGNORE `reportcard`.`test_case`
(`test_suite_fk`, `name`, `class_name`, `time`, `test_status_fk`)
values (1, 'testCaseName0', 'testCaseClassName0', 0.500, 2);

INSERT IGNORE `reportcard`.`test_case`
(`test_suite_fk`, `name`, `class_name`, `time`, `test_status_fk`)
values (1, 'testCaseName0', 'testCaseClassName1', 0.500, 0);

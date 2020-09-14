INSERT INTO `reportcard`.`org`
    (`org_id`, `org_name`)
VALUES (1, 'default');

INSERT INTO `reportcard`.`repo`
    (`repo_id`, `repo_name`, `org_fk`)
VALUES (1, 'default', 1);

INSERT INTO `reportcard`.`app`
    (`app_id`, `app_name`, `repo_fk`)
VALUES (1, 'app1', 1);

INSERT INTO `reportcard`.`branch`
    (`branch_id`, `branch_name`, `repo_fk`)
VALUES (1, 'master', '1');

INSERT INTO `reportcard`.`app_branch`
    (`app_branch_id`, `app_fk`, `branch_fk`)
VALUES (1, 1, 1);

INSERT INTO `reportcard`.`build`
    (`build_id`, `app_branch_fk`, `build_unique_string`)
VALUES (1, 1, '9282be75-6ca5-424b-a7ec-13d13370ba90');

INSERT INTO `reportcard`.`stage`
    (`stage_id`, `stage_name`, `app_branch_fk`)
VALUES (1, 'unit', 1);

INSERT INTO `reportcard`.`build_stage`
    (`build_stage_id`, `build_fk`, `stage_fk`)
VALUES (1, 1, 1);

INSERT INTO `reportcard`.`test_status`
    (`test_status_id`, `test_status_name`)
VALUES (0, 'SUCCESS'),
       (1, 'SKIPPED'),
       (2, 'FAILURE'),
       (3, 'ERROR');


insert into `reportcard`.`test_result`
(`build_stage_fk`, `tests`, `skipped`, `error`, `failure`, `time`)
values (1, 70, 30, 10, 20, 3.300);

insert into `reportcard`.`test_suite`
(`test_result_fk`, `package`, `tests`, `skipped`, `error`, `failure`, `time`)
values (1, 'com.foo.baz', 8, 7, 5, 6, 1.010);

insert into `reportcard`.`test_case`
(`test_suite_fk`, `name`, `class_name`, `time`, `test_status_fk`)
values (1, 'testCaseName0', 'testCaseClassName0', 0.500, 2);

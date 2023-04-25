USE reportcard;


INSERT `reportcard`.`org`
    (`org_id`, `org_name`)
VALUES (1, 'org1');

INSERT `reportcard`.`repo`
    (`repo_id`, `repo_name`, `org_fk`)
VALUES (1, 'repo1', 1);

INSERT `reportcard`.`branch`
    (`branch_id`, `branch_name`, `repo_fk`)
VALUES (1, 'master', '1 ');

INSERT INTO `reportcard`.`sha`
    (`sha_id`, `sha`, `repo_fk`)
VALUES (1, 'bdd15b6fae26738ca58f0b300fc43f5872b429bf', 1);

INSERT INTO `reportcard`.`context`
(`context_id`,
 `sha_fk`,
 `branch_fk`,
 `metadata`)
VALUES (1, 1, 1, '{ "application":"fooapp", "host": "foocorp.jenkins.com", "pipeline": "foopipeline" }');

INSERT INTO `reportcard`.`execution`
(`execution_id`,
 `execution_reference`,
 `context_fk`)
VALUES (1, "executionReference1", 1);


INSERT INTO `reportcard`.`stage`
(`stage_id`,
 `stage_name`,
 `execution_fk`)
VALUES (1, 'api', 1);

INSERT `reportcard`.`test_status`
    (`test_status_id`, `test_status_name`)
VALUES (1, 'SUCCESS'),
       (2, 'SKIPPED'),
       (3, 'FAILURE'),
       (4, 'ERROR'),
       (5, 'FLAKY_FAILURE'),
       (6, 'RERUN_FAILURE'),
       (7, 'FLAKY_ERROR'),
       (8, 'RERUN_ERROR');

INSERT INTO `reportcard`.`test_result`
(`test_result_id`,
 `stage_fk`,
 `tests`,
 `skipped`,
 `error`,
 `failure`,
 `time`)
VALUES (1, --       <{test_result_id: }>
        1, --       <{stage_fk: }>
        70, --       <{tests: }>
        30, --       <{skipped: }>,
        10, --       <{error: }>,
        20, --       <{failure: }>,
        3.300); --   <{time: }>,

INSERT `reportcard`.`test_suite`
(`test_result_fk`, `package`, `tests`, `skipped`, `error`, `failure`, `time`)
values (1, 'com.foo.baz', 8, 7, 5, 6, 1.010);

INSERT `reportcard`.`test_case`
    (`test_suite_fk`, `name`, `class_name`, `time`, `test_status_fk`)
values (1, 'testCaseName1', 'testCaseClassName1', 0.500, 1);

INSERT `reportcard`.`test_case`
    (`test_suite_fk`, `name`, `class_name`, `time`, `test_status_fk`)
values (1, 'testCaseName2', 'testCaseClassName2', 0.500, 3);


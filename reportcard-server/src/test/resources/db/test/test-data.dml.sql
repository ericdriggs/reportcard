USE reportcard;

INSERT `reportcard`.`company`
(`company_id`, `company_name`)
VALUES (1, 'company1');

INSERT `reportcard`.`org`
    (`org_id`, `org_name`, `company_fk`)
VALUES (1, 'org1', 1);

INSERT `reportcard`.`repo`
    (`repo_id`, `repo_name`, `org_fk`)
VALUES (1, 'repo1', 1);

INSERT `reportcard`.`branch`
    (`branch_id`, `branch_name`, `repo_fk`)
VALUES (1, 'master', '1 ');


INSERT INTO `reportcard`.`job`
(`job_id`, `branch_fk`, `job_info`)
VALUES (1, 1, '{ "application":"fooapp", "host": "foocorp.jenkins.com", "pipeline": "foopipeline" }');


INSERT INTO `reportcard`.`run`
(`run_id`,
 `run_reference`,
 `job_fk`,
 `sha`,
 `job_run_count`,
 `is_success`
 )
VALUES (1, "runReference1", 1, 'bdd15b6fae26738ca58f0b300fc43f5872b429bf', 1, 1 );

INSERT INTO `reportcard`.`stage`
(`stage_id`,
 `stage_name`,
 `run_fk`)
VALUES (1, 'api', 1);



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

INSERT INTO `reportcard`.`test_suite`
(`test_suite_id`,
 `test_result_fk`,
 `name`,
 `tests`,
 `skipped`,
 `error`,
 `failure`,
 `time`,
 `package`,
 `group`,
 `properties`)
VALUES (1, --                  <{test_suite_id: }>,
        1, --                  <{test_result_fk: }>,
        'testSuiteName1', --   <{name: }>,
        8, --                  <{tests: }>,
        7, --                  <{skipped: }>,
        6, --                  <{error: }>,
        0, --                  <{failure: }>,
        1.010, --              <{time: }>,
        'com.foo.baz', --      <{package: }>,
        null, --               <{group: }>,
        null --                <{properties: }>,
       );

INSERT `reportcard`.`test_case`
    (`test_suite_fk`, `name`, `class_name`, `time`, `test_status_fk`)
values (1, 'testCaseName1', 'testCaseClassName1', 0.500, 1);

INSERT `reportcard`.`test_case`
    (`test_suite_fk`, `name`, `class_name`, `time`, `test_status_fk`)
values (1, 'testCaseName2', 'testCaseClassName2', 0.500, 3);

insert into `reportcard`.`storage` (`stage_fk`, `label`, `prefix`, `index_file`)
values (1, 'abcLabel', '/rc/company1/org1/repo1/master/2024-01-22/abcRSr4kRco5b8HqUhSUGg/1/abc15b6fae26738ca58f0b300fc43f5872b429bf/api', 'classpath:html-samples/foo/index.html')

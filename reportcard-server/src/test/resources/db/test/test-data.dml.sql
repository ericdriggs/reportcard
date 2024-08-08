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
VALUES (1, 'aaaaaaaa-2222-bbbb-cccc-dddddddddddd', 1, 'bdd15b6fae26738ca58f0b300fc43f5872b429bf', 1, 1 );

INSERT INTO `reportcard`.`stage`
(`stage_id`,
 `stage_name`,
 `run_fk`
)
VALUES (1,
        'api',
        1);



INSERT INTO `reportcard`.`test_result`
(`test_result_id`,
 `stage_fk`,
 `tests`,
 `skipped`,
 `error`,
 `failure`,
 `time`,
 `test_suites_json`

)
VALUES (1, --       <{test_result_id: }>
        1, --       <{stage_fk: }>
        70, --       <{tests: }>
        30, --       <{skipped: }>,
        10, --       <{error: }>,
        20, --       <{failure: }>,
        3.300, --   <{time: }>,
        '[{"name":"testSuiteName1","tests":8,"skipped":7,"error":6,"failure":0,"time":1.010,"packageName":"com.foo.baz","testCases":[{"name":"testCaseName1","className":"testCaseClassName1","time":0.500,"testStatusFk":1,"testStatus":"SUCCESS"},{"name":"testCaseName1","className":"testCaseClassName1","time":0.500,"testStatusFk":3,"testStatus":"FAILURE","testCaseFaults":[{"faultContextFk":2,"type":"fooType","message":"fooMessage","value":"fooMessage","faultContext":"FAILURE"}]}]}]'
       );

insert into `reportcard`.`storage` (`stage_fk`, `label`, `prefix`, `index_file`)
values (1, 'abcLabel', '/rc/company1/org1/repo1/master/2024-01-22/abcRSr4kRco5b8HqUhSUGg/1/abc15b6fae26738ca58f0b300fc43f5872b429bf/api', 'classpath:html-samples/foo/index.html')

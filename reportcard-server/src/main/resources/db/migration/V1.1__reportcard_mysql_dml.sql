USE reportcard;

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


INSERT `reportcard`.`storage_type`
(`storage_type_id`, `storage_type_name`)
VALUES (1, 'HTML'),
       (2, 'JSON'),
       (3, 'LOG'),
       (4, 'OTHER'),
       (5, 'XML');
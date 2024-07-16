-- leave stage by delete everything underneath it
-- used for dups

use reportcard;
SET @stageId = 2212;

-- delete
select *
from test_case_fault
where test_case_fk in (
    select test_case_id from test_case
    where test_suite_fk in (
          select test_suite_id from test_suite
          where test_result_fk in (
                select test_result_id from test_result
                where stage_fk = @stageId
          )
    )
);

-- delete
select *
from test_case
where test_suite_fk in (
    select test_suite_id from test_suite
    where test_result_fk in (
        select test_result_id from test_result
        where test_result_fk in (
            select test_result_id from test_result
            where stage_fk = @stageId
        )
    )
);

-- delete
select *
from test_suite
where test_result_fk in (
    select test_result_id from test_result
    where stage_fk = @stageId
);

-- delete
select *
from test_result
where stage_fk = @stageId;

-- delete
select *
from storage
where stage_fk = @stageId







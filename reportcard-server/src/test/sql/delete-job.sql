-- delete tree for job_id

use reportcard;
SET @jobId = 1;

delete from test_case_fault
where test_case_fk in (
    select test_case_id from test_case
    where test_suite_fk in
          (
              select test_suite_id from test_suite
              where test_result_fk in
                    (
                        select test_result_id from test_result
                        where stage_fk in (
                            select stage_id from stage
                            where run_fk in
                                  (
                                      select run_id from run
                                      where job_fk = @jobId
                                  )
                        )
                    )
          )
);

delete from test_case
where test_suite_fk in
      (
          select test_suite_id from test_suite
          where test_result_fk in
                (
                    select test_result_id from test_result
                    where stage_fk in (
                        select stage_id from stage
                        where run_fk in
                              (
                                  select run_id from run
                                  where job_fk = @jobId
                              )
                    )
                )
      );

delete from test_suite
where test_result_fk in
      (
          select test_result_id from test_result
          where stage_fk in (
              select stage_id from stage
              where run_fk in
                    (
                        select run_id from run
                        where job_fk = @jobId
                    )
          )
      );


delete from test_result
where stage_fk in (
    select stage_id from stage
    where run_fk in
          (
              select run_id from run
              where job_fk = @jobId
          )
);



delete from storage
where stage_fk in
      (
          select stage_id from stage
          where run_fk in
                (
                    select run_id from run
                    where job_fk = @jobId
                )
      );

delete from stage
where run_fk in
      (
          select run_id from run
          where job_fk = @jobId
      );

delete from run
where job_fk = @jobId;

delete from job
where job_id = @jobId;









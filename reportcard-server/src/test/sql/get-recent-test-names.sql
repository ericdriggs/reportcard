CREATE TEMPORARY TABLE temp_latest_runs AS
SELECT
    c.company_name,
    o.org_name,
    r.repo_name,
    tr.test_suites_json
FROM reportcard.company c
JOIN reportcard.org o ON c.company_id = o.company_fk
JOIN reportcard.repo r ON o.org_id = r.org_fk
JOIN reportcard.branch b ON r.repo_id = b.repo_fk
JOIN reportcard.job j ON b.branch_id = j.branch_fk
JOIN reportcard.run run ON j.job_id = run.job_fk
JOIN reportcard.stage s ON run.run_id = s.run_fk
JOIN reportcard.test_result tr ON s.stage_id = tr.stage_fk
WHERE run.run_date >= DATE_SUB(NOW(), INTERVAL 3 DAY)
  AND run.run_date = (
    SELECT MAX(r2.run_date)
    FROM reportcard.run r2
    WHERE r2.job_fk = j.job_id
      AND r2.run_date >= DATE_SUB(NOW(), INTERVAL 3 DAY)
  );

SELECT DISTINCT
    t.company_name,
    t.org_name,
    t.repo_name,
    JSON_UNQUOTE(JSON_EXTRACT(tc_data.value, '$.name')) AS test_name
FROM temp_latest_runs t
CROSS JOIN JSON_TABLE(
    t.test_suites_json,
    '$[*].testCases[*]' COLUMNS (
        value JSON PATH '$'
    )
) AS tc_data
WHERE JSON_UNQUOTE(JSON_EXTRACT(tc_data.value, '$.testStatusFk')) != 2;

DROP TEMPORARY TABLE temp_latest_runs;

-- Add timing columns to test_result table
-- Run manually on deployed databases
-- ALGORITHM=INPLACE, LOCK=NONE allows concurrent reads/writes during ALTER

ALTER TABLE test_result ADD COLUMN start_time DATETIME NULL AFTER time, ALGORITHM=INPLACE, LOCK=NONE;
ALTER TABLE test_result ADD COLUMN end_time DATETIME NULL AFTER start_time, ALGORITHM=INPLACE, LOCK=NONE;

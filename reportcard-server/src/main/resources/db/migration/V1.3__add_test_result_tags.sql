-- Add tags JSON column to test_result table for tag-based search
-- Run manually on deployed databases (project does not use Flyway auto-migration)
-- ALGORITHM=INPLACE, LOCK=NONE allows concurrent reads/writes during ALTER

-- Step 1: Add tags column
ALTER TABLE test_result
  ADD COLUMN tags JSON NULL DEFAULT NULL
  COMMENT 'Flattened array of all feature and scenario tags, deduplicated, for MEMBER OF queries'
  ALGORITHM=INPLACE, LOCK=NONE;

-- Step 2: Create multi-value index for tag search
-- Index uses VARCHAR(100) ARRAY - each tag element indexed individually
-- IMPORTANT: OR queries must use UNION (single WHERE with OR does NOT use index)
-- IMPORTANT: ALGORITHM=COPY required for multi-value indexes (table rebuild)
CREATE INDEX idx_test_result_tags ON test_result (
    (CAST(tags->'$[*]' AS VARCHAR(100) ARRAY))
) COMMENT 'Multi-value index enables MEMBER OF queries on tag names';

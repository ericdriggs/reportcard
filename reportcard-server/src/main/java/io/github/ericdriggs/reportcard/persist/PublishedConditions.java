package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.gen.db.tables.StageTable;
import io.github.ericdriggs.reportcard.gen.db.tables.TestResultTable;
import org.jooq.Condition;

import static io.github.ericdriggs.reportcard.gen.db.Tables.*;
import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.selectOne;

/**
 * A stage is published once it has a test result; a run is published once any of its stages is.
 * Aliases keep these correlated subqueries distinct from STAGE and TEST_RESULT in the outer query.
 */
public final class PublishedConditions {

    private static final StageTable PUBLISHED_STAGE = STAGE.as("published_stage");
    private static final TestResultTable PUBLISHED_TEST_RESULT = TEST_RESULT.as("published_test_result");

    private PublishedConditions() {
    }

    public static Condition stageHasTestResult() {
        return exists(selectOne()
                .from(PUBLISHED_TEST_RESULT)
                .where(PUBLISHED_TEST_RESULT.STAGE_FK.eq(STAGE.STAGE_ID)));
    }

    public static Condition runHasTestResult() {
        return exists(selectOne()
                .from(PUBLISHED_STAGE)
                .join(PUBLISHED_TEST_RESULT).on(PUBLISHED_TEST_RESULT.STAGE_FK.eq(PUBLISHED_STAGE.STAGE_ID))
                .where(PUBLISHED_STAGE.RUN_FK.eq(RUN.RUN_ID)));
    }
}

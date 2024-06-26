package jooq;

import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StagePojo;
import io.github.ericdriggs.reportcard.gen.db.tables.records.StageRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RecordIntoClassTest {
    @Test
    void givenRecordWithNoValues_WhenIntoClass_ThenNullFields() {

        StageRecord record = new StageRecord();
        StagePojo stage = record.into(StagePojo.class);
        assertNotNull(stage);
        assertNull(stage.getStageId());
    }

    @Test
    void givenRecordWithValues_WhenIntoClass_ThenWhat() {
        final long stageId = 1L;
        final long runFk = 2L;
        final String stageName = "stageName";
        StageRecord record = new StageRecord();
        record.setStageId(stageId);
        record.setStageName(stageName);
        record.setRunFk(runFk);

        StagePojo stage = record.into(StagePojo.class);
        assertNotNull(stage);
        assertEquals(stageId, stage.getStageId());
        assertEquals(stageName, stage.getStageName());
        assertEquals(runFk, stage.getRunFk());
    }
}

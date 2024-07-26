package io.github.ericdriggs.reportcard.persist;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.ericdriggs.reportcard.gen.db.TestData;
import io.github.ericdriggs.reportcard.model.graph.*;
import io.github.ericdriggs.reportcard.model.trend.CompanyOrgRepoBranchJobStageName;
import io.github.ericdriggs.reportcard.model.trend.JobStageTestTrend;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.jooq.Query;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class LockServiceTest extends AbstractLockServiceTest {
    @Autowired
    public LockServiceTest(LockService lockService) {
        super(lockService);
    }

    @Test
    void uuidFromStringValidTest() {
        final UUID uuid = UUID.randomUUID();
        final UUID parsedUuuid = UUID.fromString(uuid.toString());
        assertEquals(uuid, parsedUuuid);
    }

    //using UUIDs prevents sql injection
    @Test
    void uuidFromStringInvalidTest() {
        final String notUuid = "Robert'); DROP TABLE Students;--";
        assertThrows(RuntimeException.class, () -> {
            UUID.fromString(notUuid);
        });
    }

    @Test
    void getLockReleaseLockTest() throws TimeoutException, InterruptedException {
        final UUID uuid = UUID.randomUUID();
        Query query = lockService.getDsl().startTransaction();

        Configuration queryConfig = query.configuration();
        assertNotNull(queryConfig);
        assertNotNull(queryConfig.dsl());
//        Query query = dsl.startTransaction();
//        dsl.rollback();
        assertFalse(lockService.selectIsUsedLock(uuid, queryConfig));
        assertTrue(lockService.selectIsFreeLock(uuid, queryConfig));

        lockService.getLock(uuid, queryConfig);
        assertTrue(lockService.selectIsUsedLock(uuid, queryConfig));
        assertFalse(lockService.selectIsFreeLock(uuid, queryConfig));

        lockService.releaseLock(uuid, queryConfig);
        assertFalse(lockService.selectIsUsedLock(uuid, queryConfig));
        assertTrue(lockService.selectIsFreeLock(uuid, queryConfig));
        queryConfig.dsl().commit();
    }
}

package io.github.ericdriggs.reportcard.persist;

import org.jooq.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * <p>Database locking service based on UUIDs.</p>
 * <p>Clients must call releaseLock after getLock, but locks will auto-expire after WAIT_LOCK_TIMEOUT_DURATION</p>
 * <p>Note: get_lock can be acquired multiple times within the same session so there's a possibility of a race condition
 * if multiple get_lock requests occur within the same session.</p>
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.4/en/locking-functions.html#function_get-lock">https://dev.mysql.com/doc/refman/8.4/en/locking-functions.html#function_get-lock</a>
 */

@Service
@SuppressWarnings({"unused", "ConstantConditions", "DuplicatedCode"})
public class LockService extends AbstractPersistService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    final static int MAX_LOCK_SECONDS = 60;
    final static Duration WAIT_LOCK_TIMEOUT_DURATION = Duration.ofSeconds(30);
    final static Duration POLL_SLEEP_DURATION = Duration.ofSeconds(5);

    @Autowired
    public LockService(DSLContext dsl) {
        super(dsl);

    }

    public void lockedTransaction() {

    }
    /**
     * Get the lock for UUID or throw
     * @param uuid a uuid
     * @throws TimeoutException if unable to get lock within timeout duration
     */
    public void getLock(UUID uuid, Configuration configuration) throws TimeoutException {
        showConnectionId("getLock before", configuration);
        Query query = configuration.dsl().startTransaction();

        pollIsLockAvailable(uuid, configuration);
        final boolean gotLock = selectGetLock(uuid, configuration);
        if (!gotLock) {
            throw new TimeoutException("getLock failed to get lock: " + uuid);
        }
        showConnectionId("getLock after", configuration);
    }

    public void releaseLock(UUID uuid, Configuration configuration) {
        final boolean releasedLock = selectReleaseLock(uuid, configuration);
        if (!releasedLock) {
            throw new IllegalStateException("releaseLock failed to release lock: " + uuid);
        }
    }

    //requiring UUID for lock prevents possibility of injection
    boolean selectGetLock(UUID uuid, Configuration configuration) {
        Integer result = configuration.dsl().fetchOne("SELECT GET_LOCK('" + uuid.toString() + "', " + MAX_LOCK_SECONDS + ")").into(Integer.class);
        return result == 1;
    }

    boolean selectIsFreeLock(UUID uuid, Configuration configuration) {
        showConnectionId("selectIsFreeLock before", configuration);
        Integer result = configuration.dsl().fetchOne("SELECT IS_FREE_LOCK('" + uuid.toString() + "')").into(Integer.class);
        showConnectionId("selectIsFreeLock before", configuration);
        return result == 1;
    }

    boolean selectIsUsedLock(UUID uuid, Configuration configuration) {
        showConnectionId("selectIsUsedLock before", configuration);

        Integer result = configuration.dsl().fetchOne("SELECT IS_USED_LOCK('" + uuid.toString() + "')").into(Integer.class);
        showConnectionId("selectIsUsedLock after", configuration);
        return result != null;
    }

    void showConnectionId(String text, Configuration configuration) {
        Long connectionId = configuration.dsl().fetchOne("select connection_id();").into(Long.class);
        log.info("connectionId: {}, text: {}", connectionId, text);
    }

    boolean selectReleaseLock(UUID uuid, Configuration configuration) {
        Integer result = configuration.dsl().fetchOne("SELECT RELEASE_LOCK('" + uuid.toString() + "')").into(Integer.class);
        return result == 1;
    }


    void pollIsLockAvailable(UUID uuid, Configuration configuration) throws TimeoutException {
        Instant timeout = Instant.now().plus(WAIT_LOCK_TIMEOUT_DURATION);
        while (Instant.now().isBefore(timeout)) {
            if (selectIsFreeLock(uuid, configuration)) {
                return;
            }
            try {
                //noinspection BusyWait
                Thread.sleep(POLL_SLEEP_DURATION.toMillis());
            } catch (InterruptedException e) {
                log.warn(e.getMessage());
            }
        }
        throw new TimeoutException("pollIsLockAvailable timeout for uuid: " + uuid);
    }
}
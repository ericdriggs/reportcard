package io.github.ericdriggs.reportcard.lock;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;

import java.sql.Connection;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * A callable which only performs its operation if able to obtain a database lock.
 * If the operation is performed, the result is stored and can be retrieved later using getResult()
 */
@Slf4j
public abstract class AbstractDatabaseLockCallable<V> implements Callable<V> {

    final int randomId = new Random().nextInt();
    final Configuration configuration;
    final Duration pollTimeoutDuration;
    final int getLockTimeoutSeconds;
    final long pollSleepMillis;
    final UUID uuid;

    public AbstractDatabaseLockCallable(
            final Configuration configuration,
            final Duration pollTimeoutDuration,
            final int getLockTimeoutSeconds,
            final long pollSleepMillis,
            final UUID uuid
    ) {
        this.configuration = configuration;
        this.pollTimeoutDuration = pollTimeoutDuration;
        this.getLockTimeoutSeconds = getLockTimeoutSeconds;
        this.pollSleepMillis = pollSleepMillis;
        this.uuid = uuid;

    }

    @Override
    public final V call() throws Exception {
        return pollCriticalSectionCall();
    }

    /**
     * the implementation of the operation to perform inside of the critical section
     * @return V
     * @throws Exception if operation throws exception
     */
    public abstract V doCall() throws Exception;

    /**
     * Performs operation if able to obtain lock.
     * Will poll on attempts to obtain lock, but will not retry operation if it throws.
     *
     * @return V if able to obtain lock and perform operation
     * @throws LockNotAvailableException if unable to obtain lock or doCall throws
     */
    V pollCriticalSectionCall() throws Exception {
        Instant timeout = Instant.now().plus(pollTimeoutDuration);
        while (Instant.now().isBefore(timeout)) {
            try {
                return criticalSectionCall();
            } catch (LockNotAvailableException e) {
                try {
                    //noinspection BusyWait
                    log.trace("waiting on critical section for uuid: " + uuid + ", randomId: " + randomId);
                    Thread.sleep(pollSleepMillis);
                } catch (InterruptedException interruptedException) {
                    //NO-OP
                }
            }
        }
        throw new LockNotAvailableException(uuid);
    }

    /**
     * Attempts to obtain lock and perform operation in the context of a new lambda transaction.
     *
     * @return V if able to perform operation.
     * @throws LockNotAvailableException if unable to obtain lock for critical section within getLockTimeoutSeconds
     */
    synchronized V criticalSectionCall() throws Exception {

        try (Connection connection = configuration.connectionProvider().acquire()) {
            boolean haveLock = MysqlDatabaseLockUtil.getLockOrFalse(uuid, connection, getLockTimeoutSeconds);
            if (!haveLock) {
                throw new LockNotAvailableException(uuid);
            }
            V result = doCall();
            MysqlDatabaseLockUtil.releaseLock(uuid, connection);
            return result;
        }
    }

}

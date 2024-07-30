package io.github.ericdriggs.reportcard.lock;

import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Slf4j
public class BlockedDatabaseLockResultCallable extends AbstractDatabaseLockCallable<String> {
    private boolean isBlocked = true;
    private final int threadId;
    private final static long blockedSleepMillis = 50;
    private final static Duration blockedTimeoutDuration = Duration.ofSeconds(5);
    private final static Duration pollTimeoutDuration = Duration.ofSeconds(10);
    private final static int getLockTimeoutSeconds = 1;
    private final static long pollSleepMillis = 50;

    private String result;

    public void unblock() {
        this.isBlocked = false;
    }

    public boolean getIsBlocked() {
        return isBlocked;
    }

    public BlockedDatabaseLockResultCallable(Configuration outerConfiguration, UUID uuid, int threadId) {
        super(outerConfiguration, pollTimeoutDuration, getLockTimeoutSeconds, pollSleepMillis, uuid);
        this.threadId = threadId;
    }

    @Override
    public synchronized String doCall() throws Exception {
        Instant timeout = Instant.now().plus(blockedTimeoutDuration);
        int count = 0;
        while (isBlocked) {
            try {
                //noinspection BusyWait
                count++;
                log.info("threadId: {} blocked. sleep #: {} ", threadId, count);
                Thread.sleep(blockedSleepMillis);
            } catch (InterruptedException e) {
                //NO-OP;
            }
            if (Instant.now().isAfter(timeout)) {
                throw new TimeoutException("blockedFunction timeout");
            }
        }
        log.info("threadId: {} unblocked", threadId);
        this.result = "result-" + uuid;
        return result;
    }

    public String getResult() {
        return result;
    }
}

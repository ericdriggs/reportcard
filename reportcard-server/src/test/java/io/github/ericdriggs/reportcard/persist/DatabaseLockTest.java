package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.ReportcardApplication;
import io.github.ericdriggs.reportcard.lock.BlockedDatabaseLockCallable;
import io.github.ericdriggs.reportcard.lock.LockService;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ReportcardApplication.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Slf4j
public class DatabaseLockTest extends AbstractLockServiceTest {
    @Autowired
    public DatabaseLockTest(LockService lockService) {
        super(lockService);
    }

    final static long waitToAcquireLockSleepDuration = 200L;

    @Test
    void waitingForLockTest() throws Exception {
        final Configuration configuration = lockService.dsl.configuration();
        UUID uuid = UUID.randomUUID();
        final String expectedResult = "result-" + uuid;
        BlockedDatabaseLockCallable callable1 = new BlockedDatabaseLockCallable(configuration, uuid, 1);
        BlockedDatabaseLockCallable callable2 = new BlockedDatabaseLockCallable(configuration, uuid, 2);

        //callable 1 grabs the lock first and is blocked so it holds the lock
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<String> future1 = executor.submit(callable1);
        Thread.sleep(waitToAcquireLockSleepDuration);
        Future<String> future2 = executor.submit(callable2);

        //Both callables start blocked
        assertTrue(callable1.getIsBlocked());
        assertNull(callable1.getResult());

        assertTrue(callable2.getIsBlocked());
        assertNull(callable2.getResult());

        //Callable 2 is waiting for the lock so it cannot complete
        callable2.unblock();
        Thread.sleep(200);
        assertNull(callable2.getResult());

        //After callable 1 is unblocked, both are able to complete
        callable1.unblock();
        assertEquals(expectedResult, future1.get());
        assertEquals(expectedResult, future2.get());
    }

    @Test
    void notWaitingForLockTest() throws InterruptedException, ExecutionException {
        final Configuration configuration = lockService.dsl.configuration();
        UUID uuid = UUID.randomUUID();
        final String expectedResult = "result-" + uuid;
        BlockedDatabaseLockCallable callable1 = new BlockedDatabaseLockCallable(configuration, uuid, 1);
        BlockedDatabaseLockCallable callable2 = new BlockedDatabaseLockCallable(configuration, uuid, 2);

        //callable 1 grabs the lock first and is blocked so it holds the lock
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<String> future1 = executor.submit(callable1);
        Thread.sleep(waitToAcquireLockSleepDuration);
        Future<String> future2 = executor.submit(callable2);

        //Both callables start blocked
        assertTrue(callable1.getIsBlocked());
        assertTrue(callable2.getIsBlocked());

        //unblocking the first callable allows it to complete and releases lock
        callable1.unblock();
        assertFalse(callable1.getIsBlocked());
        assertEquals(expectedResult, future1.get());

        //unblocking the second callable allows it to complete
        callable2.unblock();

        assertEquals(expectedResult, future2.get());
    }
}

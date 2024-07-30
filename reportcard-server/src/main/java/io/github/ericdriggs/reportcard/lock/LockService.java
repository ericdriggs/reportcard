package io.github.ericdriggs.reportcard.lock;

import io.github.ericdriggs.reportcard.persist.AbstractPersistService;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Function;

/**
 * Main db service class.
 * For every method which returns a single object, if <code>NULL</code> will throw
 * <code>ResponseStatusException(HttpStatus.NOT_FOUND)</code>
 */

@Service
@SuppressWarnings({"unused", "ConstantConditions", "DuplicatedCode"})
public class LockService extends AbstractPersistService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    final Duration pollTimeoutDuration = Duration.ofSeconds(10);
    final int getLockTimeoutSeconds = 2;
    final long pollSleepMillis = 1000;

    @Autowired
    public LockService(DSLContext dsl) {
        super(dsl);
    }

    public <T, R> R criticalSectionCallable(Function<T, R> function, T t, UUID uuid) throws Exception {
        FunctionDatabaseLockResultCallable<T, R> callable = new FunctionDatabaseLockResultCallable<>(
                dsl.configuration(),
                pollTimeoutDuration,
                getLockTimeoutSeconds,
                pollSleepMillis,
                uuid,
                function,
                t
        );
        return callable.call();
    }

}
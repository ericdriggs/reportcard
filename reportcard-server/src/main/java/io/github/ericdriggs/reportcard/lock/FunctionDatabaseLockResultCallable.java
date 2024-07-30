package io.github.ericdriggs.reportcard.lock;

import org.jooq.Configuration;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Function;

public class FunctionDatabaseLockResultCallable<T, R> extends AbstractDatabaseLockResultCallable<R> {

    final Function<T, R> function;
    final T t;

    public FunctionDatabaseLockResultCallable(
            final Configuration outerConfiguration,
            final Duration pollTimeoutDuration,
            final int getLockTimeoutSeconds,
            final long pollSleepMillis,
            final UUID uuid,
            Function<T, R> function,
            T t) {
        super(outerConfiguration, pollTimeoutDuration, getLockTimeoutSeconds, pollSleepMillis, uuid);
        this.function = function;
        this.t = t;
    }

    @Override
    public R doCall() throws Exception {
        return function.apply(t);
    }

}

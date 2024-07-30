package io.github.ericdriggs.reportcard.lock;

import java.util.concurrent.Callable;

/**
 * Callable which stores its result.
 * This can be useful when called from within a lambda, since variables cannot be assigned from within a lambda.
 * Result will be null until operation has been performed.
 * <br>
 * <quot>"Any local variable, formal parameter, or exception parameter used but not declared in a lambda expression must either be declared final or be effectively final (§4.12.4), or a compile-time error occurs where the use is attempted."</quot>
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se10/html/jls-15.html#jls-15.27.2">https://docs.oracle.com/javase/specs/jls/se10/html/jls-15.html#jls-15.27.2</a>
 */
public abstract class AbstractResultCallable<V> implements Callable<V> {

    V result;

    @Override
    public V call() throws Exception {
        return doCallAndSaveResult();
    }

    public final V doCallAndSaveResult() throws Exception {
        result = doCall();
        return result;
    }

    public abstract V doCall() throws Exception;

    public V getResult() {
        return result;
    }
}

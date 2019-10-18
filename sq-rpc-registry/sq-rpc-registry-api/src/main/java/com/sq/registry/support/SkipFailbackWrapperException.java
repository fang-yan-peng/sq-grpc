package com.sq.registry.support;

/**
 * Wrapper Exception, it is used to indicate that {@link FailbackRegistry} skips Failback.
 * <p>
 * NOTE: Expect to find other more conventional ways of instruction.
 *
 * @see FailbackRegistry
 */
public class SkipFailbackWrapperException extends RuntimeException {
    public SkipFailbackWrapperException(Throwable cause) {
        super(cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        // do nothing
        return null;
    }
}

package com.sq.registry.retry;

import com.sq.common.URL;
import com.sq.common.timer.Timeout;
import com.sq.registry.support.FailbackRegistry;

/**
 * FailedUnregisteredTask
 */
public final class FailedUnregisteredTask extends AbstractRetryTask {

    private static final String NAME = "retry unregister";

    public FailedUnregisteredTask(URL url, FailbackRegistry registry) {
        super(url, registry, NAME);
    }

    @Override
    protected void doRetry(URL url, FailbackRegistry registry, Timeout timeout) {
        registry.doUnregister(url);
        registry.removeFailedUnregisteredTask(url);
    }
}

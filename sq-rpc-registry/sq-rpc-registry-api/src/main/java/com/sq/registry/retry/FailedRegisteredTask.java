package com.sq.registry.retry;


import com.sq.common.URL;
import com.sq.common.timer.Timeout;
import com.sq.registry.support.FailbackRegistry;

/**
 * FailedRegisteredTask
 */
public final class FailedRegisteredTask extends AbstractRetryTask {

    private static final String NAME = "retry register";

    public FailedRegisteredTask(URL url, FailbackRegistry registry) {
        super(url, registry, NAME);
    }

    @Override
    protected void doRetry(URL url, FailbackRegistry registry, Timeout timeout) {
        registry.doRegister(url);
        registry.removeFailedRegisteredTask(url);
    }
}

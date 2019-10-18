package com.sq.registry.retry;


import com.sq.common.URL;
import com.sq.common.timer.Timeout;
import com.sq.registry.NotifyListener;
import com.sq.registry.support.FailbackRegistry;

/**
 * FailedSubscribedTask
 */
public final class FailedSubscribedTask extends AbstractRetryTask {

    private static final String NAME = "retry subscribe";

    private final NotifyListener listener;

    public FailedSubscribedTask(URL url, FailbackRegistry registry, NotifyListener listener) {
        super(url, registry, NAME);
        if (listener == null) {
            throw new IllegalArgumentException();
        }
        this.listener = listener;
    }

    @Override
    protected void doRetry(URL url, FailbackRegistry registry, Timeout timeout) {
        registry.doSubscribe(url, listener);
        registry.removeFailedSubscribedTask(url, listener);
    }
}

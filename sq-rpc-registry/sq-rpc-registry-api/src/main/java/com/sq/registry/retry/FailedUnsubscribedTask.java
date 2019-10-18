package com.sq.registry.retry;

import com.sq.common.URL;
import com.sq.common.timer.Timeout;
import com.sq.registry.NotifyListener;
import com.sq.registry.support.FailbackRegistry;

/**
 * FailedUnsubscribedTask
 */
public final class FailedUnsubscribedTask extends AbstractRetryTask {

    private static final String NAME = "retry unsubscribe";

    private final NotifyListener listener;

    public FailedUnsubscribedTask(URL url, FailbackRegistry registry, NotifyListener listener) {
        super(url, registry, NAME);
        if (listener == null) {
            throw new IllegalArgumentException();
        }
        this.listener = listener;
    }

    @Override
    protected void doRetry(URL url, FailbackRegistry registry, Timeout timeout) {
        registry.unsubscribe(url, listener);
        registry.removeFailedUnsubscribedTask(url, listener);
    }
}

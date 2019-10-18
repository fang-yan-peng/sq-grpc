package com.sq.registry.retry;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.sq.common.URL;
import com.sq.common.timer.Timeout;
import com.sq.common.utils.CollectionUtils;
import com.sq.registry.NotifyListener;
import com.sq.registry.support.FailbackRegistry;


/**
 * FailedNotifiedTask
 */
public final class FailedNotifiedTask extends AbstractRetryTask {

    private static final String NAME = "retry subscribe";

    private final NotifyListener listener;

    private final List<URL> urls = new CopyOnWriteArrayList<>();

    public FailedNotifiedTask(URL url, NotifyListener listener) {
        super(url, null, NAME);
        if (listener == null) {
            throw new IllegalArgumentException();
        }
        this.listener = listener;
    }

    public void addUrlToRetry(List<URL> urls) {
        if (CollectionUtils.isEmpty(urls)) {
            return;
        }
        this.urls.addAll(urls);
    }

    public void removeRetryUrl(List<URL> urls) {
        this.urls.removeAll(urls);
    }

    @Override
    protected void doRetry(URL url, FailbackRegistry registry, Timeout timeout) {
        if (CollectionUtils.isNotEmpty(urls)) {
            listener.notify(urls);
            urls.clear();
        }
        reput(timeout, retryPeriod);
    }
}

package com.sq.etcd.jetcd;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sq.etcd.RetryPolicy;
import com.sq.etcd.option.OptionUtil;

import io.grpc.Status;

public class RetryLoops {

    private final long startTimeMs = System.currentTimeMillis();
    private boolean isDone = false;
    private int retriedCount = 0;
    private Logger logger = LoggerFactory.getLogger(RetryLoops.class);

    public static <R> R invokeWithRetry(Callable<R> task, RetryPolicy retryPolicy) throws Exception {
        R result = null;
        RetryLoops retryLoop = new RetryLoops();
        while (retryLoop.shouldContinue()) {
            try {
                result = task.call();
                retryLoop.complete();
            } catch (Exception e) {
                retryLoop.fireException(e, retryPolicy);
            }
        }
        return result;
    }

    public void fireException(Exception e, RetryPolicy retryPolicy) throws Exception {

        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }

        boolean rethrow = true;
        if (isRetryException(e)
                && retryPolicy.shouldRetry(retriedCount++, System.currentTimeMillis() - startTimeMs, true)) {
            rethrow = false;
        }

        if (rethrow) {
            throw e;
        }
    }

    private boolean isRetryException(Throwable e) {
        Status status = Status.fromThrowable(e);
        if (OptionUtil.isRecoverable(status)) {
            return true;
        }

        return false;
    }

    public boolean shouldContinue() {
        return !isDone;
    }

    public void complete() {
        isDone = true;
    }

}

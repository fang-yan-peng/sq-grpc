package com.sq.common.threadpool.support.cached;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.threadpool.ThreadPool;
import com.sq.common.threadpool.support.AbortPolicyWithReport;
import com.sq.common.threadpool.support.ContextThreadPoolExecutor;
import com.sq.common.utils.NamedThreadFactory;


/**
 * This thread pool is self-tuned. Thread will be recycled after idle for one minute, and new thread will be created for
 * the upcoming request.
 *
 * @see java.util.concurrent.Executors#newCachedThreadPool()
 */
public class CachedThreadPool implements ThreadPool {

    @Override
    public Executor getExecutor(URL url) {
        String name = url.getParameter(Constants.THREAD_NAME_KEY, Constants.DEFAULT_THREAD_NAME);
        int cores = url.getParameter(Constants.CORE_THREADS_KEY, Constants.DEFAULT_CORE_THREADS);
        int threads = url.getParameter(Constants.THREADS_KEY, Integer.MAX_VALUE);
        int queues = url.getParameter(Constants.QUEUES_KEY, Constants.DEFAULT_QUEUES);
        int alive = url.getParameter(Constants.ALIVE_KEY, Constants.DEFAULT_ALIVE);
        return new ContextThreadPoolExecutor(cores, threads, alive, TimeUnit.MILLISECONDS,
                queues == 0 ? new SynchronousQueue<>() :
                        (queues < 0 ? new LinkedBlockingQueue<>()
                                : new LinkedBlockingQueue<>(queues)),
                new NamedThreadFactory(name, true), new AbortPolicyWithReport(name, url));
    }
}

package com.sq.common.threadpool.support.fixed;

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
 * Creates a thread pool that reuses a fixed number of threads
 *
 * @see java.util.concurrent.Executors#newFixedThreadPool(int)
 */
public class FixedThreadPool implements ThreadPool {

    @Override
    public Executor getExecutor(URL url) {
        String name = url.getParameter(Constants.THREAD_NAME_KEY, Constants.DEFAULT_THREAD_NAME);
        int threads = url.getParameter(Constants.THREADS_KEY, Constants.DEFAULT_THREADS);
        int queues = url.getParameter(Constants.QUEUES_KEY, Constants.DEFAULT_QUEUES);

        return new ContextThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
                queues == 0 ? new SynchronousQueue<>() :
                        (queues < 0 ? new LinkedBlockingQueue<>()
                                : new LinkedBlockingQueue<>(queues)),
                new NamedThreadFactory(name, true), new AbortPolicyWithReport(name, url));
    }

}

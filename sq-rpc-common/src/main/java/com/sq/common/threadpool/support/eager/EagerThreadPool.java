package com.sq.common.threadpool.support.eager;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.threadpool.ThreadPool;
import com.sq.common.threadpool.support.AbortPolicyWithReport;
import com.sq.common.utils.NamedThreadFactory;

/**
 * EagerThreadPool
 * When the core threads are all in busy,
 * create new thread instead of putting task into blocking queue.
 */
public class EagerThreadPool implements ThreadPool {

    @Override
    public Executor getExecutor(URL url) {
        String name = url.getParameter(Constants.THREAD_NAME_KEY, Constants.DEFAULT_THREAD_NAME);
        int cores = url.getParameter(Constants.CORE_THREADS_KEY, Constants.DEFAULT_CORE_THREADS);
        int threads = url.getParameter(Constants.THREADS_KEY, Integer.MAX_VALUE);
        int queues = url.getParameter(Constants.QUEUES_KEY, Constants.DEFAULT_QUEUES);
        int alive = url.getParameter(Constants.ALIVE_KEY, Constants.DEFAULT_ALIVE);

        // init queue and executor
        TaskQueue<Runnable> taskQueue = new TaskQueue<Runnable>(queues <= 0 ? 1 : queues);
        EagerThreadPoolExecutor executor = new EagerThreadPoolExecutor(cores,
                threads,
                alive,
                TimeUnit.MILLISECONDS,
                taskQueue,
                new NamedThreadFactory(name, true),
                new AbortPolicyWithReport(name, url));
        taskQueue.setExecutor(executor);
        return executor;
    }
}

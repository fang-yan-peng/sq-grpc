package com.sq.common.threadpool;

import java.util.concurrent.Executor;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.extension.Adaptive;
import com.sq.common.extension.SPI;


/**
 * ThreadPool
 */
@SPI("fixed")
public interface ThreadPool {

    /**
     * Thread pool
     *
     * @param url URL contains thread parameter
     * @return thread pool
     */
    @Adaptive({Constants.THREADPOOL_KEY})
    Executor getExecutor(URL url);

}
package com.sq.common.threadpool.support;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.utils.JVMUtil;


/**
 * Abort Policy.
 * Log warn info when abort.
 */
public class AbortPolicyWithReport extends ThreadPoolExecutor.AbortPolicy {

    protected static final Logger logger = LoggerFactory.getLogger(AbortPolicyWithReport.class);

    private final String threadName;

    private final URL url;

    private static volatile long lastPrintTime = 0;

    private static Semaphore guard = new Semaphore(1);

    public AbortPolicyWithReport(String threadName, URL url) {
        this.threadName = threadName;
        this.url = url;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        String msg = String.format("Thread pool is EXHAUSTED!" +
                        " Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: %d)," +
                        " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s), in %s://%s:%d!",
                threadName, e.getPoolSize(), e.getActiveCount(), e.getCorePoolSize(), e.getMaximumPoolSize(), e.getLargestPoolSize(),
                e.getTaskCount(), e.getCompletedTaskCount(), e.isShutdown(), e.isTerminated(), e.isTerminating(),
                url.getProtocol(), url.getIp(), url.getPort());
        logger.warn(msg);
        dumpJStack();
        throw new RejectedExecutionException(msg);
    }

    private void dumpJStack() {
        long now = System.currentTimeMillis();

        //dump every 10 minutes
        if (now - lastPrintTime < 10 * 60 * 1000) {
            return;
        }

        if (!guard.tryAcquire()) {
            return;
        }

        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.execute(() -> {
            String dumpPath = url.getParameter(Constants.DUMP_DIRECTORY, System.getProperty("user.home"));

            SimpleDateFormat sdf;

            String os = System.getProperty("os.name").toLowerCase();

            // window system don't support ":" in file name
            if (os.contains("win")) {
                sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            } else {
                sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            }

            String dateStr = sdf.format(new Date());
            //try-with-resources
            try (FileOutputStream jStackStream = new FileOutputStream(new File(dumpPath, "Sq_Rpc_JStack.log" + "." + dateStr))) {
                JVMUtil.jstack(jStackStream);
            } catch (Throwable t) {
                logger.error("dump jStack error", t);
            } finally {
                guard.release();
            }
            lastPrintTime = System.currentTimeMillis();
        });
        //must shutdown thread pool ,if not will lead to OOM
        pool.shutdown();

    }

}

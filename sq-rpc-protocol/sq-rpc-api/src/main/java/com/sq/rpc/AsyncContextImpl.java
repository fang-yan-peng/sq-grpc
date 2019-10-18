package com.sq.rpc;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncContextImpl implements AsyncContext {
    private static final Logger logger = LoggerFactory.getLogger(AsyncContextImpl.class);

    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean stopped = new AtomicBoolean(false);

    private CompletableFuture<Object> future;

    private RpcContext storedContext;
    private RpcContext storedServerContext;

    public AsyncContextImpl() {
        this.storedContext = RpcContext.getContext();
        this.storedServerContext = RpcContext.getServerContext();
    }

    @Override
    public void write(Object value) {
        if (isAsyncStarted() && stop()) {
            if (value instanceof Throwable) {
                Throwable bizExe = (Throwable) value;
                future.completeExceptionally(bizExe);
            } else {
                future.complete(value);
            }
        } else {
            throw new IllegalStateException("The async response has probably been wrote back by another thread, or the asyncContext has been closed.");
        }
    }

    @Override
    public boolean isAsyncStarted() {
        return started.get();
    }

    @Override
    public boolean stop() {
        return stopped.compareAndSet(false, true);
    }

    @Override
    public void start() {
        if (this.started.compareAndSet(false, true)) {
            this.future = new CompletableFuture<>();
        }
    }

    @Override
    public void signalContextSwitch() {
        RpcContext.restoreContext(storedContext);
        RpcContext.restoreServerContext(storedServerContext);
        // Restore any other contexts in here if necessary.
    }

    public CompletableFuture<Object> getInternalFuture() {
        return future;
    }
}

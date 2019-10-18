package com.sq.rpc.proxy;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sq.common.URL;
import com.sq.common.exceptions.RpcException;
import com.sq.rpc.Invocation;
import com.sq.rpc.Invoker;
import com.sq.rpc.Result;
import com.sq.rpc.RpcContext;
import com.sq.rpc.RpcResult;


/**
 * InvokerWrapper
 */
public abstract class AbstractProxyInvoker<T> implements Invoker<T> {
    Logger logger = LoggerFactory.getLogger(AbstractProxyInvoker.class);

    private final T proxy;

    private final Class<T> type;

    private final URL url;

    public AbstractProxyInvoker(T proxy, Class<T> type, URL url) {
        if (proxy == null) {
            throw new IllegalArgumentException("proxy == null");
        }
        if (type == null) {
            throw new IllegalArgumentException("interface == null");
        }
        if (!type.isInstance(proxy)) {
            throw new IllegalArgumentException(proxy.getClass().getName() + " not implement interface " + type);
        }
        this.proxy = proxy;
        this.type = type;
        this.url = url;
    }

    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void destroy() {
    }

    // TODO Unified to AsyncResult?
    @Override
    public Result invoke(Invocation invocation) throws RpcException {
        RpcContext rpcContext = RpcContext.getContext();
        try {
            Object obj = doInvoke(proxy, invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments());
            return new RpcResult(obj);
        } catch (InvocationTargetException e) {
            // TODO async throw exception before async thread write back, should stop asyncContext
            if (rpcContext.isAsyncStarted() && !rpcContext.stopAsync()) {
                logger.error("Provider async started, but got an exception from the original method, cannot write the exception back to consumer because an async result may have returned the new thread.", e);
            }
            return new RpcResult(e.getTargetException());
        } catch (Throwable e) {
            throw new RpcException("Failed to invoke remote proxy method " + invocation.getMethodName() + " to " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    protected abstract Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Throwable;

    @Override
    public String toString() {
        return getInterface() + " -> " + (getUrl() == null ? " " : getUrl().toString());
    }

    @Override
    public T getService() {
        return proxy;
    }
}

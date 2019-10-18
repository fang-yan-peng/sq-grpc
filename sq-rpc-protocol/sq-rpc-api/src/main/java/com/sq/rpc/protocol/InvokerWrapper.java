package com.sq.rpc.protocol;

import com.sq.common.URL;
import com.sq.common.exceptions.RpcException;
import com.sq.rpc.Invocation;
import com.sq.rpc.Invoker;
import com.sq.rpc.Result;

/**
 *
 * @author yanpengfang
 * create 2019-10-15 6:59 PM
 */
public /**
 * InvokerWrapper
 */
class InvokerWrapper<T> implements Invoker<T> {

    private final Invoker<T> invoker;

    private final URL url;

    public InvokerWrapper(Invoker<T> invoker, URL url) {
        this.invoker = invoker;
        this.url = url;
    }

    @Override
    public Class<T> getInterface() {
        return invoker.getInterface();
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean isAvailable() {
        return invoker.isAvailable();
    }

    @Override
    public Result invoke(Invocation invocation) throws RpcException {
        return invoker.invoke(invocation);
    }

    @Override
    public void destroy() {
        invoker.destroy();
    }

    @Override
    public T getService() {
        return invoker.getService();
    }
}

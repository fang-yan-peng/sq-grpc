package com.sq.config.invoker;


import com.sq.common.URL;
import com.sq.common.exceptions.RpcException;
import com.sq.config.ServiceConfig;
import com.sq.rpc.Invocation;
import com.sq.rpc.Invoker;
import com.sq.rpc.Result;

/**
 *
 * A Invoker wrapper that wrap the invoker and all the metadata (ServiceConfig)
 */
public class DelegateProviderMetaDataInvoker<T> implements Invoker {
    protected final Invoker<T> invoker;
    private ServiceConfig metadata;

    public DelegateProviderMetaDataInvoker(Invoker<T> invoker, ServiceConfig metadata) {
        this.invoker = invoker;
        this.metadata = metadata;
    }

    @Override
    public Class<T> getInterface() {
        return invoker.getInterface();
    }

    @Override
    public URL getUrl() {
        return invoker.getUrl();
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

    public ServiceConfig getMetadata() {
        return metadata;
    }
}

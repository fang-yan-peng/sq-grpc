package com.sq.rpc.cluster.support;

import java.util.List;

import com.sq.common.Version;
import com.sq.common.exceptions.RpcException;
import com.sq.common.utils.NetUtils;
import com.sq.rpc.Invocation;
import com.sq.rpc.Invoker;
import com.sq.rpc.Result;
import com.sq.rpc.cluster.Directory;
import com.sq.rpc.cluster.LoadBalance;

/**
 * Execute exactly once, which means this policy will throw an exception immediately in case of an invocation error.
 * Usually used for non-idempotent write operations
 *
 */
public class FailfastClusterInvoker<T> extends AbstractClusterInvoker<T> {

    public FailfastClusterInvoker(Directory<T> directory) {
        super(directory);
    }

    @Override
    public Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException {
        checkInvokers(invokers, invocation);
        Invoker<T> invoker = select(loadbalance, invocation, invokers, null);
        try {
            return invoker.invoke(invocation);
        } catch (Throwable e) {
            if (e instanceof RpcException && ((RpcException) e).isBiz()) { // biz exception.
                throw (RpcException) e;
            }
            throw new RpcException(e instanceof RpcException ? ((RpcException) e).getCode() : 0,
                    "Failfast invoke providers " + invoker.getUrl() + " " + loadbalance.getClass().getSimpleName()
                            + " select from all providers " + invokers + " for services " + getInterface().getName()
                            + " method " + invocation.getMethodName() + " on consumer " + NetUtils.getLocalHost()
                            + " use version " + Version.getVersion()
                            + ", but no luck to perform the invocation. Last error is: " + e.getMessage(),
                    e.getCause() != null ? e.getCause() : e);
        }
    }
}

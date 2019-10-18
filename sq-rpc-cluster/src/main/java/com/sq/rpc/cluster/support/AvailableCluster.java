package com.sq.rpc.cluster.support;

import java.util.List;

import com.sq.common.exceptions.RpcException;
import com.sq.rpc.Invocation;
import com.sq.rpc.Invoker;
import com.sq.rpc.Result;
import com.sq.rpc.cluster.Cluster;
import com.sq.rpc.cluster.Directory;
import com.sq.rpc.cluster.LoadBalance;

/**
 * AvailableCluster
 *
 */
public class AvailableCluster implements Cluster {

    public static final String NAME = "available";

    @Override
    public <T> Invoker<T> join(Directory<T> directory) throws RpcException {

        return new AbstractClusterInvoker<T>(directory) {
            @Override
            public Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException {
                for (Invoker<T> invoker : invokers) {
                    if (invoker.isAvailable()) {
                        return invoker.invoke(invocation);
                    }
                }
                throw new RpcException("No provider available in " + invokers);
            }
        };

    }

}

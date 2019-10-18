package com.sq.rpc.cluster.support;


import com.sq.common.exceptions.RpcException;
import com.sq.rpc.Invoker;
import com.sq.rpc.cluster.Cluster;
import com.sq.rpc.cluster.Directory;

/**
 * {@link FailoverClusterInvoker}
 *
 */
public class FailoverCluster implements Cluster {

    public final static String NAME = "failover";

    @Override
    public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
        return new FailoverClusterInvoker<T>(directory);
    }

}

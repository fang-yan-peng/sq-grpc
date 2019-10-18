package com.sq.rpc.cluster;


import com.sq.common.exceptions.RpcException;
import com.sq.common.extension.Adaptive;
import com.sq.common.extension.SPI;
import com.sq.rpc.Invoker;
import com.sq.rpc.cluster.support.FailoverCluster;

/**
 * Cluster. (SPI, Singleton, ThreadSafe)
 * <p>
 *
 */
@SPI(FailoverCluster.NAME)
public interface Cluster {

    /**
     * Merge the directory invokers to a virtual invoker.
     *
     * @param <T>
     * @param directory
     * @return cluster invoker
     * @throws RpcException
     */
    @Adaptive
    <T> Invoker<T> join(Directory<T> directory) throws RpcException;

}
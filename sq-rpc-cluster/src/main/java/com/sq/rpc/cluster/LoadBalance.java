package com.sq.rpc.cluster;

import java.util.List;

import com.sq.common.URL;
import com.sq.common.exceptions.RpcException;
import com.sq.common.extension.Adaptive;
import com.sq.common.extension.SPI;
import com.sq.rpc.Invocation;
import com.sq.rpc.Invoker;
import com.sq.rpc.cluster.loadbalance.RandomLoadBalance;


/**
 * LoadBalance. (SPI, Singleton, ThreadSafe)
 *
 * @see Cluster#join(Directory)
 */
@SPI(RandomLoadBalance.NAME)
public interface LoadBalance {

    /**
     * select one invoker in list.
     *
     * @param invokers   invokers.
     * @param url        refer url
     * @param invocation invocation.
     * @return selected invoker.
     */
    @Adaptive("loadbalance")
    <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException;

}
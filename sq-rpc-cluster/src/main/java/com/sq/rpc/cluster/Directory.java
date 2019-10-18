package com.sq.rpc.cluster;

import java.util.List;

import com.sq.common.Node;
import com.sq.common.exceptions.RpcException;
import com.sq.rpc.Invocation;
import com.sq.rpc.Invoker;


/**
 * Directory. (SPI, Prototype, ThreadSafe)
 * <p>
 * <a href="http://en.wikipedia.org/wiki/Directory_service">Directory Service</a>
 *
 * @see Cluster#join(Directory)
 */
public interface Directory<T> extends Node {

    /**
     * get services type.
     *
     * @return services type.
     */
    Class<T> getInterface();

    /**
     * list invokers.
     *
     * @return invokers
     */
    List<Invoker<T>> list(Invocation invocation) throws RpcException;

}
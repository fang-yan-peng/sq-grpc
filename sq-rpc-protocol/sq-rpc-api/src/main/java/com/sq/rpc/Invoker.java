
package com.sq.rpc;

import com.sq.common.Node;
import com.sq.common.exceptions.RpcException;

/**
 * Invoker. (API/SPI, Prototype, ThreadSafe)
 */
public interface Invoker<T> extends Node {

    /**
     * get services interface.
     *
     * @return services interface.
     */
    Class<T> getInterface();

    /**
     * invoke.
     *
     * @param invocation
     * @return result
     * @throws RpcException
     */
    Result invoke(Invocation invocation) throws RpcException;

    /**
     * get services
     *
     * @return t
     */
    default T getService() {
        return null;
    }
}
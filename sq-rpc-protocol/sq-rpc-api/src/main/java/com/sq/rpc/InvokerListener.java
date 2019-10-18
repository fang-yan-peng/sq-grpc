package com.sq.rpc;


import com.sq.common.URL;
import com.sq.common.exceptions.RpcException;
import com.sq.common.extension.SPI;

/**
 * InvokerListener. (SPI, Singleton, ThreadSafe)
 */
@SPI
public interface InvokerListener {

    /**
     * The invoker referred
     *
     * @param invoker
     * @throws RpcException
     * @see Protocol#refer(Class, URL)
     */
    void referred(Invoker<?> invoker) throws RpcException;

    /**
     * The invoker destroyed.
     *
     * @param invoker
     * @see Invoker#destroy()
     */
    void destroyed(Invoker<?> invoker);

}
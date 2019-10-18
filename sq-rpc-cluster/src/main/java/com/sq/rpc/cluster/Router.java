package com.sq.rpc.cluster;

import java.util.List;

import com.sq.common.URL;
import com.sq.common.exceptions.RpcException;
import com.sq.rpc.Invocation;
import com.sq.rpc.Invoker;


/**
 * Router. (SPI, Prototype, ThreadSafe)
 * <p>
 * <a href="http://en.wikipedia.org/wiki/Routing">Routing</a>
 *
 * @see Cluster#join(Directory)
 * @see Directory#list(Invocation)
 */
public interface Router extends Comparable<Router> {

    int DEFAULT_PRIORITY = Integer.MAX_VALUE;

    /**
     * Get the router url.
     *
     * @return url
     */
    URL getUrl();

    /**
     * Filter invokers with current routing rule and only return the invokers that comply with the rule.
     *
     * @param invokers   invoker list
     * @param url        refer url
     * @param invocation invocation
     * @return routed invokers
     * @throws RpcException
     */
    <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException;


    /**
     * Notify the router the invoker list. Invoker list may change from time to time. This method gives the router a
     * chance to prepare before {@link Router#route(List, URL, Invocation)} gets called.
     *
     * @param invokers invoker list
     * @param <T>      invoker's type
     */
    default <T> void notify(List<Invoker<T>> invokers) {

    }

    /**
     * To decide whether this router need to execute every time an RPC comes or should only execute when addresses or
     * rule change.
     *
     * @return true if the router need to execute every time.
     */
    boolean isRuntime();

    /**
     * To decide whether this router should take effect when none of the invoker can match the router rule, which
     * means the {@link #route(List, URL, Invocation)} would be empty. Most of time, most router implementation would
     * default this value to false.
     *
     * @return true to execute if none of invokers matches the current router
     */
    boolean isForce();

    /**
     * Router's priority, used to sort routers.
     *
     * @return router's priority
     */
    int getPriority();

    @Override
    default int compareTo(Router o) {
        if (o == null) {
            throw new IllegalArgumentException();
        }
        return Integer.compare(this.getPriority(), o.getPriority());
    }
}

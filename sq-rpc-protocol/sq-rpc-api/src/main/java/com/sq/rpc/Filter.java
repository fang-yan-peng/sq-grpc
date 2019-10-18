
package com.sq.rpc;


import com.sq.common.exceptions.RpcException;
import com.sq.common.extension.SPI;

/**
 * Extension for intercepting the invocation for both services provider and consumer, furthermore, most of
 * functions in rpc are implemented base on the same mechanism. Since every time when remote
 * method is
 * invoked, the filter extensions will be executed too, the corresponding penalty should be considered before
 * more filters are added.
 * <pre>
 *  They way filter work from sequence point of view is
 *    <b>
 *    ...code before filter ...
 *          invoker.invoke(invocation) //filter work in a filter implementation class
 *          ...code after filter ...
 *    </b>
 *    Caching is implemented in sq rpc using filter approach. If cache is configured for invocation
 *    then before
 *    remote call configured caching type's (e.g. Thread Local, JCache etc) implementation invoke method gets called.
 * </pre>
 * Filter. (SPI, Singleton, ThreadSafe)
 *
 */
@SPI
public interface Filter {

    /**
     * do invoke filter.
     * <p>
     * <code>
     * // before filter
     * Result result = invoker.invoke(invocation);
     * // after filter
     * return result;
     * </code>
     *
     * @param invoker    services
     * @param invocation invocation.
     * @return invoke result.
     * @throws RpcException
     * @see Invoker#invoke(Invocation)
     */
    Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException;

    /**
     * Return processing result
     *
     * @param result     result
     * @param invoker    invoker
     * @param invocation invocation
     * @return Return {@link Result}
     */
    default Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        return result;
    }

}
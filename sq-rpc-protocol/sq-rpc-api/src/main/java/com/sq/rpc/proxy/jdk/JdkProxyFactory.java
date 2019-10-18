package com.sq.rpc.proxy.jdk;


import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.sq.common.URL;
import com.sq.rpc.Invoker;
import com.sq.rpc.proxy.AbstractProxyFactory;
import com.sq.rpc.proxy.AbstractProxyInvoker;
import com.sq.rpc.proxy.InvokerInvocationHandler;

/**
 * JdkRpcProxyFactory
 */
public class JdkProxyFactory extends AbstractProxyFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, new InvokerInvocationHandler(invoker));
    }

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        return new AbstractProxyInvoker<T>(proxy, type, url) {
            @Override
            protected Object doInvoke(T proxy, String methodName,
                                      Class<?>[] parameterTypes,
                                      Object[] arguments) throws Throwable {
                Method method = proxy.getClass().getMethod(methodName, parameterTypes);
                return method.invoke(proxy, arguments);
            }


        };
    }

}

package com.sq.rpc.proxy;


import com.sq.common.Constants;
import com.sq.common.exceptions.RpcException;
import com.sq.common.utils.ReflectUtils;
import com.sq.rpc.Invoker;
import com.sq.rpc.ProxyFactory;

/**
 * AbstractProxyFactory
 */
public abstract class AbstractProxyFactory implements ProxyFactory {

    @Override
    public <T> T getProxy(Invoker<T> invoker) throws RpcException {
        return getProxy(invoker, false);
    }

    @Override
    public <T> T getProxy(Invoker<T> invoker, boolean generic) throws RpcException {
        Class<?>[] interfaces = null;
        String config = invoker.getUrl().getParameter(Constants.INTERFACES);
        if (config != null && config.length() > 0) {
            String[] types = Constants.COMMA_SPLIT_PATTERN.split(config);
            if (types != null && types.length > 0) {
                interfaces = new Class<?>[types.length + 1];
                interfaces[0] = invoker.getInterface();
                for (int i = 0; i < types.length; i++) {
                    // TODO can we load successfully for a different classloader?.
                    interfaces[i + 1] = ReflectUtils.forName(types[i]);
                }
            }
        }
        if (interfaces == null) {
            interfaces = new Class<?>[]{invoker.getInterface()};
        }
        return getProxy(invoker, interfaces);
    }

    public abstract <T> T getProxy(Invoker<T> invoker, Class<?>[] types);

}

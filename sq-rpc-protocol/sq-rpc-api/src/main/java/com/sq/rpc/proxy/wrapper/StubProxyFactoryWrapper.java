package com.sq.rpc.proxy.wrapper;


import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.URLBuilder;
import com.sq.common.Version;
import com.sq.common.bytecode.Wrapper;
import com.sq.common.exceptions.RpcException;
import com.sq.common.utils.ConfigUtils;
import com.sq.common.utils.NetUtils;
import com.sq.common.utils.ReflectUtils;
import com.sq.common.utils.StringUtils;
import com.sq.rpc.Exporter;
import com.sq.rpc.Invoker;
import com.sq.rpc.Protocol;
import com.sq.rpc.ProxyFactory;

/**
 * StubProxyFactoryWrapper
 */
public class StubProxyFactoryWrapper implements ProxyFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(StubProxyFactoryWrapper.class);

    private final ProxyFactory proxyFactory;

    private Protocol protocol;

    public StubProxyFactoryWrapper(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public <T> T getProxy(Invoker<T> invoker, boolean generic) throws RpcException {
        return proxyFactory.getProxy(invoker, generic);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T getProxy(Invoker<T> invoker) throws RpcException {
        T proxy = proxyFactory.getProxy(invoker);
        /*if (GenericService.class != invoker.getInterface()) {*/
        URL url = invoker.getUrl();
        String stub = url.getParameter(Constants.STUB_KEY, url.getParameter(Constants.LOCAL_KEY));
        if (ConfigUtils.isNotEmpty(stub)) {
            Class<?> serviceType = invoker.getInterface();
            if (ConfigUtils.isDefault(stub)) {
                if (url.hasParameter(Constants.STUB_KEY)) {
                    stub = serviceType.getName() + "Stub";
                } else {
                    stub = serviceType.getName() + "Local";
                }
            }
            try {
                Class<?> stubClass = ReflectUtils.forName(stub);
                if (!serviceType.isAssignableFrom(stubClass)) {
                    throw new IllegalStateException("The stub implementation class " + stubClass.getName() + " not implement interface " + serviceType.getName());
                }
                try {
                    Constructor<?> constructor = ReflectUtils.findConstructor(stubClass, serviceType);
                    proxy = (T) constructor.newInstance(new Object[]{proxy});
                    //export stub services
                    URLBuilder urlBuilder = URLBuilder.from(url);
                    if (url.getParameter(Constants.STUB_EVENT_KEY, Constants.DEFAULT_STUB_EVENT)) {
                        urlBuilder.addParameter(Constants.STUB_EVENT_METHODS_KEY, StringUtils.join(Wrapper.getWrapper(proxy.getClass()).getDeclaredMethodNames(), ","));
                        urlBuilder.addParameter(Constants.IS_SERVER_KEY, Boolean.FALSE.toString());
                        try {
                            export(proxy, (Class) invoker.getInterface(), urlBuilder.build());
                        } catch (Exception e) {
                            LOGGER.error("export a stub services error.", e);
                        }
                    }
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException("No such constructor \"public " + stubClass.getSimpleName() + "(" + serviceType.getName() + ")\" in stub implementation class " + stubClass.getName(), e);
                }
            } catch (Throwable t) {
                LOGGER.error("Failed to create stub implementation class " + stub + " in consumer " + NetUtils.getLocalHost() + " use version " + Version.getVersion() + ", cause: " + t.getMessage(), t);
                // ignore
            }
        }
        /*}*/
        return proxy;
    }

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException {
        return proxyFactory.getInvoker(proxy, type, url);
    }

    private <T> Exporter<T> export(T instance, Class<T> type, URL url) {
        return protocol.export(proxyFactory.getInvoker(instance, type, url));
    }

}

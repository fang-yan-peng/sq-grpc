package com.sq.rpc.module;


import java.lang.reflect.Method;
import java.util.Map;

/**
 * Consumer Model which is about subscribed services.
 */
public class ConsumerModel {
    private final Object proxyObject;
    private final String serviceName;
    private final Class<?> serviceInterfaceClass;

    /**
     *  This constructor create an instance of ConsumerModel and passed objects should not be null.
     *  If services name, services instance, proxy object,methods should not be null. If these are null
     *  then this constructor will throw {@link IllegalArgumentException}
     * @param serviceName Name of the services.
     * @param serviceInterfaceClass Service interface class.
     * @param proxyObject  Proxy object.
     * @param attributes Attributes of methods.
     */
    public ConsumerModel(String serviceName
            , Class<?> serviceInterfaceClass
            , Object proxyObject
            , Map<String, Object> attributes) {


        this.serviceName = serviceName;
        this.serviceInterfaceClass = serviceInterfaceClass;
        this.proxyObject = proxyObject;
    }

    /**
     * Return the proxy object used by called while creating instance of ConsumerModel
     * @return
     */
    public Object getProxyObject() {
        return proxyObject;
    }


    public Class<?> getServiceInterfaceClass() {
        return serviceInterfaceClass;
    }

    public String getServiceName() {
        return serviceName;
    }
}

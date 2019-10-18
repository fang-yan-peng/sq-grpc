package com.sq.rpc.module;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ProviderModel which is about published services
 */
public class ProviderModel {
    private final String serviceName;
    private final Object serviceInstance;
    private final Class<?> serviceInterfaceClass;
    public ProviderModel(String serviceName, Object serviceInstance, Class<?> serviceInterfaceClass) {
        if (null == serviceInstance) {
            throw new IllegalArgumentException("Service[" + serviceName + "]Target is NULL.");
        }

        this.serviceName = serviceName;
        this.serviceInstance = serviceInstance;
        this.serviceInterfaceClass = serviceInterfaceClass;

        initMethod();
    }


    public String getServiceName() {
        return serviceName;
    }

    public Class<?> getServiceInterfaceClass() {
        return serviceInterfaceClass;
    }

    public Object getServiceInstance() {
        return serviceInstance;
    }


    private void initMethod() {
        Method[] methodsToExport = this.serviceInterfaceClass.getMethods();
        for (Method method : methodsToExport) {
            method.setAccessible(true);
        }
    }

}

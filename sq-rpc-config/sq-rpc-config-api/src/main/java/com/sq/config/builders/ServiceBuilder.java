package com.sq.config.builders;

import java.util.ArrayList;
import java.util.List;

import com.sq.config.MethodConfig;
import com.sq.config.ProviderConfig;
import com.sq.config.ServiceConfig;


/**
 * This is a builder for build {@link ServiceConfig}.
 *
 * @since 2.7
 */
public class ServiceBuilder<U> extends AbstractServiceBuilder<ServiceConfig, ServiceBuilder<U>> {
    /**
     * The interface name of the exported services
     */
    private String interfaceName;

    /**
     * The interface class of the exported services
     */
    private Class<?> interfaceClass;

    /**
     * The reference of the interface implementation
     */
    private U ref;

    /**
     * The services name
     */
    private String path;

    /**
     * The method configuration
     */
    private List<MethodConfig> methods;

    /**
     * The provider configuration
     */
    private ProviderConfig provider;

    /**
     * The providerIds
     */
    private String providerIds;
    /**
     * whether it is a GenericService
     */
    private String generic;

    public ServiceBuilder<U> interfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
        return getThis();
    }

    public ServiceBuilder<U> interfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
        return getThis();
    }

    public ServiceBuilder<U> ref(U ref) {
        this.ref = ref;
        return getThis();
    }

    public ServiceBuilder<U> path(String path) {
        this.path = path;
        return getThis();
    }

    public ServiceBuilder<U> addMethod(MethodConfig method) {
        if (this.methods == null) {
            this.methods = new ArrayList<>();
        }
        this.methods.add(method);
        return getThis();
    }

    public ServiceBuilder<U> addMethods(List<? extends MethodConfig> methods) {
        if (this.methods == null) {
            this.methods = new ArrayList<>();
        }
        this.methods.addAll(methods);
        return getThis();
    }

    public ServiceBuilder<U> provider(ProviderConfig provider) {
        this.provider = provider;
        return getThis();
    }

    public ServiceBuilder<U> providerIds(String providerIds) {
        this.providerIds = providerIds;
        return getThis();
    }

    public ServiceBuilder<U> generic(String generic) {
        this.generic = generic;
        return getThis();
    }

    @Override
    public ServiceBuilder<U> mock(String mock) {
        throw new IllegalArgumentException("mock doesn't support on provider side");
    }

    @Override
    public ServiceBuilder<U> mock(Boolean mock) {
        throw new IllegalArgumentException("mock doesn't support on provider side");
    }

    public ServiceConfig<U> build() {
        ServiceConfig<U> serviceConfig = new ServiceConfig<>();
        super.build(serviceConfig);

        serviceConfig.setInterface(interfaceName);
        serviceConfig.setInterface(interfaceClass);
        serviceConfig.setRef(ref);
        serviceConfig.setPath(path);
        serviceConfig.setMethods(methods);
        serviceConfig.setProvider(provider);
        serviceConfig.setProviderIds(providerIds);
        serviceConfig.setGeneric(generic);

        return serviceConfig;
    }

    @Override
    protected ServiceBuilder<U> getThis() {
        return this;
    }
}

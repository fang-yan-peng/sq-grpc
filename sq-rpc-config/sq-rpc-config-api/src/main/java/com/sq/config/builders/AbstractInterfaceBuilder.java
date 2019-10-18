package com.sq.config.builders;


import java.util.ArrayList;
import java.util.List;

import com.sq.common.utils.StringUtils;
import com.sq.config.AbstractInterfaceConfig;
import com.sq.config.ApplicationConfig;
import com.sq.config.ConfigCenterConfig;
import com.sq.config.MetadataReportConfig;
import com.sq.config.ModuleConfig;
import com.sq.config.MonitorConfig;
import com.sq.config.RegistryConfig;

/**
 * AbstractBuilder
 *
 * @since 2.7
 */
public abstract class AbstractInterfaceBuilder<T extends AbstractInterfaceConfig, B extends AbstractInterfaceBuilder<T, B>>
        extends AbstractMethodBuilder<T, B> {
    /**
     * Local impl class name for the services interface
     */
    protected String local;

    /**
     * Local stub class name for the services interface
     */
    protected String stub;


    /**
     * Strategies for generating dynamic agents，there are two strategies can be choosed: jdk and javassist
     */
    protected String proxy;

    /**
     * Cluster type
     */
    protected String cluster;

    /**
     * when the provider side exposed a services or the customer side references a remote services used,
     * if there are more than one, you can use commas to separate them
     */
    protected String filter;

    /**
     * The Listener when the provider side exposes a services or the customer side references a remote services used
     * if there are more than one, you can use commas to separate them
     */
    protected String listener;

    /**
     * The owner of the services providers
     */
    protected String owner;

    /**
     * Connection limits, 0 means shared connection, otherwise it defines the connections delegated to the current services
     */
    protected Integer connections;

    /**
     * The layer of services providers
     */
    protected String layer;

    /**
     * The application info
     */
    protected ApplicationConfig application;

    /**
     * The module info
     */
    protected ModuleConfig module;

    /**
     * Registry centers
     */
    protected List<RegistryConfig> registries;

    protected String registryIds;

    // connection events
    protected String onconnect;

    /**
     * Disconnection events
     */
    protected String ondisconnect;
    protected MetadataReportConfig metadataReportConfig;

    protected ConfigCenterConfig configCenter;

    // callback limits
    private Integer callbacks;
    // the scope for referring/exporting a services, if it's local, it means searching in current JVM only.
    private String scope;

    private String tag;

    /**
     * @param local
     * @see AbstractInterfaceBuilder#stub(String)
     * @deprecated Replace to <code>stub(String)</code>
     */
    @Deprecated
    public B local(String local) {
        this.local = local;
        return getThis();
    }

    /**
     * @param local
     * @see AbstractInterfaceBuilder#stub(Boolean)
     * @deprecated Replace to <code>stub(Boolean)</code>
     */
    @Deprecated
    public B local(Boolean local) {
        if (local != null) {
            this.local = local.toString();
        } else {
            this.local = null;
        }
        return getThis();
    }

    public B stub(String stub) {
        this.stub = stub;
        return getThis();
    }

    public B stub(Boolean stub) {
        if (stub != null) {
            this.stub = stub.toString();
        } else {
            this.stub = null;
        }
        return getThis();
    }

    public B monitor(MonitorConfig monitor) {
        return getThis();
    }

    public B monitor(String monitor) {
        return getThis();
    }

    public B proxy(String proxy) {
        this.proxy = proxy;
        return getThis();
    }

    public B cluster(String cluster) {
        this.cluster = cluster;
        return getThis();
    }

    public B filter(String filter) {
        this.filter = filter;
        return getThis();
    }

    public B listener(String listener) {
        this.listener = listener;
        return getThis();
    }

    public B owner(String owner) {
        this.owner = owner;
        return getThis();
    }

    public B connections(Integer connections) {
        this.connections = connections;
        return getThis();
    }

    public B layer(String layer) {
        this.layer = layer;
        return getThis();
    }

    public B application(ApplicationConfig application) {
        this.application = application;
        return getThis();
    }

    public B module(ModuleConfig module) {
        this.module = module;
        return getThis();
    }

    public B addRegistries(List<RegistryConfig> registries) {
        if (this.registries == null) {
            this.registries = new ArrayList<>();
        }
        this.registries.addAll(registries);
        return getThis();
    }

    public B addRegistry(RegistryConfig registry) {
        if (this.registries == null) {
            this.registries = new ArrayList<>();
        }
        this.registries.add(registry);
        return getThis();
    }

    public B registryIds(String registryIds) {
        this.registryIds = registryIds;
        return getThis();
    }

    public B onconnect(String onconnect) {
        this.onconnect = onconnect;
        return getThis();
    }

    public B ondisconnect(String ondisconnect) {
        this.ondisconnect = ondisconnect;
        return getThis();
    }

    public B metadataReportConfig(MetadataReportConfig metadataReportConfig) {
        this.metadataReportConfig = metadataReportConfig;
        return getThis();
    }

    public B configCenter(ConfigCenterConfig configCenter) {
        this.configCenter = configCenter;
        return getThis();
    }

    public B callbacks(Integer callbacks) {
        this.callbacks = callbacks;
        return getThis();
    }

    public B scope(String scope) {
        this.scope = scope;
        return getThis();
    }

    public B tag(String tag) {
        this.tag = tag;
        return getThis();
    }

    @Override
    public void build(T instance) {
        super.build(instance);

        if (!StringUtils.isEmpty(local)) {
            instance.setLocal(local);
        }
        if (!StringUtils.isEmpty(stub)) {
            instance.setStub(stub);
        }

        if (!StringUtils.isEmpty(proxy)) {
            instance.setProxy(proxy);
        }
        if (!StringUtils.isEmpty(cluster)) {
            instance.setCluster(cluster);
        }
        if (!StringUtils.isEmpty(filter)) {
            instance.setFilter(filter);
        }
        if (!StringUtils.isEmpty(listener)) {
            instance.setListener(listener);
        }
        if (!StringUtils.isEmpty(owner)) {
            instance.setOwner(owner);
        }
        if (connections != null) {
            instance.setConnections(connections);
        }
        if (!StringUtils.isEmpty(layer)) {
            instance.setLayer(layer);
        }
        if (application != null) {
            instance.setApplication(application);
        }
        if (module != null) {
            instance.setModule(module);
        }
        if (registries != null) {
            instance.setRegistries(registries);
        }
        if (!StringUtils.isEmpty(registryIds)) {
            instance.setRegistryIds(registryIds);
        }
        if (!StringUtils.isEmpty(onconnect)) {
            instance.setOnconnect(onconnect);
        }
        if (!StringUtils.isEmpty(ondisconnect)) {
            instance.setOndisconnect(ondisconnect);
        }
        if (metadataReportConfig != null) {
            instance.setMetadataReportConfig(metadataReportConfig);
        }
        if (configCenter != null) {
            instance.setConfigCenter(configCenter);
        }
        if (callbacks != null) {
            instance.setCallbacks(callbacks);
        }
        if (!StringUtils.isEmpty(scope)) {
            instance.setScope(scope);
        }
        if (StringUtils.isNotEmpty(tag)) {
            instance.setTag(tag);
        }
    }
}

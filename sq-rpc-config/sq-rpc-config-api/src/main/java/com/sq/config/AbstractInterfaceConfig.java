package com.sq.config;


import static com.sq.common.config.ConfigurationUtils.parseProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.URLBuilder;
import com.sq.common.Version;
import com.sq.common.config.Environment;
import com.sq.common.extension.ExtensionLoader;
import com.sq.common.utils.CollectionUtils;
import com.sq.common.utils.ConfigUtils;
import com.sq.common.utils.ReflectUtils;
import com.sq.common.utils.StringUtils;
import com.sq.common.utils.UrlUtils;
import com.sq.config.context.ConfigManager;
import com.sq.config.support.Parameter;
import com.sq.registry.RegistryService;
import com.sq.rpc.Filter;
import com.sq.rpc.InvokerListener;
import com.sq.rpc.ProxyFactory;
import com.sq.rpc.cluster.Cluster;
import com.sq.rpc.cluster.router.condition.config.center.DynamicConfiguration;
import com.sq.rpc.cluster.router.condition.config.center.DynamicConfigurationFactory;
import com.sq.rpc.module.ApplicationModel;

/**
 * AbstractDefaultConfig
 *
 * @export
 */
public abstract class AbstractInterfaceConfig extends AbstractMethodConfig {

    private static final long serialVersionUID = -1559314110797223229L;

    /**
     * Local impl class name for the services interface
     */
    protected String local;

    /**
     * Local stub class name for the services interface
     */
    protected String stub;

    /**
     * Service monitor
     */
    protected MonitorConfig monitor;

    /**
     * Strategies for generating dynamic agents，there are two strategies can be choosed: jdk and javassist
     */
    protected String proxy;

    /**
     * Cluster type
     */
    protected String cluster;

    /**
     * The when the provider side exposed a services or the customer side references a remote services used,
     * if there are more than one, you can use commas to separate them
     */
    protected String filter;

    protected String interceptor;

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

    /**
     * The metrics configuration
     */
    protected MetricsConfig metrics;
    protected MetadataReportConfig metadataReportConfig;

    protected ConfigCenterConfig configCenter;

    // callback limits
    private Integer callbacks;
    // the scope for referring/exporting a services, if it's local, it means searching in current JVM only.
    private String scope;

    protected String tag;

    /**
     * Check whether the registry config is exists, and then conversion it to {@link RegistryConfig}
     */
    protected void checkRegistry() {
        loadRegistriesFromBackwardConfig();

        convertRegistryIdsToRegistries();

        for (RegistryConfig registryConfig : registries) {
            if (!registryConfig.isValid()) {
                throw new IllegalStateException("No registry config found or it's not a valid config! " +
                        "The registry config is: " + registryConfig);
            }
        }

        /*useRegistryForConfigIfNecessary();*/
    }

    @SuppressWarnings("deprecation")
    protected void checkApplication() {
        // for backward compatibility
        createApplicationIfAbsent();

        if (!application.isValid()) {
            throw new IllegalStateException("No application config found or it's not a valid config! " +
                    "Please add <sq-rpc:application name=\"...\" /> to your spring config.");
        }

        ApplicationModel.setApplication(application.getName());

        // backward compatibility
        String wait = ConfigUtils.getProperty(Constants.SHUTDOWN_WAIT_KEY);
        if (wait != null && wait.trim().length() > 0) {
            System.setProperty(Constants.SHUTDOWN_WAIT_KEY, wait.trim());
        } else {
            wait = ConfigUtils.getProperty(Constants.SHUTDOWN_WAIT_SECONDS_KEY);
            if (wait != null && wait.trim().length() > 0) {
                System.setProperty(Constants.SHUTDOWN_WAIT_SECONDS_KEY, wait.trim());
            }
        }
    }

    protected void checkMonitor() {
        createMonitorIfAbsent();
        if (!monitor.isValid()) {
            logger.info("There's no valid monitor config found, if you want to open monitor statistics, " +
                    "please make sure your monitor is configured properly.");
        }
    }

    private void createMonitorIfAbsent() {
        if (this.monitor != null) {
            return;
        }
        ConfigManager configManager = ConfigManager.getInstance();
        setMonitor(
                configManager
                        .getMonitor()
                        .orElseGet(() -> {
                            return new MonitorConfig();
                        })
        );
    }

    protected void checkMetadataReport() {
        // TODO get from ConfigManager first, only create if absent.
        if (metadataReportConfig == null) {
            setMetadataReportConfig(new MetadataReportConfig());
        }
        if (!metadataReportConfig.isValid()) {
            logger.warn("There's no valid metadata config found, if you are using the simplified mode of registry url, " +
                    "please make sure you have a metadata address configured properly.");
        }
    }


    void startConfigCenter() {
        if (configCenter == null) {
            ConfigManager.getInstance().getConfigCenter().ifPresent(cc -> this.configCenter = cc);
        }

        if (this.configCenter != null) {
            // TODO there may have duplicate refresh
            prepareEnvironment();
        }
        ConfigManager.getInstance().refreshAll();
    }

    private void prepareEnvironment() {
        if (configCenter.isValid()) {
            if (!configCenter.checkOrUpdateInited()) {
                return;
            }
            DynamicConfiguration dynamicConfiguration = getDynamicConfiguration(configCenter.toUrl());
            String configContent = dynamicConfiguration.getConfig(configCenter.getConfigFile(), configCenter.getGroup());

            String appGroup = application != null ? application.getName() : null;
            String appConfigContent = null;
            if (StringUtils.isNotEmpty(appGroup)) {
                appConfigContent = dynamicConfiguration.getConfig
                        (StringUtils.isNotEmpty(configCenter.getAppConfigFile()) ? configCenter.getAppConfigFile() : configCenter.getConfigFile(),
                                appGroup
                        );
            }
            try {
                Environment.getInstance().setConfigCenterFirst(configCenter.isHighestPriority());
                Environment.getInstance().updateExternalConfigurationMap(parseProperties(configContent));
                Environment.getInstance().updateAppExternalConfigurationMap(parseProperties(appConfigContent));
            } catch (IOException e) {
                throw new IllegalStateException("Failed to parse configurations from Config Center.", e);
            }
        }
    }

    private DynamicConfiguration getDynamicConfiguration(URL url) {
        DynamicConfigurationFactory factories = ExtensionLoader
                .getExtensionLoader(DynamicConfigurationFactory.class)
                .getExtension(url.getProtocol());
        DynamicConfiguration configuration = factories.getDynamicConfiguration(url);
        Environment.getInstance().setDynamicConfiguration(configuration);
        return configuration;
    }

    /**
     **
     * @param provider whether it is the provider side
     * @return
     */
    protected List<URL> loadRegistries(boolean provider) {
        // check && override if necessary
        List<URL> registryList = new ArrayList<URL>();
        if (CollectionUtils.isNotEmpty(registries)) {
            for (RegistryConfig config : registries) {
                String address = config.getAddress();
                if (StringUtils.isEmpty(address)) {
                    address = Constants.ANYHOST_VALUE;
                }
                if (!RegistryConfig.NO_AVAILABLE.equalsIgnoreCase(address)) {
                    Map<String, String> map = new HashMap<String, String>();
                    appendParameters(map, application);
                    appendParameters(map, config);
                    map.put(Constants.PATH_KEY, RegistryService.class.getName());
                    appendRuntimeParameters(map);
                    if (!map.containsKey(Constants.PROTOCOL_KEY)) {
                        map.put(Constants.PROTOCOL_KEY, Constants.SQ_RPC_PROTOCOL);
                    }
                    List<URL> urls = UrlUtils.parseURLs(address, map);

                    for (URL url : urls) {
                        url = URLBuilder.from(url)
                                .addParameter(Constants.REGISTRY_KEY, url.getProtocol())
                                .setProtocol(Constants.REGISTRY_PROTOCOL)
                                .build();
                        if ((provider && url.getParameter(Constants.REGISTER_KEY, true))
                                || (!provider && url.getParameter(Constants.SUBSCRIBE_KEY, true))) {
                            registryList.add(url);
                        }
                    }
                }
            }
        }
        return registryList;
    }


    static void appendRuntimeParameters(Map<String, String> map) {
        map.put(Constants.GRPC_VERSION_KEY, Version.getProtocolVersion());
        map.put(Constants.RELEASE_KEY, Version.getVersion());
        map.put(Constants.TIMESTAMP_KEY, String.valueOf(System.currentTimeMillis()));
        if (ConfigUtils.getPid() > 0) {
            map.put(Constants.PID_KEY, String.valueOf(ConfigUtils.getPid()));
        }
    }

    private URL loadMetadataReporterURL() {
        String address = metadataReportConfig.getAddress();
        if (StringUtils.isEmpty(address)) {
            return null;
        }
        Map<String, String> map = new HashMap<String, String>();
        appendParameters(map, metadataReportConfig);
        return UrlUtils.parseURL(address, map);
    }

    /**
     * Check whether the remote services interface and the methods meet with rpc's requirements.it
     * mainly check, if the
     * methods configured in the configuration file are included in the interface of remote services
     *
     * @param interfaceClass the interface of remote services
     * @param methods the methods configured
     */
    protected void checkInterfaceAndMethods(Class<?> interfaceClass, List<MethodConfig> methods) {
        // interface cannot be null

        // to verify interfaceClass is an interface
        if (!interfaceClass.isInterface()) {
            throw new IllegalStateException("The interface class " + interfaceClass + " is not a interface!");
        }
        // check if methods exist in the remote services interface
        if (CollectionUtils.isNotEmpty(methods)) {
            for (MethodConfig methodBean : methods) {
                methodBean.setService(interfaceClass.getName());
                methodBean.setServiceId(this.getId());
                String methodName = methodBean.getName();
                if (StringUtils.isEmpty(methodName)) {
                    throw new IllegalStateException("<sq-rpc:method> name attribute is required! " +
                            "Please check: " +
                            "<sq-rpc:services interface=\"" + interfaceClass.getName() + "\" ... >" +
                            "<sq-rpc:method name=\"\" ... /></<sq-rpc:reference>");
                }

                boolean hasMethod = Arrays.stream(interfaceClass.getMethods()).anyMatch(method -> method.getName().equals(methodName));
                if (!hasMethod) {
                    throw new IllegalStateException("The interface " + interfaceClass.getName()
                            + " not found method " + methodName);
                }
            }
        }
    }


    /**
     * Legitimacy check of stub, note that: the local will deprecated, and replace with <code>stub</code>
     *
     * @param interfaceClass for provider side, it is the {@link Class} of the services that will be exported; for consumer
     *                       side, it is the {@link Class} of the remote services interface
     */
    void checkStubAndLocal(Class<?> interfaceClass) {
        if (ConfigUtils.isNotEmpty(local)) {
            Class<?> localClass = ConfigUtils.isDefault(local) ?
                    ReflectUtils.forName(interfaceClass.getName() + "Local") : ReflectUtils.forName(local);
            verify(interfaceClass, localClass);
        }
        if (ConfigUtils.isNotEmpty(stub)) {
            Class<?> localClass = ConfigUtils.isDefault(stub) ?
                    ReflectUtils.forName(interfaceClass.getName() + "Stub") : ReflectUtils.forName(stub);
            verify(interfaceClass, localClass);
        }
    }

    private void verify(Class<?> interfaceClass, Class<?> localClass) {
        if (!interfaceClass.isAssignableFrom(localClass)) {
            throw new IllegalStateException("The local implementation class " + localClass.getName() +
                    " not implement interface " + interfaceClass.getName());
        }

        try {
            //Check if the localClass a constructor with parameter who's type is interfaceClass
            ReflectUtils.findConstructor(localClass, interfaceClass);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("No such constructor \"public " + localClass.getSimpleName() +
                    "(" + interfaceClass.getName() + ")\" in local implementation class " + localClass.getName());
        }
    }

    private void convertRegistryIdsToRegistries() {
        /*if (StringUtils.isEmpty(registryIds) && CollectionUtils.isEmpty(registries)) {
            Set<String> configedRegistries = new HashSet<>();
            configedRegistries.addAll(getSubProperties(Environment.getInstance().getExternalConfigurationMap(),
                    Constants.REGISTRIES_SUFFIX));
            configedRegistries.addAll(getSubProperties(Environment.getInstance().getAppExternalConfigurationMap(),
                    Constants.REGISTRIES_SUFFIX));

            registryIds = String.join(Constants.COMMA_SEPARATOR, configedRegistries);
        }*/

        if (StringUtils.isEmpty(registryIds)) {
            if (CollectionUtils.isEmpty(registries)) {
                setRegistries(
                        ConfigManager.getInstance().getDefaultRegistries()
                        .filter(CollectionUtils::isNotEmpty)
                        .orElseGet(() -> {
                            RegistryConfig registryConfig = new RegistryConfig();
                            registryConfig.refresh();
                            return Arrays.asList(registryConfig);
                        })
                );
            }
        } else {
            String[] ids = Constants.COMMA_SPLIT_PATTERN.split(registryIds);
            List<RegistryConfig> tmpRegistries = CollectionUtils.isNotEmpty(registries) ? registries : new ArrayList<>();
            Arrays.stream(ids).forEach(id -> {
                if (tmpRegistries.stream().noneMatch(reg -> reg.getId().equals(id))) {
                    tmpRegistries.add(ConfigManager.getInstance().getRegistry(id).orElseGet(() -> {
                        RegistryConfig registryConfig = new RegistryConfig();
                        registryConfig.setId(id);
                        registryConfig.refresh();
                        return registryConfig;
                    }));
                }
            });

            if (tmpRegistries.size() > ids.length) {
                throw new IllegalStateException("Too much registries found, the registries assigned to this services " +
                        "are :" + registryIds + ", but got " + tmpRegistries.size() + " registries!");
            }

            setRegistries(tmpRegistries);
        }

    }

    private void loadRegistriesFromBackwardConfig() {
        // for backward compatibility
        // -Drpc.registry.address is now deprecated.
        if (registries == null || registries.isEmpty()) {
            String address = ConfigUtils.getProperty("rpc.registry.address");
            if (address != null && address.length() > 0) {
                List<RegistryConfig> tmpRegistries = new ArrayList<RegistryConfig>();
                String[] as = address.split("\\s*[|]+\\s*");
                for (String a : as) {
                    RegistryConfig registryConfig = new RegistryConfig();
                    registryConfig.setAddress(a);
                    registryConfig.refresh();
                    tmpRegistries.add(registryConfig);
                }
                setRegistries(tmpRegistries);
            }
        }
    }

    /**
     * For compatibility purpose, use registry as the default config center if the registry protocol is zookeeper and
     * there's no config center specified explicitly.
     */
    private void useRegistryForConfigIfNecessary() {
        registries.stream().filter(RegistryConfig::isZookeeperProtocol).findFirst().ifPresent(rc -> {
            // we use the loading status of DynamicConfiguration to decide whether ConfigCenter has been initiated.
            Environment.getInstance().getDynamicConfiguration().orElseGet(() -> {
                ConfigManager configManager = ConfigManager.getInstance();
                ConfigCenterConfig cc = configManager.getConfigCenter().orElse(new ConfigCenterConfig());
                cc.setProtocol(rc.getProtocol());
                cc.setAddress(rc.getAddress());
                cc.setHighestPriority(false);
                setConfigCenter(cc);
                startConfigCenter();
                return null;
            });
        });
    }

    /**
     * @return local
     * @deprecated Replace to <code>getStub()</code>
     */
    @Deprecated
    public String getLocal() {
        return local;
    }

    /**
     * @param local
     * @deprecated Replace to <code>setStub(Boolean)</code>
     */
    @Deprecated
    public void setLocal(Boolean local) {
        if (local == null) {
            setLocal((String) null);
        } else {
            setLocal(local.toString());
        }
    }

    /**
     * @param local
     * @deprecated Replace to <code>setStub(String)</code>
     */
    @Deprecated
    public void setLocal(String local) {
        checkName(Constants.LOCAL_KEY, local);
        this.local = local;
    }

    public String getStub() {
        return stub;
    }

    public void setStub(Boolean stub) {
        if (stub == null) {
            setStub((String) null);
        } else {
            setStub(stub.toString());
        }
    }

    public void setStub(String stub) {
        checkName("stub", stub);
        this.stub = stub;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        checkExtension(Cluster.class, Constants.CLUSTER_KEY, cluster);
        this.cluster = cluster;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        checkExtension(ProxyFactory.class, Constants.PROXY_KEY, proxy);
        this.proxy = proxy;
    }

    public Integer getConnections() {
        return connections;
    }

    public void setConnections(Integer connections) {
        this.connections = connections;
    }

    @Parameter(key = Constants.REFERENCE_FILTER_KEY, append = true)
    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        checkMultiExtension(Filter.class, Constants.FILE_KEY, filter);
        this.filter = filter;
    }

    @Parameter(key = Constants.REFERENCE_INTERCEPTOR_KEY, append = true)
    public String getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(String interceptor) {
        this.interceptor = interceptor;
    }

    @Parameter(key = Constants.INVOKER_LISTENER_KEY, append = true)
    public String getListener() {
        return listener;
    }

    public void setListener(String listener) {
        checkMultiExtension(InvokerListener.class, Constants.LISTENER_KEY, listener);
        this.listener = listener;
    }

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        checkNameHasSymbol(Constants.LAYER_KEY, layer);
        this.layer = layer;
    }

    public ApplicationConfig getApplication() {
        return application;
    }

    public void setApplication(ApplicationConfig application) {
        ConfigManager.getInstance().setApplication(application);
        this.application = application;
    }

    private void createApplicationIfAbsent() {
        if (this.application != null) {
            return;
        }
        ConfigManager configManager = ConfigManager.getInstance();
        setApplication(
                configManager
                        .getApplication()
                        .orElseGet(() -> {
                            ApplicationConfig applicationConfig = new ApplicationConfig();
                            applicationConfig.refresh();
                            return applicationConfig;
                        })
        );
    }

    public ModuleConfig getModule() {
        return module;
    }

    public void setModule(ModuleConfig module) {
        ConfigManager.getInstance().setModule(module);
        this.module = module;
    }

    public RegistryConfig getRegistry() {
        return CollectionUtils.isEmpty(registries) ? null : registries.get(0);
    }

    public void setRegistry(RegistryConfig registry) {
        List<RegistryConfig> registries = new ArrayList<RegistryConfig>(1);
        registries.add(registry);
        setRegistries(registries);
    }

    public List<RegistryConfig> getRegistries() {
        return registries;
    }

    @SuppressWarnings({"unchecked"})
    public void setRegistries(List<? extends RegistryConfig> registries) {
        ConfigManager.getInstance().addRegistries((List<RegistryConfig>) registries);
        this.registries = (List<RegistryConfig>) registries;
    }

    @Parameter(excluded = true)
    public String getRegistryIds() {
        return registryIds;
    }

    public void setRegistryIds(String registryIds) {
        this.registryIds = registryIds;
    }

    public MonitorConfig getMonitor() {
        return monitor;
    }

    public void setMonitor(String monitor) {
        setMonitor(new MonitorConfig(monitor));
    }

    public void setMonitor(MonitorConfig monitor) {
        ConfigManager.getInstance().setMonitor(monitor);
        this.monitor = monitor;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        checkMultiName("owner", owner);
        this.owner = owner;
    }

    public ConfigCenterConfig getConfigCenter() {
        return configCenter;
    }

    public void setConfigCenter(ConfigCenterConfig configCenter) {
        ConfigManager.getInstance().setConfigCenter(configCenter);
        this.configCenter = configCenter;
    }

    public Integer getCallbacks() {
        return callbacks;
    }

    public void setCallbacks(Integer callbacks) {
        this.callbacks = callbacks;
    }

    public String getOnconnect() {
        return onconnect;
    }

    public void setOnconnect(String onconnect) {
        this.onconnect = onconnect;
    }

    public String getOndisconnect() {
        return ondisconnect;
    }

    public void setOndisconnect(String ondisconnect) {
        this.ondisconnect = ondisconnect;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public MetadataReportConfig getMetadataReportConfig() {
        return metadataReportConfig;
    }

    public void setMetadataReportConfig(MetadataReportConfig metadataReportConfig) {
        this.metadataReportConfig = metadataReportConfig;
    }

    public MetricsConfig getMetrics() {
        return metrics;
    }

    public void setMetrics(MetricsConfig metrics) {
        this.metrics = metrics;
    }

    @Parameter(key = Constants.TAG_KEY, useKeyAsProperty = false)
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}

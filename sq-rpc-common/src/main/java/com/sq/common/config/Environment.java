package com.sq.common.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.sq.common.Constants;
import com.sq.common.utils.StringUtils;


/**
 * TODO load as SPI will be better?
 */
public class Environment {
    private static final Environment INSTANCE = new Environment();

    private Map<String, PropertiesConfiguration> propertiesConfigs = new ConcurrentHashMap<>();
    private Map<String, SystemConfiguration> systemConfigs = new ConcurrentHashMap<>();
    private Map<String, EnvironmentConfiguration> environmentConfigs = new ConcurrentHashMap<>();
    private Map<String, InmemoryConfiguration> externalConfigs = new ConcurrentHashMap<>();
    private Map<String, InmemoryConfiguration> appExternalConfigs = new ConcurrentHashMap<>();

    private Map<String, String> externalConfigurationMap = new HashMap<>();
    private Map<String, String> appExternalConfigurationMap = new HashMap<>();

    private boolean configCenterFirst = true;

    /**
     * FIXME, this instance will always be a type of DynamicConfiguration, ConfigCenterConfig will load the instance at startup and assign it to here.
     */
    private Configuration dynamicConfiguration;

    public static Environment getInstance() {
        return INSTANCE;
    }

    public PropertiesConfiguration getPropertiesConfig(String prefix, String id) {
        return propertiesConfigs.computeIfAbsent(toKey(prefix, id), k -> new PropertiesConfiguration(prefix, id));
    }

    public SystemConfiguration getSystemConfig(String prefix, String id) {
        return systemConfigs.computeIfAbsent(toKey(prefix, id), k -> new SystemConfiguration(prefix, id));
    }

    public InmemoryConfiguration getExternalConfig(String prefix, String id) {
        return externalConfigs.computeIfAbsent(toKey(prefix, id), k -> {
            InmemoryConfiguration configuration = new InmemoryConfiguration(prefix, id);
            configuration.setProperties(externalConfigurationMap);
            return configuration;
        });
    }

    public InmemoryConfiguration getAppExternalConfig(String prefix, String id) {
        return appExternalConfigs.computeIfAbsent(toKey(prefix, id), k -> {
            InmemoryConfiguration configuration = new InmemoryConfiguration(prefix, id);
            configuration.setProperties(appExternalConfigurationMap);
            return configuration;
        });
    }

    public EnvironmentConfiguration getEnvironmentConfig(String prefix, String id) {
        return environmentConfigs.computeIfAbsent(toKey(prefix, id), k -> new EnvironmentConfiguration(prefix, id));
    }

    public void setExternalConfigMap(Map<String, String> externalConfiguration) {
        this.externalConfigurationMap = externalConfiguration;
    }

    public void setAppExternalConfigMap(Map<String, String> appExternalConfiguration) {
        this.appExternalConfigurationMap = appExternalConfiguration;
    }

    public Map<String, String> getExternalConfigurationMap() {
        return externalConfigurationMap;
    }

    public Map<String, String> getAppExternalConfigurationMap() {
        return appExternalConfigurationMap;
    }

    public void updateExternalConfigurationMap(Map<String, String> externalMap) {
        this.externalConfigurationMap.putAll(externalMap);
    }

    public void updateAppExternalConfigurationMap(Map<String, String> externalMap) {
        this.appExternalConfigurationMap.putAll(externalMap);
    }

    /**
     * Create new instance for each call, since it will be called only at startup, I think there's no big deal of the potential cost.
     * Otherwise, if use cache, we should make sure each Config has a unique id which is difficult to guarantee because is on the user's side,
     * especially when it comes to ServiceConfig and ReferenceConfig.
     *
     * @param prefix
     * @param id
     * @return
     */
    public CompositeConfiguration getConfiguration(String prefix, String id) {
        CompositeConfiguration compositeConfiguration = new CompositeConfiguration();
        // Config center has the highest priority
        compositeConfiguration.addConfiguration(this.getSystemConfig(prefix, id));
        compositeConfiguration.addConfiguration(this.getAppExternalConfig(prefix, id));
        compositeConfiguration.addConfiguration(this.getExternalConfig(prefix, id));
        compositeConfiguration.addConfiguration(this.getPropertiesConfig(prefix, id));
        return compositeConfiguration;
    }

    public Configuration getConfiguration() {
        return getConfiguration(null, null);
    }

    private static String toKey(String prefix, String id) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(prefix)) {
            sb.append(prefix);
        }
        if (StringUtils.isNotEmpty(id)) {
            sb.append(id);
        }

        if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '.') {
            sb.append(".");
        }

        if (sb.length() > 0) {
            return sb.toString();
        }
        return Constants.SQ_RPC;
    }

    public boolean isConfigCenterFirst() {
        return configCenterFirst;
    }

    public void setConfigCenterFirst(boolean configCenterFirst) {
        this.configCenterFirst = configCenterFirst;
    }

    public Optional<Configuration> getDynamicConfiguration() {
        return Optional.ofNullable(dynamicConfiguration);
    }

    public void setDynamicConfiguration(Configuration dynamicConfiguration) {
        this.dynamicConfiguration = dynamicConfiguration;
    }

    // For test
    public void clearExternalConfigs() {
        this.externalConfigs.clear();
        this.externalConfigurationMap.clear();
    }

    // For test
    public void clearAppExternalConfigs() {
        this.appExternalConfigs.clear();
        this.appExternalConfigurationMap.clear();
    }
}

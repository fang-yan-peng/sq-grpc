package com.sq.rpc.cluster.configurator.parser.model;

import java.util.List;

/**
 *
 */
public class ConfiguratorConfig {
    public static final String SCOPE_SERVICE = "services";
    public static final String SCOPE_APPLICATION = "application";

    private String configVersion;
    private String scope;
    private String key;
    private Boolean enabled = true;
    private List<ConfigItem> configs;


    public String getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(String configVersion) {
        this.configVersion = configVersion;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<ConfigItem> getConfigs() {
        return configs;
    }

    public void setConfigs(List<ConfigItem> configs) {
        this.configs = configs;
    }
}

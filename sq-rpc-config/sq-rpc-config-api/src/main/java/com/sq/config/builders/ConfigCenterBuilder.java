package com.sq.config.builders;

import java.util.Map;

import com.sq.config.ConfigCenterConfig;

/**
 * This is a builder for build {@link ConfigCenterConfig}.
 *
 * @since 2.7
 */
public class ConfigCenterBuilder extends AbstractBuilder<ConfigCenterConfig, ConfigCenterBuilder> {

    private String protocol;
    private String address;
    private String cluster;
    private String namespace = "grpc";
    private String group = "grpc";
    private String username;
    private String password;
    private Long timeout = 3000L;
    private Boolean highestPriority = true;
    private Boolean check = true;

    private String appName;
    private String configFile = "rpc.properties";
    private String appConfigFile;

    private Map<String, String> parameters;

    public ConfigCenterBuilder protocol(String protocol) {
        this.protocol = protocol;
        return getThis();
    }

	public ConfigCenterBuilder address(String address) {
        this.address = address;
        return getThis();
    }

    public ConfigCenterBuilder cluster(String cluster) {
        this.cluster = cluster;
        return getThis();
    }

    public ConfigCenterBuilder namespace(String namespace) {
        this.namespace = namespace;
        return getThis();
    }

    public ConfigCenterBuilder group(String group) {
        this.group = group;
        return getThis();
    }

    public ConfigCenterBuilder username(String username) {
        this.username = username;
        return getThis();
    }

    public ConfigCenterBuilder password(String password) {
        this.password = password;
        return getThis();
    }

    public ConfigCenterBuilder timeout(Long timeout) {
        this.timeout = timeout;
        return getThis();
    }

    public ConfigCenterBuilder highestPriority(Boolean highestPriority) {
        this.highestPriority = highestPriority;
        return getThis();
    }

    public ConfigCenterBuilder check(Boolean check) {
        this.check = check;
        return getThis();
    }

    public ConfigCenterBuilder appName(String appName) {
        this.appName = appName;
        return getThis();
    }

    public ConfigCenterBuilder configFile(String configFile) {
        this.configFile = configFile;
        return getThis();
    }

    public ConfigCenterBuilder appConfigFile(String appConfigFile) {
        this.appConfigFile = appConfigFile;
        return getThis();
    }

    public ConfigCenterBuilder appendParameters(Map<String, String> appendParameters) {
        this.parameters = appendParameters(this.parameters, appendParameters);
        return getThis();
    }

    public ConfigCenterBuilder appendParameter(String key, String value) {
        this.parameters = appendParameter(this.parameters, key, value);
        return getThis();
    }

    public ConfigCenterConfig build() {
        ConfigCenterConfig configCenter = new ConfigCenterConfig();
        super.build(configCenter);

        configCenter.setProtocol(protocol);
        configCenter.setAddress(address);
        configCenter.setCluster(cluster);
        configCenter.setNamespace(namespace);
        configCenter.setGroup(group);
        configCenter.setUsername(username);
        configCenter.setPassword(password);
        configCenter.setTimeout(timeout);
        configCenter.setHighestPriority(highestPriority);
        configCenter.setCheck(check);
        configCenter.setAppName(appName);
        configCenter.setConfigFile(configFile);
        configCenter.setAppConfigFile(appConfigFile);
        configCenter.setParameters(parameters);

        return configCenter;
    }

    @Override
    protected ConfigCenterBuilder getThis() {
        return this;
    }
}

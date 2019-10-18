package com.sq.config.builders;

import java.util.Map;

import com.sq.config.MonitorConfig;

/**
 * This is a builder for build {@link MonitorConfig}.
 *
 * @since 2.7
 */
public class MonitorBuilder extends AbstractBuilder<MonitorConfig, MonitorBuilder> {
    /**
     * The protocol of the monitor, if the value is registry, it will search the monitor address from the registry center,
     * otherwise, it will directly connect to the monitor center
     */
    private String protocol;

    /**
     * The monitor address
     */
    private String address;

    /**
     * The monitor user name
     */
    private String username;

    /**
     * The password
     */
    private String password;

    private String group;

    private String version;

    private String interval;

    /**
     * customized parameters
     */
    private Map<String, String> parameters;

    /**
     * If it's default
     */
    private Boolean isDefault;

    public MonitorBuilder protocol(String protocol) {
        this.protocol = protocol;
        return getThis();
    }

    public MonitorBuilder address(String address) {
        this.address = address;
        return getThis();
    }

    public MonitorBuilder username(String username) {
        this.username = username;
        return getThis();
    }

    public MonitorBuilder password(String password) {
        this.password = password;
        return getThis();
    }

    public MonitorBuilder group(String group) {
        this.group = group;
        return getThis();
    }

    public MonitorBuilder version(String version) {
        this.version = version;
        return getThis();
    }

    public MonitorBuilder interval(String interval) {
        this.interval = interval;
        return getThis();
    }

    public MonitorBuilder isDefault(Boolean isDefault) {
        this.isDefault = isDefault;
        return getThis();
    }

    public MonitorBuilder appendParameter(String key, String value) {
        this.parameters = appendParameter(parameters, key, value);
        return getThis();
    }

    public MonitorBuilder appendParameters(Map<String, String> appendParameters) {
        this.parameters = appendParameters(parameters, appendParameters);
        return getThis();
    }

    public MonitorConfig build() {
        MonitorConfig monitorConfig = new MonitorConfig();
        super.build(monitorConfig);

        monitorConfig.setProtocol(protocol);
        monitorConfig.setAddress(address);
        monitorConfig.setUsername(username);
        monitorConfig.setPassword(password);
        monitorConfig.setGroup(group);
        monitorConfig.setVersion(version);
        monitorConfig.setInterval(interval);
        monitorConfig.setParameters(parameters);
        monitorConfig.setDefault(isDefault);

        return monitorConfig;
    }

    @Override
    protected MonitorBuilder getThis() {
        return this;
    }
}

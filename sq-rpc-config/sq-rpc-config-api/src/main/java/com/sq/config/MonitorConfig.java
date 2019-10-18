package com.sq.config;

import java.util.Map;

import com.sq.common.utils.StringUtils;
import com.sq.config.support.Parameter;

/**
 * MonitorConfig
 *
 * @export
 */
public class MonitorConfig extends AbstractConfig {

    private static final long serialVersionUID = -1184681514659198203L;

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

    public MonitorConfig() {
    }

    public MonitorConfig(String address) {
        this.address = address;
    }

    @Parameter(excluded = true)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Parameter(excluded = true)
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Parameter(excluded = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Parameter(excluded = true)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        checkParameterName(parameters);
        this.parameters = parameters;
    }

    public Boolean isDefault() {
        return isDefault;
    }

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    @Override
    @Parameter(excluded = true)
    public boolean isValid() {
        return StringUtils.isNotEmpty(address);
    }

}

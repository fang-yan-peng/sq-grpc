package com.sq.config;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.config.Environment;
import com.sq.common.utils.StringUtils;
import com.sq.common.utils.UrlUtils;
import com.sq.config.support.Parameter;

/**
 * ConfigCenterConfig
 */
public class ConfigCenterConfig extends AbstractConfig {
    private AtomicBoolean inited = new AtomicBoolean(false);

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

    // customized parameters
    private Map<String, String> parameters;

    public ConfigCenterConfig() {
    }

    public URL toUrl() {
        Map<String, String> map = this.getMetaData();
        if (StringUtils.isEmpty(address)) {
            address = Constants.ANYHOST_VALUE;
        }
        map.put(Constants.PATH_KEY, ConfigCenterConfig.class.getSimpleName());
        // use 'zookeeper' as the default configcenter.
        if (StringUtils.isEmpty(map.get(Constants.PROTOCOL_KEY))) {
            map.put(Constants.PROTOCOL_KEY, Constants.ZOOKEEPER_PROTOCOL);
        }
        return UrlUtils.parseURL(address, map);
    }

    public boolean checkOrUpdateInited() {
        return inited.compareAndSet(false, true);
    }

    public void setExternalConfig(Map<String, String> externalConfiguration) {
        Environment.getInstance().setExternalConfigMap(externalConfiguration);
    }

    public void setAppExternalConfig(Map<String, String> appExternalConfiguration) {
        Environment.getInstance().setAppExternalConfigMap(appExternalConfiguration);
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Parameter(excluded = true)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Parameter(key = Constants.CONFIG_CLUSTER_KEY, useKeyAsProperty = false)
    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    @Parameter(key = Constants.CONFIG_NAMESPACE_KEY, useKeyAsProperty = false)
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Parameter(key = Constants.CONFIG_GROUP_KEY, useKeyAsProperty = false)
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Parameter(key = Constants.CONFIG_CHECK_KEY, useKeyAsProperty = false)
    public Boolean isCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    @Parameter(key = Constants.CONFIG_ENABLE_KEY, useKeyAsProperty = false)
    public Boolean isHighestPriority() {
        return highestPriority;
    }

    public void setHighestPriority(Boolean highestPriority) {
        this.highestPriority = highestPriority;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Parameter(key = Constants.CONFIG_TIMEOUT_KEY, useKeyAsProperty = false)
    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    @Parameter(key = Constants.CONFIG_CONFIGFILE_KEY, useKeyAsProperty = false)
    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    @Parameter(excluded = true)
    public String getAppConfigFile() {
        return appConfigFile;
    }

    public void setAppConfigFile(String appConfigFile) {
        this.appConfigFile = appConfigFile;
    }

    @Parameter(key = Constants.CONFIG_APPNAME_KEY, useKeyAsProperty = false)
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        checkParameterName(parameters);
        this.parameters = parameters;
    }

    @Override
    @Parameter(excluded = true)
    public boolean isValid() {
        if (StringUtils.isEmpty(address)) {
            return false;
        }

        return address.contains("://") || StringUtils.isNotEmpty(protocol);
    }
}

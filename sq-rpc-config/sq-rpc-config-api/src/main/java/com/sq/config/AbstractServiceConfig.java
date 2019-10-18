package com.sq.config;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sq.common.Constants;
import com.sq.common.utils.CollectionUtils;
import com.sq.config.context.ConfigManager;
import com.sq.config.support.Parameter;

/**
 * AbstractServiceConfig
 *
 * @export
 */
public abstract class AbstractServiceConfig extends AbstractInterfaceConfig {

    private static final long serialVersionUID = 1L;

    /**
     * The services version
     */
    protected String version;

    /**
     * The services group
     */
    protected String group;

    /**
     * whether the services is deprecated
     */
    protected Boolean deprecated = false;

    /**
     * The time delay register services (milliseconds)
     */
    protected Integer delay;

    /**
     * Whether to export the services
     */
    protected Boolean export;

    /**
     * The services weight
     */
    protected Integer weight;

    /**
     * Document center
     */
    protected String document;

    /**
     * Whether to register as a dynamic services or not on register center, the value is true, the status will be enabled
     * after the services registered,and it needs to be disabled manually; if you want to disable the services, you also need
     * manual processing
     */
    protected Boolean dynamic = true;

    /**
     * Whether to use token
     */
    protected String token;

    /**
     * Whether to export access logs to logs
     */
    protected String accesslog;

    /**
     * The protocol list the services will export with
     */
    protected List<ProtocolConfig> protocols;

    protected String protocolIds;

    // max allowed execute times
    private Integer executes;

    /**
     * Whether to register
     */
    private Boolean register = true;

    /**
     * Warm up period
     */
    private Integer warmup;

    /**
     * The serialization type
     */
    private String serialization;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        checkKey(Constants.VERSION_KEY, version);
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        checkKey(Constants.GROUP_KEY, group);
        this.group = group;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Boolean getExport() {
        return export;
    }

    public void setExport(Boolean export) {
        this.export = export;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Parameter(escaped = true)
    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getToken() {
        return token;
    }

    public void setToken(Boolean token) {
        if (token == null) {
            setToken((String) null);
        } else {
            setToken(String.valueOf(token));
        }
    }

    public void setToken(String token) {
        checkName(Constants.TOKEN_KEY, token);
        this.token = token;
    }

    public Boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public Boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(Boolean dynamic) {
        this.dynamic = dynamic;
    }

    public List<ProtocolConfig> getProtocols() {
        return protocols;
    }

    @SuppressWarnings({"unchecked"})
    public void setProtocols(List<? extends ProtocolConfig> protocols) {
        ConfigManager.getInstance().addProtocols((List<ProtocolConfig>) protocols);
        this.protocols = (List<ProtocolConfig>) protocols;
    }

    public ProtocolConfig getProtocol() {
        return CollectionUtils.isEmpty(protocols) ? null : protocols.get(0);
    }

    public void setProtocol(ProtocolConfig protocol) {
        setProtocols(new ArrayList<>(Arrays.asList(protocol)));
    }

    @Parameter(excluded = true)
    public String getProtocolIds() {
        return protocolIds;
    }

    public void setProtocolIds(String protocolIds) {
        this.protocolIds = protocolIds;
    }

    public String getAccesslog() {
        return accesslog;
    }

    public void setAccesslog(Boolean accesslog) {
        if (accesslog == null) {
            setAccesslog((String) null);
        } else {
            setAccesslog(String.valueOf(accesslog));
        }
    }

    public void setAccesslog(String accesslog) {
        this.accesslog = accesslog;
    }

    public Integer getExecutes() {
        return executes;
    }

    public void setExecutes(Integer executes) {
        this.executes = executes;
    }

    @Override
    @Parameter(key = Constants.SERVICE_FILTER_KEY, append = true)
    public String getFilter() {
        return super.getFilter();
    }

    @Override
    @Parameter(key = Constants.SERVICE_INTERCEPTOR_KEY, append = true)
    public String getInterceptor() {
        return super.getInterceptor();
    }

    @Override
    @Parameter(key = Constants.EXPORTER_LISTENER_KEY, append = true)
    public String getListener() {
        return listener;
    }

    @Override
    public void setListener(String listener) {
        this.listener = listener;
    }

    public Boolean isRegister() {
        return register;
    }

    public void setRegister(Boolean register) {
        this.register = register;
    }

    public Integer getWarmup() {
        return warmup;
    }

    public void setWarmup(Integer warmup) {
        this.warmup = warmup;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        this.serialization = serialization;
    }
}

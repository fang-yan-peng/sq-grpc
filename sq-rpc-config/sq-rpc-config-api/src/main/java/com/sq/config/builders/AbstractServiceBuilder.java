package com.sq.config.builders;

import java.util.ArrayList;
import java.util.List;

import com.sq.common.utils.StringUtils;
import com.sq.config.AbstractServiceConfig;
import com.sq.config.ProtocolConfig;

/**
 * AbstractBuilder
 *
 * @since 2.7
 */
public abstract class AbstractServiceBuilder<T extends AbstractServiceConfig, B extends AbstractServiceBuilder<T, B>>
        extends AbstractInterfaceBuilder<T, B> {

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
    protected Boolean deprecated;

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
     * Whether to register as a dynamic services or not on register center, it the value is false, the status will be disabled
     * after the services registered,and it needs to be enabled manually; if you want to disable the services, you also need
     * manual processing
     */
    protected Boolean dynamic;

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
    private Boolean register;

    /**
     * Warm up period
     */
    private Integer warmup;

    /**
     * The serialization type
     */
    private String serialization;

    public B version(String version) {
        this.version = version;
        return getThis();
    }

    public B group(String group) {
        this.group = group;
        return getThis();
    }

    public B deprecated(Boolean deprecated) {
        this.deprecated = deprecated;
        return getThis();
    }

    public B delay(Integer delay) {
        this.delay = delay;
        return getThis();
    }

    public B export(Boolean export) {
        this.export = export;
        return getThis();
    }

    public B weight(Integer weight) {
        this.weight = weight;
        return getThis();
    }

    public B document(String document) {
        this.document = document;
        return getThis();
    }

    public B dynamic(Boolean dynamic) {
        this.dynamic = dynamic;
        return getThis();
    }

    public B token(String token) {
        this.token = token;
        return getThis();
    }

    public B token(Boolean token) {
        if (token != null) {
            this.token = token.toString();
        } else {
            this.token = null;
        }
        return getThis();
    }

    public B accesslog(String accesslog) {
        this.accesslog = accesslog;
        return getThis();
    }

    public B accesslog(Boolean accesslog) {
        if (accesslog != null) {
            this.accesslog = accesslog.toString();
        } else {
            this.accesslog = null;
        }
        return getThis();
    }

    public B addProtocols(List<ProtocolConfig> protocols) {
        if (this.protocols == null) {
            this.protocols = new ArrayList<>();
        }
        this.protocols.addAll(protocols);
        return getThis();
    }

    public B addProtocol(ProtocolConfig protocol) {
        if (this.protocols == null) {
            this.protocols = new ArrayList<>();
        }
        this.protocols.add(protocol);
        return getThis();
    }

    public B protocolIds(String protocolIds) {
        this.protocolIds = protocolIds;
        return getThis();
    }

    public B executes(Integer executes) {
        this.executes = executes;
        return getThis();
    }

    public B register(Boolean register) {
        this.register = register;
        return getThis();
    }

    public B warmup(Integer warmup) {
        this.warmup = warmup;
        return getThis();
    }

    public  B serialization(String serialization) {
        this.serialization = serialization;
        return getThis();
    }

    @Override
    public void build(T instance) {
        super.build(instance);

        if (!StringUtils.isEmpty(version)) {
            instance.setVersion(version);
        }
        if (!StringUtils.isEmpty(group)) {
            instance.setGroup(group);
        }
        if (deprecated != null) {
            instance.setDeprecated(deprecated);
        }
        if (delay != null) {
            instance.setDelay(delay);
        }
        if (export != null) {
            instance.setExport(export);
        }
        if (weight != null) {
            instance.setWeight(weight);
        }
        if (!StringUtils.isEmpty(document)) {
            instance.setDocument(document);
        }
        if (dynamic != null) {
            instance.setDynamic(dynamic);
        }
        if (!StringUtils.isEmpty(token)) {
            instance.setToken(token);
        }
        if (!StringUtils.isEmpty(accesslog)) {
            instance.setAccesslog(accesslog);
        }
        if (protocols != null) {
            instance.setProtocols(protocols);
        }
        if (!StringUtils.isEmpty(protocolIds)) {
            instance.setProtocolIds(protocolIds);
        }
        if (executes != null) {
            instance.setExecutes(executes);
        }
        if (register != null) {
            instance.setRegister(register);
        }
        if (warmup != null) {
            instance.setWarmup(warmup);
        }
        if (!StringUtils.isEmpty(serialization)) {
            instance.setSerialization(serialization);
        }
    }
}

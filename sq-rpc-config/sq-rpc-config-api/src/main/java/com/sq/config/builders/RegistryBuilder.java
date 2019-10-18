package com.sq.config.builders;

import java.util.Map;

import com.sq.config.RegistryConfig;


/**
 * This is a builder for build {@link RegistryConfig}.
 *
 * @since 2.7
 */
public class RegistryBuilder extends AbstractBuilder<RegistryConfig, RegistryBuilder> {
    /**
     * Register center address
     */
    private String address;

    /**
     * Username to login register center
     */
    private String username;

    /**
     * Password to login register center
     */
    private String password;

    /**
     * Default port for register center
     */
    private Integer port;

    /**
     * Protocol for register center
     */
    private String protocol;

    /**
     * Network transmission type
     */
    private String transporter;

    private String server;

    private String client;

    private String cluster;

    /**
     * The group the services registry in
     */
    private String group;

    private String version;

    /**
     * Request timeout in milliseconds for register center
     */
    private Integer timeout;

    /**
     * Session timeout in milliseconds for register center
     */
    private Integer session;

    /**
     * File for saving register center dynamic list
     */
    private String file;

    /**
     * Wait time before stop
     */
    private Integer wait;

    /**
     * Whether to check if register center is available when boot up
     */
    private Boolean check;

    /**
     * Whether to allow dynamic services to register on the register center
     */
    private Boolean dynamic;

    /**
     * Whether to export services on the register center
     */
    private Boolean register;

    /**
     * Whether allow to subscribe services on the register center
     */
    private Boolean subscribe;

    /**
     * The customized parameters
     */
    private Map<String, String> parameters;

    /**
     * Whether it's default
     */
    private Boolean isDefault;

    /**
     * Simple the registry. both useful for provider and consumer
     *
     * @since 2.7.0
     */
    private Boolean simplified;
    /**
     * After simplify the registry, should add some paramter individually. just for provider.
     * <p>
     * such as: extra-keys = A,b,c,d
     *
     * @since 2.7.0
     */
    private String extraKeys;

    public RegistryBuilder address(String address) {
        this.address = address;
        return getThis();
    }

    public RegistryBuilder username(String username) {
        this.username = username;
        return getThis();
    }

    public RegistryBuilder password(String password) {
        this.password = password;
        return getThis();
    }

    public RegistryBuilder port(Integer port) {
        this.port = port;
        return getThis();
    }

    public RegistryBuilder protocol(String protocol) {
        this.protocol = protocol;
        return getThis();
    }

    public RegistryBuilder transporter(String transporter) {
        this.transporter = transporter;
        return getThis();
    }

    /**
     * @param transport
     * @see #transporter(String)
     * @deprecated
     */
    @Deprecated
    public RegistryBuilder transport(String transport) {
        this.transporter = transport;
        return getThis();
    }

    public RegistryBuilder server(String server) {
        this.server = server;
        return getThis();
    }

    public RegistryBuilder client(String client) {
        this.client = client;
        return getThis();
    }

    public RegistryBuilder cluster(String cluster) {
        this.cluster = cluster;
        return getThis();
    }

    public RegistryBuilder group(String group) {
        this.group = group;
        return getThis();
    }

    public RegistryBuilder version(String version) {
        this.version = version;
        return getThis();
    }

    public RegistryBuilder timeout(Integer timeout) {
        this.timeout = timeout;
        return getThis();
    }

    public RegistryBuilder session(Integer session) {
        this.session = session;
        return getThis();
    }

    public RegistryBuilder file(String file) {
        this.file = file;
        return getThis();
    }

    /**
     * @param wait
     * @see ProviderBuilder#wait(Integer)
     * @deprecated
     */
    @Deprecated
    public RegistryBuilder wait(Integer wait) {
        this.wait = wait;
        return getThis();
    }

    public RegistryBuilder isCheck(Boolean check) {
        this.check = check;
        return getThis();
    }

    public RegistryBuilder isDynamic(Boolean dynamic) {
        this.dynamic = dynamic;
        return getThis();
    }

    public RegistryBuilder register(Boolean register) {
        this.register = register;
        return getThis();
    }

    public RegistryBuilder subscribe(Boolean subscribe) {
        this.subscribe = subscribe;
        return getThis();
    }

    public RegistryBuilder appendParameter(String key, String value) {
        this.parameters = appendParameter(parameters, key, value);
        return getThis();
    }

    public RegistryBuilder appendParameters(Map<String, String> appendParameters) {
        this.parameters = appendParameters(parameters, appendParameters);
        return getThis();
    }

    public RegistryBuilder isDefault(Boolean isDefault) {
        this.isDefault = isDefault;
        return getThis();
    }

    public RegistryBuilder simplified(Boolean simplified) {
        this.simplified = simplified;
        return getThis();
    }

    public RegistryBuilder extraKeys(String extraKeys) {
        this.extraKeys = extraKeys;
        return getThis();
    }

    public RegistryConfig build() {
        RegistryConfig registry = new RegistryConfig();
        super.build(registry);

        registry.setAddress(address);
        registry.setCheck(check);
        registry.setClient(client);
        registry.setCluster(cluster);
        registry.setDefault(isDefault);
        registry.setDynamic(dynamic);
        registry.setExtraKeys(extraKeys);
        registry.setFile(file);
        registry.setGroup(group);
        registry.setParameters(parameters);
        registry.setPassword(password);
        registry.setPort(port);
        registry.setProtocol(protocol);
        registry.setRegister(register);
        registry.setServer(server);
        registry.setSession(session);
        registry.setSimplified(simplified);
        registry.setSubscribe(subscribe);
        registry.setTimeout(timeout);
        registry.setTransporter(transporter);
        registry.setUsername(username);
        registry.setVersion(version);
        registry.setWait(wait);

        return registry;
    }

    @Override
    protected RegistryBuilder getThis() {
        return this;
    }
}

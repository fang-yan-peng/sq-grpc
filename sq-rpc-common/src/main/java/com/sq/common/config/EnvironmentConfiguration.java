package com.sq.common.config;

/**
 * Configuration from system environment
 */
public class EnvironmentConfiguration extends AbstractPrefixConfiguration {

    public EnvironmentConfiguration(String prefix, String id) {
        super(prefix, id);
    }

    public EnvironmentConfiguration() {
        this(null, null);
    }

    @Override
    public Object getInternalProperty(String key) {
        return System.getenv(key);
    }

}

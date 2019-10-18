package com.sq.common.config;


/**
 * FIXME: is this really necessary? PropertiesConfiguration should have already covered this:
 * @see PropertiesConfiguration
 * @See ConfigUtils#getProperty(String)
 */
public class SystemConfiguration extends AbstractPrefixConfiguration {

    public SystemConfiguration(String prefix, String id) {
        super(prefix, id);
    }

    public SystemConfiguration() {
        this(null, null);
    }

    @Override
    public Object getInternalProperty(String key) {
        return System.getProperty(key);
    }

}

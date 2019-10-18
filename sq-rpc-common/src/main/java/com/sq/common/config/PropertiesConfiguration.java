package com.sq.common.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sq.common.utils.ConfigUtils;

/**
 * Configuration from system properties and rpc.properties
 */
public class PropertiesConfiguration extends AbstractPrefixConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesConfiguration.class);

    public PropertiesConfiguration(String prefix, String id) {
        super(prefix, id);
    }

    public PropertiesConfiguration() {
        this(null, null);
    }

    @Override
    public Object getInternalProperty(String key) {
        return ConfigUtils.getProperty(key);
    }
}

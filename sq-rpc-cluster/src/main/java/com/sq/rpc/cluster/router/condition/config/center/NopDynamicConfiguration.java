package com.sq.rpc.cluster.router.condition.config.center;


import com.sq.common.URL;

/**
 * The default extension of {@link DynamicConfiguration}. If user does not specify a config centre, or specifies one
 * that is not a valid extension, it will default to this one.
 */
public class NopDynamicConfiguration implements DynamicConfiguration {

    public NopDynamicConfiguration(URL url) {
        // no-op
    }


    @Override
    public Object getInternalProperty(String key) {
        return null;
    }

    @Override
    public void addListener(String key, String group, ConfigurationListener listener) {
        // no-op
    }

    @Override
    public void removeListener(String key, String group, ConfigurationListener listener) {
        // no-op
    }

    @Override
    public String getConfig(String key, String group, long timeout) throws IllegalStateException {
        return null;
    }
}

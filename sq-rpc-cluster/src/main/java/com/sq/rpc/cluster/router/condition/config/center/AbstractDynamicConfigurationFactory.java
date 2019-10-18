package com.sq.rpc.cluster.router.condition.config.center;


import com.sq.common.URL;

/**
 *
 */
public abstract class AbstractDynamicConfigurationFactory implements DynamicConfigurationFactory {

    private volatile DynamicConfiguration dynamicConfiguration;

    @Override
    public DynamicConfiguration getDynamicConfiguration(URL url) {
        if (dynamicConfiguration == null) {
            synchronized (this) {
                if (dynamicConfiguration == null) {
                    dynamicConfiguration = createDynamicConfiguration(url);
                }
            }
        }
        return dynamicConfiguration;
    }

    protected abstract DynamicConfiguration createDynamicConfiguration(URL url);
}

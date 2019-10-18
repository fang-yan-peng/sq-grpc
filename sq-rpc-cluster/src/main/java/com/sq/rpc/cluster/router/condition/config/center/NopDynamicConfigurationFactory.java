package com.sq.rpc.cluster.router.condition.config.center;

import com.sq.common.URL;

public class NopDynamicConfigurationFactory extends AbstractDynamicConfigurationFactory {

    @Override
    protected DynamicConfiguration createDynamicConfiguration(URL url) {
        return new NopDynamicConfiguration(url);
    }
}

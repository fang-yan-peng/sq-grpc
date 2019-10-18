package com.sq.rpc.cluster.configurator.override;

import com.sq.common.URL;
import com.sq.rpc.cluster.Configurator;
import com.sq.rpc.cluster.ConfiguratorFactory;

/**
 * OverrideConfiguratorFactory
 *
 */
public class OverrideConfiguratorFactory implements ConfiguratorFactory {

    @Override
    public Configurator getConfigurator(URL url) {
        return new OverrideConfigurator(url);
    }

}

package com.sq.rpc.cluster.configurator.absent;


import com.sq.common.URL;
import com.sq.rpc.cluster.Configurator;
import com.sq.rpc.cluster.ConfiguratorFactory;

/**
 * AbsentConfiguratorFactory
 *
 */
public class AbsentConfiguratorFactory implements ConfiguratorFactory {

    @Override
    public Configurator getConfigurator(URL url) {
        return new AbsentConfigurator(url);
    }

}

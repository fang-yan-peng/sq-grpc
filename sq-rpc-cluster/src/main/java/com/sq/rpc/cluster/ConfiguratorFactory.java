package com.sq.rpc.cluster;


import com.sq.common.URL;
import com.sq.common.extension.Adaptive;
import com.sq.common.extension.SPI;

/**
 * ConfiguratorFactory. (SPI, Singleton, ThreadSafe)
 *
 */
@SPI
public interface ConfiguratorFactory {

    /**
     * get the configurator instance.
     *
     * @param url - configurator url.
     * @return configurator instance.
     */
    @Adaptive("protocol")
    Configurator getConfigurator(URL url);

}

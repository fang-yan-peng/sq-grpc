package com.sq.rpc.cluster.router.condition.config.center;


import com.sq.common.URL;
import com.sq.common.extension.SPI;

/**
 *
 */
@SPI("nop")
public interface DynamicConfigurationFactory {

    DynamicConfiguration getDynamicConfiguration(URL url);

}

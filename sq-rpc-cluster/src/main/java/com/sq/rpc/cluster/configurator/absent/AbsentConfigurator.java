package com.sq.rpc.cluster.configurator.absent;

import com.sq.common.URL;
import com.sq.rpc.cluster.configurator.AbstractConfigurator;

/**
 * AbsentConfigurator
 *
 */
public class AbsentConfigurator extends AbstractConfigurator {

    public AbsentConfigurator(URL url) {
        super(url);
    }

    @Override
    public URL doConfigure(URL currentUrl, URL configUrl) {
        return currentUrl.addParametersIfAbsent(configUrl.getParameters());
    }

}

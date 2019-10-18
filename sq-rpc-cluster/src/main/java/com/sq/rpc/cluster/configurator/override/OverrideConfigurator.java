package com.sq.rpc.cluster.configurator.override;


import com.sq.common.URL;
import com.sq.rpc.cluster.configurator.AbstractConfigurator;

/**
 * OverrideConfigurator
 *
 */
public class OverrideConfigurator extends AbstractConfigurator {

    public OverrideConfigurator(URL url) {
        super(url);
    }

    @Override
    public URL doConfigure(URL currentUrl, URL configUrl) {
        return currentUrl.addParameters(configUrl.getParameters());
    }

}

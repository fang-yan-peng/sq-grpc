package com.sq.rpc.cluster.router.condition.config;


import com.sq.common.URL;
import com.sq.common.extension.Activate;
import com.sq.rpc.cluster.CacheableRouterFactory;
import com.sq.rpc.cluster.Router;
import com.sq.rpc.cluster.router.condition.config.center.DynamicConfiguration;

/**
 * Service level router factory
 */
@Activate(order = 300)
public class ServiceRouterFactory extends CacheableRouterFactory {

    public static final String NAME = "services";

    @Override
    protected Router createRouter(URL url) {
        return new ServiceRouter(DynamicConfiguration.getDynamicConfiguration(), url);
    }

}

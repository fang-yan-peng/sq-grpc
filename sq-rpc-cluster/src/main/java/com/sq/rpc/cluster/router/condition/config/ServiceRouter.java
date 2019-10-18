package com.sq.rpc.cluster.router.condition.config;

import com.sq.common.URL;
import com.sq.rpc.cluster.router.condition.config.center.DynamicConfiguration;

/**
 * Service level router, "server-unique-name.condition-router"
 */
public class ServiceRouter extends ListenableRouter {
    public static final String NAME = "SERVICE_ROUTER";
    /**
     * ServiceRouter should before AppRouter
     */
    private static final int SERVICE_ROUTER_DEFAULT_PRIORITY = 140;

    public ServiceRouter(DynamicConfiguration configuration, URL url) {
        super(configuration, url, url.getEncodedServiceKey());
        this.priority = SERVICE_ROUTER_DEFAULT_PRIORITY;
    }
}

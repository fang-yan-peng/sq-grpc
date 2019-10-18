package com.sq.rpc.cluster.router.condition.config;


import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.rpc.cluster.router.condition.config.center.DynamicConfiguration;

/**
 * Application level router, "application.condition-router"
 */
public class AppRouter extends ListenableRouter {
    public static final String NAME = "APP_ROUTER";
    /**
     * AppRouter should after ServiceRouter
     */
    private static final int APP_ROUTER_DEFAULT_PRIORITY = 150;

    public AppRouter(DynamicConfiguration configuration, URL url) {
        super(configuration, url, url.getParameter(Constants.APPLICATION_KEY));
        this.priority = APP_ROUTER_DEFAULT_PRIORITY;
    }
}

package com.sq.rpc.cluster.router.condition.config;

import com.sq.common.URL;
import com.sq.common.extension.Activate;
import com.sq.rpc.cluster.Router;
import com.sq.rpc.cluster.RouterFactory;
import com.sq.rpc.cluster.router.condition.config.center.DynamicConfiguration;

/**
 * Application level router factory
 */
@Activate(order = 200)
public class AppRouterFactory implements RouterFactory {
    public static final String NAME = "app";

    private volatile Router router;

    @Override
    public Router getRouter(URL url) {
        if (router != null) {
            return router;
        }
        synchronized (this) {
            if (router == null) {
                router = createRouter(url);
            }
        }
        return router;
    }

    private Router createRouter(URL url) {
        return new AppRouter(DynamicConfiguration.getDynamicConfiguration(), url);
    }
}

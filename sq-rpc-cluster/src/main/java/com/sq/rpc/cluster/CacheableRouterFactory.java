package com.sq.rpc.cluster;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.sq.common.URL;


/**
 * If you want to provide a router implementation based on design of v2.7.0, please extend from this abstract class.
 * For 2.6.x style router, please implement and use RouterFactory directly.
 */
public abstract class CacheableRouterFactory implements RouterFactory {
    private ConcurrentMap<String, Router> routerMap = new ConcurrentHashMap<>();

    @Override
    public Router getRouter(URL url) {
        routerMap.computeIfAbsent(url.getServiceKey(), k -> createRouter(url));
        return routerMap.get(url.getServiceKey());
    }

    protected abstract Router createRouter(URL url);
}

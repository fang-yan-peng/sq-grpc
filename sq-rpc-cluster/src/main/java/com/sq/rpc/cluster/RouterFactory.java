package com.sq.rpc.cluster;

import com.sq.common.URL;
import com.sq.common.extension.Adaptive;
import com.sq.common.extension.SPI;
import com.sq.rpc.Invocation;

/**
 * RouterFactory. (SPI, Singleton, ThreadSafe)
 * <p>
 * <a href="http://en.wikipedia.org/wiki/Routing">Routing</a>
 *
 * @see Cluster#join(Directory)
 * @see Directory#list(Invocation)
 * <p>
 * Note Router has a different behaviour since 2.7.0, for each type of Router, there will only has one Router instance
 * for each services. See {@link CacheableRouterFactory} and {@link RouterChain} for how to extend a new Router or how
 * the Router instances are loaded.
 */
@SPI
public interface RouterFactory {

    /**
     * Create router.
     * Since 2.7.0, most of the time, we will not use @Adaptive feature, so it's kept only for compatibility.
     *
     * @param url url
     * @return router instance
     */
    @Adaptive("protocol")
    Router getRouter(URL url);
}

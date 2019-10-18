package com.sq.rpc.cluster.router.tag;


import com.sq.common.URL;
import com.sq.common.extension.Activate;
import com.sq.rpc.cluster.CacheableRouterFactory;
import com.sq.rpc.cluster.Router;
import com.sq.rpc.cluster.router.condition.config.center.DynamicConfiguration;

/**
 * Tag router factory
 */
@Activate(order = 100)
public class TagRouterFactory extends CacheableRouterFactory {

    public static final String NAME = "tag";

    @Override
    protected Router createRouter(URL url) {
        return new TagRouter(DynamicConfiguration.getDynamicConfiguration(), url);
    }
}

package com.sq.rpc.cluster.router.condition;


import com.sq.common.URL;
import com.sq.rpc.cluster.Router;
import com.sq.rpc.cluster.RouterFactory;

/**
 * ConditionRouterFactory
 *
 */
public class ConditionRouterFactory implements RouterFactory {

    public static final String NAME = "condition";

    @Override
    public Router getRouter(URL url) {
        return new ConditionRouter(url);
    }

}

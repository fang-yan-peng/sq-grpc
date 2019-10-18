package com.sq.rpc.cluster.router.mock;

import com.sq.common.URL;
import com.sq.common.extension.Activate;
import com.sq.rpc.cluster.Router;
import com.sq.rpc.cluster.RouterFactory;

/**
 *
 */
@Activate
public class MockRouterFactory implements RouterFactory {
    public static final String NAME = "mock";

    @Override
    public Router getRouter(URL url) {
        return new MockInvokersSelector();
    }

}

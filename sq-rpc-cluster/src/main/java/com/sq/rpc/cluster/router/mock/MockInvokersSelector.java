package com.sq.rpc.cluster.router.mock;

import java.util.ArrayList;
import java.util.List;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.exceptions.RpcException;
import com.sq.common.utils.CollectionUtils;
import com.sq.rpc.Invocation;
import com.sq.rpc.Invoker;
import com.sq.rpc.cluster.router.AbstractRouter;

/**
 * A specific Router designed to realize mock feature.
 * If a request is configured to use mock, then this router guarantees that only the invokers with protocol MOCK appear in final the invoker list, all other invokers will be excluded.
 */
public class MockInvokersSelector extends AbstractRouter {

    public static final String NAME = "MOCK_ROUTER";
    private static final int MOCK_INVOKERS_DEFAULT_PRIORITY = Integer.MIN_VALUE;

    public MockInvokersSelector() {
        this.priority = MOCK_INVOKERS_DEFAULT_PRIORITY;
    }

    @Override
    public <T> List<Invoker<T>> route(final List<Invoker<T>> invokers,
                                      URL url, final Invocation invocation) throws RpcException {
        if (CollectionUtils.isEmpty(invokers)) {
            return invokers;
        }

        if (invocation.getAttachments() == null) {
            return getNormalInvokers(invokers);
        } else {
            String value = invocation.getAttachments().get(Constants.INVOCATION_NEED_MOCK);
            if (value == null) {
                return getNormalInvokers(invokers);
            } else if (Boolean.TRUE.toString().equalsIgnoreCase(value)) {
                return getMockedInvokers(invokers);
            }
        }
        return invokers;
    }

    private <T> List<Invoker<T>> getMockedInvokers(final List<Invoker<T>> invokers) {
        if (!hasMockProviders(invokers)) {
            return null;
        }
        List<Invoker<T>> sInvokers = new ArrayList<Invoker<T>>(1);
        for (Invoker<T> invoker : invokers) {
            if (invoker.getUrl().getProtocol().equals(Constants.MOCK_PROTOCOL)) {
                sInvokers.add(invoker);
            }
        }
        return sInvokers;
    }

    private <T> List<Invoker<T>> getNormalInvokers(final List<Invoker<T>> invokers) {
        if (!hasMockProviders(invokers)) {
            return invokers;
        } else {
            List<Invoker<T>> sInvokers = new ArrayList<Invoker<T>>(invokers.size());
            for (Invoker<T> invoker : invokers) {
                if (!invoker.getUrl().getProtocol().equals(Constants.MOCK_PROTOCOL)) {
                    sInvokers.add(invoker);
                }
            }
            return sInvokers;
        }
    }

    private <T> boolean hasMockProviders(final List<Invoker<T>> invokers) {
        boolean hasMockProvider = false;
        for (Invoker<T> invoker : invokers) {
            if (invoker.getUrl().getProtocol().equals(Constants.MOCK_PROTOCOL)) {
                hasMockProvider = true;
                break;
            }
        }
        return hasMockProvider;
    }

}

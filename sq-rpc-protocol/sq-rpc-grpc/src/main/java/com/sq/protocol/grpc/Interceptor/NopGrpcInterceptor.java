package com.sq.protocol.grpc.Interceptor;

import com.sq.common.URL;

/**
 *
 * @author yanpengfang
 * create 2019-10-17 2:00 PM
 */
public class NopGrpcInterceptor implements GrpcInterceptor {

    @Override
    public void interceptCall(URL url, String method, long duration, Throwable e) {

    }

    @Override
    public void interceptMethodCall(URL url, String method, long duration, Throwable e) {

    }
}

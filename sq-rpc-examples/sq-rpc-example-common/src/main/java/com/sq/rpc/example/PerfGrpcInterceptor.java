package com.sq.rpc.example;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.protocol.grpc.Interceptor.GrpcInterceptor;

/**
 *
 * @author yanpengfang
 * create 2019-10-17 2:50 PM
 */
public class PerfGrpcInterceptor implements GrpcInterceptor {

    @Override
    public void interceptCall(URL url, String method, long duration, Throwable e) {
        System.out.println("call:" + url.getParameter("side") +
                "|" + url.getParameter(Constants.INTERFACE_KEY) +
                "|" + method +
                "|" + duration);
    }

    @Override
    public void interceptMethodCall(URL url, String method, long duration, Throwable e) {
        System.out.println("method:" + url.getParameter("side") +
                "|" + url.getParameter(Constants.INTERFACE_KEY) +
                "|" + method +
                "|" + duration);
    }
}

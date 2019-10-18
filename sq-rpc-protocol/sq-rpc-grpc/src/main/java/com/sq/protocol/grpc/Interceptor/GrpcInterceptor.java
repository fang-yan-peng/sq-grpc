package com.sq.protocol.grpc.Interceptor;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.extension.Adaptive;
import com.sq.common.extension.SPI;

/**
 *
 * @author yanpengfang
 * @create 2019-10-17 1:57 PM
 */
@SPI("nop")
public interface GrpcInterceptor {

    /**
     * 拦截grpc执行整个过程，包括执行业务逻辑过程
     * @param url 服务url
     * @param method 调用的方法
     * @param duration 方法执行时间，单位纳秒。
     * @param e 异常
     */
    @Adaptive({Constants.SERVICE_INTERCEPTOR_KEY, Constants.REFERENCE_INTERCEPTOR_KEY})
    void interceptCall(URL url, String method, long duration, Throwable e);

    /**
     * 拦截grpc 执行业务逻辑执行过程，只对server端有效
     * @param url 服务url
     * @param method 调用的方法
     * @param duration 方法执行时间，单位纳秒。
     * @param e 异常
     */
    @Adaptive({Constants.SERVICE_INTERCEPTOR_KEY, Constants.REFERENCE_INTERCEPTOR_KEY})
    void interceptMethodCall(URL url, String method, long duration, Throwable e);
}

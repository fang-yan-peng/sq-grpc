package com.sq.protocol.grpc.Interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.extension.ExtensionLoader;
import com.sq.common.utils.StringUtils;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;

/**
 *
 * @author yanpengfang
 * create 2019-10-17 10:29 AM
 */
public class GrpcClientInterceptor implements ClientInterceptor {

    private final Logger logger = LoggerFactory.getLogger(GrpcClientInterceptor.class);

    private final URL url;

    public GrpcClientInterceptor(URL url) {
        this.url = url;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT>
            method, CallOptions callOptions, Channel next) {

        //创建client
        ClientCall<ReqT, RespT> clientCall = next.newCall(method, callOptions);

        long startTime = System.nanoTime();

        String methodName = StringUtils.removeEnd(method.getFullMethodName(), "/");

        return new ForwardingClientCall<ReqT, RespT>() {

            @Override
            protected ClientCall<ReqT, RespT> delegate() {
                return clientCall;
            }

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                String reqId = MDC.get(Constants.X_REQUEST_ID);
                if (StringUtils.isNotEmpty(reqId)) {
                    Metadata.Key<String> xRequestId = Metadata.Key.of(Constants.X_REQUEST_ID, Metadata.ASCII_STRING_MARSHALLER);
                    headers.put(xRequestId, reqId);
                }
                Listener<RespT> forwardListener = new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {

                    @Override
                    public void onHeaders(Metadata headers) {
                        super.onHeaders(headers);
                    }

                    //收到消息
                    @Override
                    public void onMessage(RespT message) {
                        super.onMessage(message);
                    }

                    @Override
                    public void onClose(Status status, Metadata trailers) {
                        super.onClose(status, trailers);
                        try {
                            ExtensionLoader.getExtensionLoader(GrpcInterceptor.class)
                                    .getAdaptiveExtension()
                                    .interceptCall(url, methodName, System.nanoTime() - startTime, status.getCause());
                        } catch (Throwable e) {
                            logger.error("grcp interceptor execute error", e);
                        }
                    }
                };
                super.start(forwardListener, headers);
            }
        };
    }
}

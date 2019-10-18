package com.sq.protocol.grpc.Interceptor;

import static io.grpc.Metadata.BINARY_BYTE_MARSHALLER;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.common.extension.ExtensionLoader;
import com.sq.common.utils.StringUtils;

import io.grpc.ForwardingServerCall;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

/**
 *
 * @author yanpengfang
 * create 2019-10-17 10:29 AM
 */
public class GrpcServerInterceptor implements ServerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(GrpcServerInterceptor.class);

    private final URL url;

    public GrpcServerInterceptor(URL url) {
        this.url = url;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT>
            call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

        String methodName = StringUtils.removeEnd(call.getMethodDescriptor().getFullMethodName(), "/");
        Metadata.Key<String> xRequestId = Metadata.Key.of(Constants.X_REQUEST_ID, Metadata.ASCII_STRING_MARSHALLER);
        String reqId = headers.get(xRequestId);
        long startTime = System.nanoTime();
        ServerCall.Listener<ReqT> listener = next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {

            @Override
            public void sendHeaders(Metadata headers) {
                super.sendHeaders(headers);
            }

            //处理完业务，回写客户端消息
            @Override
            public void sendMessage(RespT message) {
                delegate().sendMessage(message);
            }

            //一次请求完成
            @Override
            public void close(Status status, Metadata trailers) {
                MDC.remove(Constants.X_REQUEST_ID);
                delegate().close(status, trailers);
                try {
                    ExtensionLoader.getExtensionLoader(GrpcInterceptor.class)
                            .getAdaptiveExtension()
                            .interceptCall(url, methodName, System.nanoTime() - startTime, status.getCause());
                } catch (Throwable e) {
                    logger.error("grcp interceptor execute error", e);
                }
            }


        }, headers);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(listener) {

            //收到client端发来的消息
            @Override
            public void onMessage(ReqT message) {
                if (StringUtils.isNotEmpty(reqId)) {
                    MDC.put(Constants.X_REQUEST_ID, reqId);
                }
                delegate().onMessage(message);
            }

            //处理完业务，真实业务代码执行。
            @Override
            public void onHalfClose() {
                long startTime = System.nanoTime();
                Throwable t = null;
                try {
                    delegate().onHalfClose();
                } catch (Throwable e) {
                    t = e;
                    onError(e);
                } finally {
                    try {
                        ExtensionLoader.getExtensionLoader(GrpcInterceptor.class)
                                .getAdaptiveExtension()
                                .interceptMethodCall(url, methodName, System.nanoTime() - startTime, t);
                    } catch (Throwable e) {
                        logger.error("grcp interceptor execute error", e);
                    }
                }
            }

            /**
             * copy from {@link io.grpc.stub.ServerCalls.ServerCallStreamObserverImpl#onError}
             */
            private void onError(Throwable e) {
                Metadata metadata = Status.trailersFromThrowable(e);
                if (metadata == null) {
                    metadata = new Metadata();
                    String message = e.getClass().getName() + ": " + e.getMessage();
                    metadata.put(Metadata.Key.of("remote_internal_exception_message", BINARY_BYTE_MARSHALLER),
                            message.getBytes());
                }
                call.close(Status.fromThrowable(e), metadata);
            }
        };
    }
}

package com.sq.rpc.example;

import org.slf4j.MDC;

import com.sq.common.Constants;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;

/**
 *
 * @author yanpengfang
 * create 2019-10-16 2:41 PM
 */
public class GreeterImpl extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        System.out.println("客户端传过来的X-Request-Id:" + MDC.get(Constants.X_REQUEST_ID));
        System.out.println("services:" + req.getName());
        HelloReply reply = HelloReply.newBuilder().setMessage(("Hello: " + req.getName())).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}

package com.sq.rpc.example.comp;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Component;

import com.sq.config.annotation.Reference;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloRequest;

@Component("greeterFutureComponent")
public class GreeterServiceComponent {

    @Reference
    private GreeterGrpc.GreeterFutureClient greeterFutureClient;

    public String sayHello(String name) throws InterruptedException, ExecutionException, TimeoutException {
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        return greeterFutureClient.sayHello(request).get(5, TimeUnit.SECONDS).getMessage();
    }
}

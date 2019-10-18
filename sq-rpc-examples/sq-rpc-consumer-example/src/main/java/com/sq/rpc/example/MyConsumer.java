package com.sq.rpc.example;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.MDC;

import com.google.common.util.concurrent.ListenableFuture;
import com.sq.common.Constants;
import com.sq.config.ApplicationConfig;
import com.sq.config.ReferenceConfig;
import com.sq.config.RegistryConfig;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;

/**
 * 通过注册中心订阅provider的地址
 * @author yanpengfang
 * create 2019-10-16 4:33 PM
 */
@SuppressWarnings("ALL")
public class MyConsumer {

    static final ApplicationConfig application = new ApplicationConfig();

    static final RegistryConfig registry = new RegistryConfig();

    //不推荐使用，没有超时时间
    static GreeterGrpc.GreeterBlockingClient blockingClient;

    //推荐使用，有超时时间
    static GreeterGrpc.GreeterFutureClient futureClient;

    //不推荐使用，没有超时时间
    static GreeterGrpc.Greeter asyncClient;

    public static void main(String[] args) {
        application.setName("test-rpc-client");
        registry.setAddress("zookeeper://127.0.0.1:2181");
        MDC.put(Constants.X_REQUEST_ID, UUID.randomUUID().toString().replaceAll("\\-", ""));
        blockingClient = getServiceStub(GreeterGrpc.GreeterBlockingClient.class);

        futureClient = getServiceStub(GreeterGrpc.GreeterFutureClient.class);

        asyncClient = getServiceStub(GreeterGrpc.Greeter.class);

        greetBlocking("xiaozhang");
        try {
            greetFuture("xiaoli");
        } catch (Exception e) {
            e.printStackTrace();
        }
        greetCallback("xiaoming");
        try {
            CountDownLatch latch = new CountDownLatch(1);
            while (true) {
                latch.await();
            }
        } catch (InterruptedException ignored) {

        }
    }

    private static <T> T getServiceStub(Class<T> clientClz) {
        ReferenceConfig<T> reference = new ReferenceConfig<>();
        reference.setApplication(application);
        reference.setRegistry(registry);
        reference.setInterface(clientClz);
        reference.setInterceptor("perf");
        return reference.get();
    }

    static void greetBlocking(String name) {
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response = blockingClient.sayHello(request);
        System.out.println(response.getMessage());

    }

    static void greetFuture(String name) throws InterruptedException, ExecutionException,
            TimeoutException {
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        ListenableFuture<HelloReply> response = futureClient.sayHello(request);
        System.out.println(response.get(100, TimeUnit.MILLISECONDS).getMessage());
    }

    static void greetCallback(String name) {
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        asyncClient.sayHello(request, new StreamObserver<HelloReply>() {
            @Override
            public void onNext(HelloReply helloReply) {
                System.out.println(helloReply.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("success");
            }
        });
    }
}

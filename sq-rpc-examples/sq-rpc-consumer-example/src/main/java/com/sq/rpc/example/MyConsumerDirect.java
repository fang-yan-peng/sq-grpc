package com.sq.rpc.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.util.concurrent.ListenableFuture;
import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.config.ApplicationConfig;
import com.sq.config.ReferenceConfig;
import com.sq.config.RegistryConfig;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;

/**
 * 直连provider
 * @author yanpengfang
 * create 2019-10-16 4:33 PM
 */
@SuppressWarnings("ALL")
public class MyConsumerDirect {

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
        registry.setAddress("N/A");
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
        List<URL> urls = reference.toUrls();
        urls.addAll(buildUrls(GreeterGrpc.Greeter.class.getName()));
        return reference.get();
    }

    static List<URL> buildUrls(String interfaceName) {
        List<URL> urls = new ArrayList<>();
        // 配置直连的 provider 列表
        urls.add(new URL(Constants.SQ_RPC_PROTOCOL, "192.168.10.79", 20880, interfaceName));
        return urls;
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

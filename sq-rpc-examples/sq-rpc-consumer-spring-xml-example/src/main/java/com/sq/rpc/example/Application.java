package com.sq.rpc.example;

import java.util.concurrent.TimeUnit;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloRequest;

public class Application {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/sq-rpc-consumer.xml");
        context.start();
        GreeterGrpc.GreeterFutureClient greeterFutureClient = context.getBean("greeterFutureClient",
                GreeterGrpc.GreeterFutureClient.class);
        HelloRequest request = HelloRequest.newBuilder().setName("world").build();
        String hello = null;
        try {
            hello = greeterFutureClient.sayHello(request).get(5, TimeUnit.SECONDS).getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("result: " + hello);
    }
}

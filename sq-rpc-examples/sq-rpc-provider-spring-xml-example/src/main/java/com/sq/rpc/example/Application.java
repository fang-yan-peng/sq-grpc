package com.sq.rpc.example;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sq.protocol.grpc.GrpcProtocol;

public class Application {

    public static void main(String[] args) throws Exception {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/sq-rpc-provider.xml");
        context.start();
        GrpcProtocol.getGrpcProtocol().start();
        System.in.read();
    }
}

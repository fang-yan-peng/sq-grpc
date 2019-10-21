package com.sq.rpc.example;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import com.sq.config.ApplicationConfig;
import com.sq.config.ProtocolConfig;
import com.sq.config.RegistryConfig;
import com.sq.config.ServiceConfig;
import com.sq.protocol.grpc.GrpcProtocol;

import io.grpc.examples.helloworld.GreeterGrpc;

/**
 *
 * @author yanpengfang
 * create 2019-10-16 2:37 PM
 */
public class MyProviderDirect {

    private static ApplicationConfig application = new ApplicationConfig();

    private static RegistryConfig registry = new RegistryConfig();

    private static ProtocolConfig protocol = new ProtocolConfig();

    public static void main(String[] args) throws IOException {
        // 当前应用配置
        application.setName("test-rpc-provider");

        // 连接注册中心配置
        registry.setAddress("N/A");

        // 服务提供者协议配置
        protocol.setName("grpc");
        protocol.setPort(20880);
        protocol.setThreads(10);
        protocol.setHost("0.0.0.0");
        exportHelloService();
        //最后开启协议
        GrpcProtocol.getGrpcProtocol().start();
        System.in.read();
    }

    @SuppressWarnings("ALL")
    private static void exportHelloService() {
        // 服务提供者暴露服务配置
        ServiceConfig<GreeterGrpc.Greeter> service = new ServiceConfig<>();
        service.setApplication(application);
        service.setRegistry(registry);
        service.setProtocol(protocol);
        service.setInterface(GreeterGrpc.Greeter.class);
        service.setRef(new GreeterImpl());
        service.setInterceptor("perf");
        // 暴露及注册服务
        service.export();
    }
}

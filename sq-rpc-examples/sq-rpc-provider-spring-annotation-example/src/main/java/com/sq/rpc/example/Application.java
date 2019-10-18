package com.sq.rpc.example;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.sq.config.RegistryConfig;
import com.sq.config.spring.context.annotation.EnableSqGrpc;
import com.sq.protocol.grpc.GrpcProtocol;

public class Application {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ProviderConfiguration.class);
        context.start();
        GrpcProtocol.getGrpcProtocol().start();
        System.in.read();
    }

    @Configuration
    @EnableSqGrpc(scanBasePackages = "com.sq.rpc.example")
    @PropertySource("classpath:/spring/sq-rpc-provider.properties")
    static class ProviderConfiguration {
        @Bean
        public RegistryConfig registryConfig() {
            RegistryConfig registryConfig = new RegistryConfig();
            registryConfig.setAddress("zookeeper://127.0.0.1:2181");
            return registryConfig;
        }
    }
}

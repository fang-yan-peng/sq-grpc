package com.sq.rpc.example;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.sq.config.spring.context.annotation.EnableSqGrpc;
import com.sq.rpc.example.comp.GreeterServiceComponent;

/**
 *
 * @author yanpengfang
 * create 2019-10-18 4:52 PM
 */
public class Application {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
        context.start();
        GreeterServiceComponent service = context.getBean("greeterFutureComponent", GreeterServiceComponent.class);
        String hello = null;
        try {
            hello = service.sayHello("world");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("result :" + hello);
    }

    @Configuration
    @EnableSqGrpc(scanBasePackages = "com.sq.rpc.example.comp")
    @PropertySource("classpath:/spring/sq-rpc-consumer.properties")
    @ComponentScan(value = {"com.sq.rpc.example.comp"})
    static class ConsumerConfiguration {

    }
}

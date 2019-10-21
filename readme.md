# 简介

Sq-Rpc 封装了现有rpc框架grpc，支持直连、服务注册发现、连接池、监控并且扩展了许多功能。提供spring相关插件，方便与spring和spring boot进行集成。

# 特点

* 支持zk和etcd3的服务注册和发现
* 支持直接连接
* 可配置连接池
* 可配置负载均衡策略
* 提供接口耗时，qps等监控。

# 例子
## 依赖
```xml
<dependency>
 <groupId>com.sq</groupId>
 <artifactId>sq-rpc-registry-zookeeper</artifactId>
 <version>1.0-SNAPSHOT</version>
</dependency>
<dependency>
 <groupId>com.sq</groupId>
 <artifactId>sq-rpc-grpc</artifactId>
 <version>1.0-SNAPSHOT</version>
</dependency>
<dependency>
 <groupId>com.sq</groupId>
 <artifactId>sq-rpc-config-api</artifactId>
 <version>1.0-SNAPSHOT</version>
</dependency>
<!--不与spring集成不用依赖此包-->
<dependency>
 <groupId>com.sq</groupId>
 <artifactId>sq-rpc-config-spring</artifactId>
 <version>1.0-SNAPSHOT</version>
</dependency>
```
## 一、定义proto文件

helloworld.proto

```xml
syntax = "proto3";

option java_multiple_files = true;
option java_package = "io.grpc.examples.helloworld";
option java_outer_classname = "HelloWorldProto";
option objc_class_prefix = "HLW";

package helloworld;

service Greeter {
    rpc SayHello (HelloRequest) returns (HelloReply) {}
}

message HelloRequest {
    string name = 1;
}

// The response message containing the greetings
message HelloReply {
    string message = 1;
}
```

maven提供了grpc相关的插件，编译项目自动生成grpc相关的类。配置maven插件。在 pom.xml中添加如下：

```xml
<build>
        <extensions>
            <extension>
                <groupId>kr.motd.maven</groupId>
                <artifactId>os-maven-plugin</artifactId>
                <version>1.4.1.Final</version>
            </extension>
        </extensions>
        <plugins>
            <plugin>
                <groupId>org.xolstice.maven.plugins</groupId>
                <artifactId>protobuf-maven-plugin</artifactId>
                <version>0.5.0</version>
                <configuration>
                    <protocArtifact>com.google.protobuf:protoc:3.0.0:exe:${os.detected.classifier}</protocArtifact>
                    <pluginId>grpc-java</pluginId>
                    <pluginArtifact>io.grpc:protoc-gen-grpc-java:1.0.0:exe:${os.detected.classifier}</pluginArtifact>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>compile-custom</goal>
                        </goals>
                        <configuration>
                            <pluginParameter>enable_deprecated=true</pluginParameter>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <testSource>1.8</testSource>
                    <testTarget>1.8</testTarget>
                </configuration>
                <version>3.3</version>
            </plugin>
        </plugins>
    </build>
```

项目的结构如下：

![image-20191018190344808](https://github.com/fang-yan-peng/sq-grpc/blob/master/project.png)

这样编译项目就会生成grpc相关的类：

![image-20191018190625773](https://github.com/fang-yan-peng/sq-grpc/blob/master/generate_code.png)

grpc会提供同步客户端、异步客户端和Future带超时的客户端。本例子中就是GreeGrpc的内部类：GreeterBlockingClient、Greeter和GreeterFutureClient。



## 二、直接使用

### 1、直连不使用服务注册发现

####  1.1、服务端代码

```java
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
```

```java
package com.sq.rpc.example;

import java.util.concurrent.CountDownLatch;

import com.sq.config.ApplicationConfig;
import com.sq.config.ProtocolConfig;
import com.sq.config.RegistryConfig;
import com.sq.config.ServiceConfig;
import com.sq.protocol.grpc.GrpcProtocol;

import io.grpc.examples.helloworld.GreeterGrpc;

public class MyProviderDirect {

    private static ApplicationConfig application = new ApplicationConfig();

    private static RegistryConfig registry = new RegistryConfig();

    private static ProtocolConfig protocol = new ProtocolConfig();

    public static void main(String[] args) {
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
        try {
            CountDownLatch latch = new CountDownLatch(1);
            while (true) {
                latch.await();
            }
        } catch (InterruptedException ignored) {
        }


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
        //service.setInterceptor("perf");
        // 暴露及注册服务
        service.export();
    }
```

#### 1.2、客户端代码

提供了三种客户端的实现，推荐使用Future客户端。

```java
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
        //reference.setInterceptor("perf");
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

```

### 2、使用服务注册发现

#### 2.1、以zk作为注册中心为例，服务端代码。

```java
package com.sq.rpc.example;

import java.util.concurrent.CountDownLatch;

import com.sq.config.ApplicationConfig;
import com.sq.config.ProtocolConfig;
import com.sq.config.RegistryConfig;
import com.sq.config.ServiceConfig;
import com.sq.protocol.grpc.GrpcProtocol;

import io.grpc.examples.helloworld.GreeterGrpc;

public class MyProvider {

    private static ApplicationConfig application = new ApplicationConfig();

    private static RegistryConfig registry = new RegistryConfig();

    private static ProtocolConfig protocol = new ProtocolConfig();

    public static void main(String[] args) {
        // 当前应用配置
        application.setName("test-rpc-provider");

        // 连接注册中心配置
        registry.setAddress("zookeeper://127.0.0.1:2181");

        // 服务提供者协议配置
        protocol.setName("grpc");
        protocol.setPort(20880);
        protocol.setThreads(10);
        protocol.setHost("0.0.0.0");
        exportHelloService();
        //最后开启协议
        GrpcProtocol.getGrpcProtocol().start();
        try {
            CountDownLatch latch = new CountDownLatch(1);
            while (true) {
                latch.await();
            }
        } catch (InterruptedException ignored) {
        }


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
        //service.setInterceptor("perf");
        // 暴露及注册服务
        service.export();
    }
}
```

#### 2.2、以zk作为注册中心为例，客户端代码。

```java
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
        //reference.setInterceptor("perf");
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
```



## 三、与spring集成，使用注解的方式

### 1、服务端代码

```java
package com.sq.rpc.example;

import com.sq.config.annotation.Service;

import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;

/**
 *
 * @author yanpengfang
 * create 2019-10-16 2:41 PM
 */
@Service
public class GreeterImpl extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage(("Hello: " + req.getName())).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}

```

```java
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
```

sq-rpc-provider.properties的 内容：

```
sq.grpc.application.name=demo-annotation-provider
sq.grpc.protocol.name=grpc
sq.grpc.protocol.port=20880
```

### 2、客户端代码

```java

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
```

```java
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
```

sq-rpc-consumer.properties的内容：

```
sq.grpc.application.name=demo-annotation-consumer
sq.grpc.registry.address=zookeeper://127.0.0.1:2181
```

## 四、与spring集成，使用配置文件的方式

### 1、服务端代码

spring/sq-rpc-provider.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:sq-grpc="http://com.sq/schema/sq-grpc"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.3.xsd
       http://com.sq/schema/sq-grpc http://com.sq/schema/sq-grpc/sq-grpc.xsd">

    <!-- provider's application name, used for tracing dependency relationship -->
    <sq-grpc:application name="demo-provider"/>

    <sq-grpc:registry address="zookeeper://127.0.0.1:2181" />

    <!-- use grpc protocol to export service on port 20880 -->
    <sq-grpc:protocol name="grpc"/>

    <!-- service implementation, as same as regular local bean -->
    <bean id="greeterService" class="com.sq.rpc.example.GreeterImpl"/>

    <!-- declare the service interface to be exported -->
    <sq-grpc:service interface="io.grpc.examples.helloworld.GreeterGrpc$Greeter" ref="greeterService"/>

</beans>
```

```java
package com.sq.rpc.example;

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
        HelloReply reply = HelloReply.newBuilder().setMessage(("Hello: " + req.getName())).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
```

```java
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
```
如果是以tomcat方式启动，没有办法在在main方法中调用GrpcProtocol.getGrpcProtocol().start()，所以重写了org.springframework.web.context.ContextLoaderListener的contextInitialized方法，在web.xml中把org.springframework.web.context.ContextLoaderListener替换成com.sq.config.spring.context.config.SqContextLoaderListener即可。
### 2、客户端代码

spring/sq-rpc-consumer.xml

```java
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:sq-grpc="http://com.sq/schema/sq-grpc"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.3.xsd
       http://com.sq/schema/sq-grpc   http://com.sq/schema/sq-grpc/sq-grpc.xsd">

    <sq-grpc:application name="demo-consumer"/>

    <sq-grpc:registry address="zookeeper://127.0.0.1:2181"/>

    <sq-grpc:reference id="greeterFutureClient" check="false"
                       interface="io.grpc.examples.helloworld.GreeterGrpc$GreeterFutureClient"/>

</beans>
```

```java
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
```
## 五、使用拦截器统计耗时

拦截器以spi的方式接入。

```java
package com.sq.rpc.example;

import com.sq.common.Constants;
import com.sq.common.URL;
import com.sq.protocol.grpc.Interceptor.GrpcInterceptor;

/**
 *
 * @author yanpengfang
 * create 2019-10-17 2:50 PM
 */
public class PerfGrpcInterceptor implements GrpcInterceptor {

    @Override
    public void interceptCall(URL url, String method, long duration, Throwable e) {
        System.out.println("call:" + url.getParameter("side") +
                "|" + url.getParameter(Constants.INTERFACE_KEY) +
                "|" + method +
                "|" + duration);
    }
}

```

在META-INF/services新建com.sq.protocol.grpc.Interceptor.GrpcInterceptor文件，文件内容：

```
perf=com.sq.rpc.example.PerfGrpcInterceptor
```

目录结构：

![image-20191018201941863](https://github.com/fang-yan-peng/sq-grpc/blob/master/interceptor.png)

在服务端和客户端配置上拦截器即可。

1、如果是不依赖spring框架使用sq-grpc,则调用ServiceConfig和ReferenceConfig的setInterceptor方法传入拦截器的名称"perf"即可。

2、如果使用spring注解的方式，则在Reference和Service注解中，设置属性interceptor="perf"即可。

3、如果使用spring配置文件的方式，则需要在原有的配置文件上添加上interceptor属性，如下所示：

客户端：

```
<sq-grpc:reference id="greeterFutureClient" check="false"
                   interface="io.grpc.examples.helloworld.GreeterGrpc$GreeterFutureClient" 
                   interceptor="perf"/>
```

服务端：

```
<sq-grpc:service interface="io.grpc.examples.helloworld.GreeterGrpc$Greeter" 
                 ref="greeterService" interceptor="perf"/>
```

# 六、常用的配置

* iothread 配置在ProtocolConfig上，指定处理io的线程数。
* maxCallsPerConnection 配置在ProtocolConfig上，限制每个连接最大并发。
* maxMessageSize 配置在ProtocolConfig上，限制rpc调用的最大传输字节数。
* threads 配置在ProtocolConfig上，限制处理业务线程数。
* queues 配置在ProtocolConfig上，限制处理业务任务排队数。
* connections 配置在ReferenceConfig上，设置与服务连接的数，如果是0则走公共的连接。
* shareconnections 配置在ReferenceConfig上，设置公共连接数大小。

以上只是常用到的一些配置，还有许多配置需要深入了解项目才能理解。
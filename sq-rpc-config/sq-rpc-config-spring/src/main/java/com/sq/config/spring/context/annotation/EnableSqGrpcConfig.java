package com.sq.config.spring.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.sq.config.ApplicationConfig;
import com.sq.config.ConsumerConfig;
import com.sq.config.ModuleConfig;
import com.sq.config.MonitorConfig;
import com.sq.config.ProtocolConfig;
import com.sq.config.ProviderConfig;
import com.sq.config.RegistryConfig;

/**
 * As  a convenient and multiple {@link EnableSqGrpcConfigBinding}
 * in default behavior , is equal to single bean bindings with below convention prefixes of properties:
 * <ul>
 * <li>{@link ApplicationConfig} binding to property : "sq.grpc.application"</li>
 * <li>{@link ModuleConfig} binding to property :  "sq.grpc.module"</li>
 * <li>{@link RegistryConfig} binding to property :  "sq.grpc.registry"</li>
 * <li>{@link ProtocolConfig} binding to property :  "sq.grpc.protocol"</li>
 * <li>{@link MonitorConfig} binding to property :  "sq.grpc.monitor"</li>
 * <li>{@link ProviderConfig} binding to property :  "sq.grpc.provider"</li>
 * <li>{@link ConsumerConfig} binding to property :  "sq.grpc.consumer"</li>
 * </ul>
 * <p>
 * In contrast, on multiple bean bindings that requires to set {@link #multiple()} to be <code>true</code> :
 * <ul>
 * <li>{@link ApplicationConfig} binding to property : "sq.grpc.applications"</li>
 * <li>{@link ModuleConfig} binding to property :  "sq.grpc.modules"</li>
 * <li>{@link RegistryConfig} binding to property :  "sq.grpc.registries"</li>
 * <li>{@link ProtocolConfig} binding to property :  "sq.grpc.protocols"</li>
 * <li>{@link MonitorConfig} binding to property :  "sq.grpc.monitors"</li>
 * <li>{@link ProviderConfig} binding to property :  "sq.grpc.providers"</li>
 * <li>{@link ConsumerConfig} binding to property :  "sq.grpc.consumers"</li>
 * </ul>
 *
 * @see EnableSqGrpcConfigBinding
 * @see SqGrpcConfigConfiguration
 * @see SqGrpcConfigConfigurationRegistrar
 * @since 2.5.8
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(SqGrpcConfigConfigurationRegistrar.class)
public @interface EnableSqGrpcConfig {

    /**
     * It indicates whether binding to multiple Spring Beans.
     *
     * @return the default value is <code>false</code>
     * @revised 2.5.9
     */
    boolean multiple() default true;

}

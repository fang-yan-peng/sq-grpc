package com.sq.config.spring.context.annotation;


import org.springframework.context.annotation.Configuration;

import com.sq.config.AbstractConfig;
import com.sq.config.ApplicationConfig;
import com.sq.config.ConsumerConfig;
import com.sq.config.MetadataReportConfig;
import com.sq.config.ModuleConfig;
import com.sq.config.MonitorConfig;
import com.sq.config.ProtocolConfig;
import com.sq.config.ProviderConfig;
import com.sq.config.RegistryConfig;
import com.sq.config.spring.ConfigCenterBean;

/**
 * sq.grpc {@link AbstractConfig Config} {@link Configuration}
 *
 * @see Configuration
 * @see EnableSqGrpcConfigBindings
 * @see EnableSqGrpcConfigBinding
 * @see ApplicationConfig
 * @see ModuleConfig
 * @see RegistryConfig
 * @see ProtocolConfig
 * @see MonitorConfig
 * @see ProviderConfig
 * @see ConsumerConfig
 * @see
 * @since 2.5.8
 */
public class SqGrpcConfigConfiguration {

    /**
     * Single sq.grpc {@link AbstractConfig Config} Bean Binding
     */
    @EnableSqGrpcConfigBindings({
            @EnableSqGrpcConfigBinding(prefix = "sq.grpc.application", type = ApplicationConfig.class),
            @EnableSqGrpcConfigBinding(prefix = "sq.grpc.module", type = ModuleConfig.class),
            @EnableSqGrpcConfigBinding(prefix = "sq.grpc.registry", type = RegistryConfig.class),
            @EnableSqGrpcConfigBinding(prefix = "sq.grpc.protocol", type = ProtocolConfig.class),
            @EnableSqGrpcConfigBinding(prefix = "sq.grpc.monitor", type = MonitorConfig.class),
            @EnableSqGrpcConfigBinding(prefix = "sq.grpc.provider", type = ProviderConfig.class),
            @EnableSqGrpcConfigBinding(prefix = "sq.grpc.consumer", type = ConsumerConfig.class),
            @EnableSqGrpcConfigBinding(prefix = "sq.grpc.config-center", type = ConfigCenterBean.class),
            @EnableSqGrpcConfigBinding(prefix = "sq.grpc.metadata-report", type = MetadataReportConfig.class)
    })
    public static class Single {

    }

    /**
     * Multiple sq.grpc {@link AbstractConfig Config} Bean Binding
     */
    @EnableSqGrpcConfigBindings({
            @EnableSqGrpcConfigBinding(prefix = "sq.grpc.applications", type = ApplicationConfig.class, multiple = true),
            @EnableSqGrpcConfigBinding(prefix = "sq.grpc.modules", type = ModuleConfig.class, multiple = true),
            @EnableSqGrpcConfigBinding(prefix = "sq.grpc.registries", type = RegistryConfig.class, multiple = true),
            @EnableSqGrpcConfigBinding(prefix = "sq.grpc.protocols", type = ProtocolConfig.class, multiple = true),
            @EnableSqGrpcConfigBinding(prefix = "sq.grpc.monitors", type = MonitorConfig.class, multiple = true),
            @EnableSqGrpcConfigBinding(prefix = "sq.grpc.providers", type = ProviderConfig.class, multiple = true),
            @EnableSqGrpcConfigBinding(prefix = "sq.grpc.consumers", type = ConsumerConfig.class, multiple = true),
            @EnableSqGrpcConfigBinding(prefix = "sq.grpc.config-centers", type = ConfigCenterBean.class, multiple = true),
            @EnableSqGrpcConfigBinding(prefix = "sq.grpc.metadata-reports", type = MetadataReportConfig.class, multiple = true)
    })
    public static class Multiple {

    }
}

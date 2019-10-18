package com.sq.config.spring.context.annotation;


import static com.sq.config.spring.util.AnnotatedBeanDefinitionRegistryUtils.registerBeans;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import com.sq.config.AbstractConfig;

/**
 * {@link AbstractConfig Config} {@link ImportBeanDefinitionRegistrar register}, which order can be configured
 *
 * @see EnableSqGrpcConfig
 * @see SqGrpcConfigConfiguration
 * @see Ordered
 * @since 2.5.8
 */
public class SqGrpcConfigConfigurationRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(EnableSqGrpcConfig.class.getName()));

        boolean multiple = attributes.getBoolean("multiple");

        // Single Config Bindings
        registerBeans(registry, SqGrpcConfigConfiguration.Single.class);

        if (multiple) {
            registerBeans(registry, SqGrpcConfigConfiguration.Multiple.class);
        }
    }

}

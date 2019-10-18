package com.sq.config.spring.context.config;

import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;

import com.sq.config.AbstractConfig;
import com.sq.config.spring.annotation.SqGrpcConfigBindingBeanPostProcessor;
import com.sq.config.spring.context.properties.SqGrpcConfigBinder;

/**
 * The Bean customizer for {@link AbstractConfig  Config}. Generally, The subclass will be  registered as a Spring
 * Bean that is used to {@link #customize(String, AbstractConfig) customize} {@link AbstractConfig  Config} bean
 * after {@link SqGrpcConfigBinder#bind(String, AbstractConfig) its binding}.
 * <p>
 * If There are multiple {@link SqGrpcConfigBeanCustomizer} beans in the Spring {@link ApplicationContext context}, they
 * are executed orderly, thus the subclass should be aware to implement the {@link #getOrder()} method.
 *
 * @see SqGrpcConfigBinder#bind(String, AbstractConfig)
 * @see SqGrpcConfigBindingBeanPostProcessor
 * @since 2.6.6
 */
public interface SqGrpcConfigBeanCustomizer extends Ordered {

    /**
     * Customize {@link AbstractConfig  Config Bean}
     *
     * @param beanName        the name of {@link AbstractConfig  Config Bean}
     * @param configBean the instance of {@link AbstractConfig  Config Bean}
     */
    void customize(String beanName, AbstractConfig configBean);
}

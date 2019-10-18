package com.sq.config.spring.context.annotation;


import static com.sq.config.spring.annotation.ReferenceAnnotationBeanPostProcessor.BEAN_NAME;
import static com.sq.config.spring.util.BeanRegistrar.registerInfrastructureBean;
import static com.sq.config.spring.util.PropertySourcesUtils.getSubProperties;
import static com.sq.config.spring.util.PropertySourcesUtils.normalizePrefix;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;
import static org.springframework.beans.factory.support.BeanDefinitionReaderUtils.registerWithGeneratedName;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.sq.config.AbstractConfig;
import com.sq.config.spring.annotation.SqGrpcConfigBindingBeanPostProcessor;
import com.sq.config.spring.context.config.NamePropertyDefaultValueSqGrpcConfigBeanCustomizer;

/**
 * {@link AbstractConfig Config} binding Bean registrar
 *
 * @see EnableSqGrpcConfigBinding
 * @see SqGrpcConfigBindingBeanPostProcessor
 * @since 2.5.8
 */
public class SqGrpcConfigBindingRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private final Log log = LogFactory.getLog(getClass());

    private ConfigurableEnvironment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(EnableSqGrpcConfigBinding.class.getName()));

        registerBeanDefinitions(attributes, registry);

    }

    protected void registerBeanDefinitions(AnnotationAttributes attributes, BeanDefinitionRegistry registry) {

        String prefix = environment.resolvePlaceholders(attributes.getString("prefix"));

        Class<? extends AbstractConfig> configClass = attributes.getClass("type");

        boolean multiple = attributes.getBoolean("multiple");

        registerConfigBeans(prefix, configClass, multiple, registry);

    }

    private void registerConfigBeans(String prefix,
                                          Class<? extends AbstractConfig> configClass,
                                          boolean multiple,
                                          BeanDefinitionRegistry registry) {

        Map<String, Object> properties = getSubProperties(environment.getPropertySources(), prefix);

        if (CollectionUtils.isEmpty(properties)) {
            if (log.isDebugEnabled()) {
                log.debug("There is no property for binding to config class [" + configClass.getName()
                        + "] within prefix [" + prefix + "]");
            }
            return;
        }

        Set<String> beanNames = multiple ? resolveMultipleBeanNames(properties) :
                Collections.singleton(resolveSingleBeanName(properties, configClass, registry));

        for (String beanName : beanNames) {

            registerConfigBean(beanName, configClass, registry);

            registerConfigBindingBeanPostProcessor(prefix, beanName, multiple, registry);

        }

        registerConfigBeanCustomizers(registry);

    }

    private void registerConfigBean(String beanName, Class<? extends AbstractConfig> configClass,
                                         BeanDefinitionRegistry registry) {

        BeanDefinitionBuilder builder = rootBeanDefinition(configClass);

        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();

        registry.registerBeanDefinition(beanName, beanDefinition);

        if (log.isInfoEnabled()) {
            log.info("The config bean definition [name : " + beanName + ", class : " + configClass.getName() +
                    "] has been registered.");
        }

    }

    private void registerConfigBindingBeanPostProcessor(String prefix, String beanName, boolean multiple,
                                                             BeanDefinitionRegistry registry) {

        Class<?> processorClass = SqGrpcConfigBindingBeanPostProcessor.class;

        BeanDefinitionBuilder builder = rootBeanDefinition(processorClass);

        String actualPrefix = multiple ? normalizePrefix(prefix) + beanName : prefix;

        builder.addConstructorArgValue(actualPrefix).addConstructorArgValue(beanName);

        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();

        beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

        registerWithGeneratedName(beanDefinition, registry);

        if (log.isInfoEnabled()) {
            log.info("The BeanPostProcessor bean definition [" + processorClass.getName()
                    + "] for config bean [name : " + beanName + "] has been registered.");
        }

    }

    private void registerConfigBeanCustomizers(BeanDefinitionRegistry registry) {
        registerInfrastructureBean(registry, BEAN_NAME, NamePropertyDefaultValueSqGrpcConfigBeanCustomizer.class);
    }

    @Override
    public void setEnvironment(Environment environment) {

        Assert.isInstanceOf(ConfigurableEnvironment.class, environment);

        this.environment = (ConfigurableEnvironment) environment;

    }

    private Set<String> resolveMultipleBeanNames(Map<String, Object> properties) {

        Set<String> beanNames = new LinkedHashSet<String>();

        for (String propertyName : properties.keySet()) {

            int index = propertyName.indexOf(".");

            if (index > 0) {

                String beanName = propertyName.substring(0, index);

                beanNames.add(beanName);
            }

        }

        return beanNames;

    }

    private String resolveSingleBeanName(Map<String, Object> properties, Class<? extends AbstractConfig> configClass,
                                         BeanDefinitionRegistry registry) {

        String beanName = (String) properties.get("id");

        if (!StringUtils.hasText(beanName)) {
            BeanDefinitionBuilder builder = rootBeanDefinition(configClass);
            beanName = BeanDefinitionReaderUtils.generateBeanName(builder.getRawBeanDefinition(), registry);
        }

        return beanName;

    }

}
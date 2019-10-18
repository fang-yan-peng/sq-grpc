package com.sq.config.spring.annotation;

import static org.springframework.beans.factory.BeanFactoryUtils.beansOfTypeIncludingAncestors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import com.sq.config.AbstractConfig;
import com.sq.config.spring.context.config.SqGrpcConfigBeanCustomizer;
import com.sq.config.spring.context.properties.DefaultSqGrpcConfigBinder;
import com.sq.config.spring.context.properties.SqGrpcConfigBinder;

/**
 *  Config Binding {@link BeanPostProcessor}
 *
 * @since 2.5.8
 */

public class SqGrpcConfigBindingBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware, InitializingBean {

    private final Log log = LogFactory.getLog(getClass());

    /**
     * The prefix of Configuration Properties
     */
    private final String prefix;

    /**
     * Binding Bean Name
     */
    private final String beanName;

    private SqGrpcConfigBinder sqGrpcConfigBinder;

    private ApplicationContext applicationContext;

    private boolean ignoreUnknownFields = true;

    private boolean ignoreInvalidFields = true;

    private List<SqGrpcConfigBeanCustomizer> configBeanCustomizers = Collections.emptyList();

    /**
     * @param prefix   the prefix of Configuration Properties
     * @param beanName the binding Bean Name
     */
    public SqGrpcConfigBindingBeanPostProcessor(String prefix, String beanName) {
        Assert.notNull(prefix, "The prefix of Configuration Properties must not be null");
        Assert.notNull(beanName, "The name of bean must not be null");
        this.prefix = prefix;
        this.beanName = beanName;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if (beanName.equals(this.beanName) && bean instanceof AbstractConfig) {

            AbstractConfig config = (AbstractConfig) bean;

            bind(prefix, config);

            customize(beanName, config);

        }

        return bean;

    }

    private void bind(String prefix, AbstractConfig config) {

        sqGrpcConfigBinder.bind(prefix, config);

        if (log.isInfoEnabled()) {
            log.info("The properties of bean [name : " + beanName + "] have been binding by prefix of " +
                    "configuration properties : " + prefix);
        }
    }

    private void customize(String beanName, AbstractConfig config) {

        for (SqGrpcConfigBeanCustomizer customizer : configBeanCustomizers) {
            customizer.customize(beanName, config);
        }

    }

    public boolean isIgnoreUnknownFields() {
        return ignoreUnknownFields;
    }

    public void setIgnoreUnknownFields(boolean ignoreUnknownFields) {
        this.ignoreUnknownFields = ignoreUnknownFields;
    }

    public boolean isIgnoreInvalidFields() {
        return ignoreInvalidFields;
    }

    public void setIgnoreInvalidFields(boolean ignoreInvalidFields) {
        this.ignoreInvalidFields = ignoreInvalidFields;
    }

    public SqGrpcConfigBinder getSqGrpcConfigBinder() {
        return sqGrpcConfigBinder;
    }

    public void setSqGrpcConfigBinder(SqGrpcConfigBinder sqGrpcConfigBinder) {
        this.sqGrpcConfigBinder = sqGrpcConfigBinder;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        initConfigBinder();

        initConfigBeanCustomizers();

    }

    private void initConfigBinder() {

        if (sqGrpcConfigBinder == null) {
            try {
                sqGrpcConfigBinder = applicationContext.getBean(SqGrpcConfigBinder.class);
            } catch (BeansException ignored) {
                if (log.isDebugEnabled()) {
                    log.debug("SqGrpcConfigBinder Bean can't be found in ApplicationContext.");
                }
                // Use Default implementation
                sqGrpcConfigBinder = createConfigBinder(applicationContext.getEnvironment());
            }
        }

        sqGrpcConfigBinder.setIgnoreUnknownFields(ignoreUnknownFields);
        sqGrpcConfigBinder.setIgnoreInvalidFields(ignoreInvalidFields);

    }

    private void initConfigBeanCustomizers() {

        Collection<SqGrpcConfigBeanCustomizer> configBeanCustomizers =
                beansOfTypeIncludingAncestors(applicationContext, SqGrpcConfigBeanCustomizer.class).values();

        this.configBeanCustomizers = new ArrayList<>(configBeanCustomizers);

        AnnotationAwareOrderComparator.sort(this.configBeanCustomizers);
    }

    /**
     * Create {@link SqGrpcConfigBinder} instance.
     *
     * @param environment
     * @return {@link DefaultSqGrpcConfigBinder}
     */
    protected SqGrpcConfigBinder createConfigBinder(Environment environment) {
        DefaultSqGrpcConfigBinder defaultSqGrpcConfigBinder = new DefaultSqGrpcConfigBinder();
        defaultSqGrpcConfigBinder.setEnvironment(environment);
        return defaultSqGrpcConfigBinder;
    }

}
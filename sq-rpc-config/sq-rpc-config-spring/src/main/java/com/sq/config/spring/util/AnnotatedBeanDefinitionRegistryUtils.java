package com.sq.config.spring.util;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.util.ObjectUtils;

/**
 * Annotated {@link BeanDefinition} Utilities
 * <p>
 * @since 2.6.6
 */
public abstract class AnnotatedBeanDefinitionRegistryUtils {

    private static final Log logger = LogFactory.getLog(AnnotatedBeanDefinitionRegistryUtils.class);

    /**
     * Register Beans
     *
     * @param registry         {@link BeanDefinitionRegistry}
     * @param annotatedClasses {@link Annotation annotation} class
     */
    public static void registerBeans(BeanDefinitionRegistry registry, Class<?>... annotatedClasses) {

        if (ObjectUtils.isEmpty(annotatedClasses)) {
            return;
        }

        boolean debugEnabled = logger.isDebugEnabled();

        AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(registry);

        if (debugEnabled) {
            logger.debug(registry.getClass().getSimpleName() + " will register annotated classes : " + Arrays.asList(annotatedClasses) + " .");
        }

        reader.register(annotatedClasses);

    }
}

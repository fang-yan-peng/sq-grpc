package com.sq.config.spring.schema;

import static org.springframework.util.StringUtils.commaDelimitedListToStringArray;
import static org.springframework.util.StringUtils.trimArrayElements;


import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import com.sq.config.spring.annotation.ReferenceAnnotationBeanPostProcessor;
import com.sq.config.spring.annotation.ServiceAnnotationBeanPostProcessor;
import com.sq.config.spring.util.BeanRegistrar;

/**
 * @link BeanDefinitionParser}
 *
 * @see ServiceAnnotationBeanPostProcessor
 * @see ReferenceAnnotationBeanPostProcessor
 * @since 2.5.9
 */
public class AnnotationBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    /**
     * parse
     * <prev>
     * :annotation package="" /&gt;
     * </prev>
     *
     * @param element
     * @param parserContext
     * @param builder
     */
    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

        String packageToScan = element.getAttribute("package");

        String[] packagesToScan = trimArrayElements(commaDelimitedListToStringArray(packageToScan));

        builder.addConstructorArgValue(packagesToScan);

        builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

        // Registers ReferenceAnnotationBeanPostProcessor
        registerReferenceAnnotationBeanPostProcessor(parserContext.getRegistry());

    }

    @Override
    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }

    /**
     * Registers {@link ReferenceAnnotationBeanPostProcessor} into {@link BeanFactory}
     *
     * @param registry {@link BeanDefinitionRegistry}
     */
    private void registerReferenceAnnotationBeanPostProcessor(BeanDefinitionRegistry registry) {

        // Register @Reference Annotation Bean Processor
        BeanRegistrar.registerInfrastructureBean(registry,
                ReferenceAnnotationBeanPostProcessor.BEAN_NAME, ReferenceAnnotationBeanPostProcessor.class);

    }

    @Override
    protected Class<?> getBeanClass(Element element) {
        return ServiceAnnotationBeanPostProcessor.class;
    }

}

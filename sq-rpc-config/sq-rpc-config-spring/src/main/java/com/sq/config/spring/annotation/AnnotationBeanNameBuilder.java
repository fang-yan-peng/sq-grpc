package com.sq.config.spring.annotation;

import static com.sq.common.Constants.CONSUMERS_CATEGORY;
import static com.sq.common.Constants.PROVIDERS_CATEGORY;
import static com.sq.config.spring.util.AnnotationUtils.resolveInterfaceName;
import static org.springframework.util.StringUtils.arrayToCommaDelimitedString;
import static org.springframework.util.StringUtils.hasText;

import org.springframework.core.env.Environment;

import com.sq.common.Constants;
import com.sq.config.annotation.Reference;
import com.sq.config.annotation.Service;
import com.sq.registry.Registry;

/**
 * The Bean Name Builder for the annotations {@link Service} and {@link Reference}
 * <p>
 * The naming rule is consistent with the the implementation {@link Registry} that is based on the service-name aware
 * infrastructure, e.g Spring Cloud, Cloud Native and so on.
 * <p>
 * The pattern of bean name : ${category}:${protocol}:${serviceInterface}:${version}:${group}.
 * <p>
 * ${version} and ${group} are optional.
 *
 * @since 2.6.6
 */
class AnnotationBeanNameBuilder {

    private static final String SEPARATOR = ":";

    // Required properties

    private final String category;

    private final String protocol;

    private final String interfaceClassName;

    // Optional properties

    private String version;

    private String group;

    private Environment environment;

    private AnnotationBeanNameBuilder(String category, String protocol, String interfaceClassName) {
        this.category = category;
        this.protocol = protocol;
        this.interfaceClassName = interfaceClassName;
    }

    private AnnotationBeanNameBuilder(Service service, Class<?> interfaceClass) {
        this(PROVIDERS_CATEGORY, resolveProtocol(service.protocol()), resolveInterfaceName(service, interfaceClass));
        this.group(service.group());
        this.version(service.version());
    }

    private AnnotationBeanNameBuilder(Reference reference, Class<?> interfaceClass) {
        this(CONSUMERS_CATEGORY, resolveProtocol(reference.protocol()), resolveInterfaceName(reference, interfaceClass));
        this.group(reference.group());
        this.version(reference.version());
    }

    public static AnnotationBeanNameBuilder create(Service service, Class<?> interfaceClass) {
        return new AnnotationBeanNameBuilder(service, interfaceClass);
    }

    public static AnnotationBeanNameBuilder create(Reference reference, Class<?> interfaceClass) {
        return new AnnotationBeanNameBuilder(reference, interfaceClass);
    }

    private static void append(StringBuilder builder, String value) {
        if (hasText(value)) {
            builder.append(SEPARATOR).append(value);
        }
    }

    public AnnotationBeanNameBuilder group(String group) {
        this.group = group;
        return this;
    }

    public AnnotationBeanNameBuilder version(String version) {
        this.version = version;
        return this;
    }

    public AnnotationBeanNameBuilder environment(Environment environment) {
        this.environment = environment;
        return this;
    }

    /**
     * Resolve the protocol
     *
     * @param protocols one or more protocols
     * @return if <code>protocols</code> == <code>null</code>, it will return
     * {@link Constants#SQ_RPC_PROTOCOL "grpc"} as the default protocol
     * @see Constants#SQ_RPC_PROTOCOL
     */
    private static String resolveProtocol(String... protocols) {
        String protocol = arrayToCommaDelimitedString(protocols);
        return hasText(protocol) ? protocol : Constants.SQ_RPC_PROTOCOL;
    }

    /**
     * Build bean name while resolve the placeholders if possible.
     *
     * @return pattern : ${category}:${protocol}:${serviceInterface}:${version}:${group}
     */
    public String build() {
        // Append the required properties
        StringBuilder beanNameBuilder = new StringBuilder(category);
        append(beanNameBuilder, protocol);
        append(beanNameBuilder, interfaceClassName);
        // Append the optional properties
        append(beanNameBuilder, version);
        append(beanNameBuilder, group);
        String beanName = beanNameBuilder.toString();
        // Resolve placeholders
        return environment != null ? environment.resolvePlaceholders(beanName) : beanName;
    }
}

package com.sq.config.spring.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.core.env.PropertySources;

import com.sq.config.AbstractConfig;
import com.sq.config.ApplicationConfig;
import com.sq.config.ModuleConfig;
import com.sq.config.RegistryConfig;
import com.sq.config.spring.annotation.SqGrpcConfigBindingBeanPostProcessor;

/**
 * Enables Spring's annotation-driven {@link AbstractConfig Config} from {@link PropertySources properties}.
 * <p>
 * Default , {@link #prefix()} associates with a prefix of {@link PropertySources properties}, e,g. "sq.grpc.application."
 * or "sq.grpc.application"
 * <pre class="code">
 * <p>
 * </pre>
 *
 * @see SqGrpcConfigBindingRegistrar
 * @see SqGrpcConfigBindingBeanPostProcessor
 * @see EnableSqGrpcConfigBindings
 * @since 2.5.8
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(EnableSqGrpcConfigBindings.class)
@Import(SqGrpcConfigBindingRegistrar.class)
public @interface EnableSqGrpcConfigBinding {

    /**
     * The name prefix of the properties that are valid to bind to {@link AbstractConfig sq.grpc Config}.
     *
     * @return the name prefix of the properties to bind
     */
    String prefix();

    /**
     * @return The binding type of {@link AbstractConfig sq.grpc Config}.
     * @see AbstractConfig
     * @see ApplicationConfig
     * @see ModuleConfig
     * @see RegistryConfig
     */
    Class<? extends AbstractConfig> type();

    /**
     * It indicates whether {@link #prefix()} binding to multiple Spring Beans.
     *
     * @return the default value is <code>false</code>
     */
    boolean multiple() default false;

}

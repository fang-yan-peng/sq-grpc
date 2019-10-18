package com.sq.config.spring.context.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * Multiple {@link EnableSqGrpcConfigBinding} {@link Annotation}
 *
 * @since 2.5.8
 * @see EnableSqGrpcConfigBinding
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SqGrpcConfigBindingsRegistrar.class)
public @interface EnableSqGrpcConfigBindings {

    /**
     * The value of {@link EnableSqGrpcConfigBindings}
     *
     * @return non-null
     */
    EnableSqGrpcConfigBinding[] value();

}

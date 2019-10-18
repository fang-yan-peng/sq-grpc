package com.sq.config.spring.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

import com.sq.config.AbstractConfig;

/**
 * Enables sq grpc components as Spring Beans, equals
 * {@link SqGrpcComponentScan} and {@link EnableSqGrpcConfig} combination.
 * <p>
 * Note : {@link EnableSqGrpc} must base on Spring Framework 4.2 and above
 *
 * @see SqGrpcComponentScan
 * @see EnableSqGrpcConfig
 * @since 2.5.8
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@EnableSqGrpcConfig
@SqGrpcComponentScan
public @interface EnableSqGrpc {

    /**
     * Base packages to scan for annotated @Service classes.
     * <p>
     * Use {@link #scanBasePackageClasses()} for a type-safe alternative to String-based
     * package names.
     *
     * @return the base packages to scan
     * @see SqGrpcComponentScan#basePackages()
     */
    @AliasFor(annotation = SqGrpcComponentScan.class, attribute = "basePackages")
    String[] scanBasePackages() default {};

    /**
     * Type-safe alternative to {@link #scanBasePackages()} for specifying the packages to
     * scan for annotated @Service classes. The package of each class specified will be
     * scanned.
     *
     * @return classes from the base packages to scan
     * @see SqGrpcComponentScan#basePackageClasses
     */
    @AliasFor(annotation = SqGrpcComponentScan.class, attribute = "basePackageClasses")
    Class<?>[] scanBasePackageClasses() default {};


    /**
     * It indicates whether {@link AbstractConfig} binding to multiple Spring Beans.
     *
     * @return the default value is <code>false</code>
     * @see EnableSqGrpcConfig#multiple()
     */
    @AliasFor(annotation = EnableSqGrpcConfig.class, attribute = "multiple")
    boolean multipleConfig() default true;

}

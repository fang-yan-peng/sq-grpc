package com.sq.common.extension;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * services provider interface
 * @author yanpengfang
 * create 2019-09-17 4:04 PM
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SPI {

    /**
     * default extension nameDisableInject
     */
    String value() default "";
}

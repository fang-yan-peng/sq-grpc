package com.sq.config.spring.context.properties;

import org.springframework.context.EnvironmentAware;

import com.sq.config.AbstractConfig;

/**
 * {@link AbstractConfig Config} Binder
 *
 * @see AbstractConfig
 * @see EnvironmentAware
 * @since 2.5.11
 */
public interface SqGrpcConfigBinder extends EnvironmentAware {

    /**
     * Set whether to ignore unknown fields, that is, whether to ignore bind
     * parameters that do not have corresponding fields in the target object.
     * <p>Default is "true". Turn this off to enforce that all bind parameters
     * must have a matching field in the target object.
     *
     * @see #bind
     */
    void setIgnoreUnknownFields(boolean ignoreUnknownFields);

    /**
     * Set whether to ignore invalid fields, that is, whether to ignore bind
     * parameters that have corresponding fields in the target object which are
     * not accessible (for example because of null values in the nested path).
     * <p>Default is "false".
     *
     * @see #bind
     */
    void setIgnoreInvalidFields(boolean ignoreInvalidFields);

    /**
     *
     * @param prefix
     * @param config
     */
    <C extends AbstractConfig> void bind(String prefix, C config);
}

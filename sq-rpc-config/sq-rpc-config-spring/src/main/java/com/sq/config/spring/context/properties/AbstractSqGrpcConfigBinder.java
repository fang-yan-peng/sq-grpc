package com.sq.config.spring.context.properties;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

/**
 * Abstract {@link SqGrpcConfigBinder} implementation
 */
public abstract class AbstractSqGrpcConfigBinder implements SqGrpcConfigBinder {

    private Iterable<PropertySource<?>> propertySources;

    private boolean ignoreUnknownFields = true;

    private boolean ignoreInvalidFields = false;

    /**
     * Get multiple {@link PropertySource propertySources}
     *
     * @return multiple {@link PropertySource propertySources}
     */
    protected Iterable<PropertySource<?>> getPropertySources() {
        return propertySources;
    }

    public boolean isIgnoreUnknownFields() {
        return ignoreUnknownFields;
    }

    @Override
    public void setIgnoreUnknownFields(boolean ignoreUnknownFields) {
        this.ignoreUnknownFields = ignoreUnknownFields;
    }

    public boolean isIgnoreInvalidFields() {
        return ignoreInvalidFields;
    }

    @Override
    public void setIgnoreInvalidFields(boolean ignoreInvalidFields) {
        this.ignoreInvalidFields = ignoreInvalidFields;
    }

    @Override
    public final void setEnvironment(Environment environment) {

        if (environment instanceof ConfigurableEnvironment) {
            this.propertySources = ((ConfigurableEnvironment) environment).getPropertySources();
        }

    }
}

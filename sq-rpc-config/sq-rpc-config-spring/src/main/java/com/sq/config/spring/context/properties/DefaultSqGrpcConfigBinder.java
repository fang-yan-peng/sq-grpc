package com.sq.config.spring.context.properties;


import static com.sq.config.spring.util.PropertySourcesUtils.getSubProperties;

import java.util.Map;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.DataBinder;

import com.sq.config.AbstractConfig;

/**
 * Default {@link SqGrpcConfigBinder} implementation based on Spring {@link DataBinder}
 */
public class DefaultSqGrpcConfigBinder extends AbstractSqGrpcConfigBinder {

    @Override
    public <C extends AbstractConfig> void bind(String prefix, C config) {
        DataBinder dataBinder = new DataBinder(config);
        // Set ignored*
        dataBinder.setIgnoreInvalidFields(isIgnoreInvalidFields());
        dataBinder.setIgnoreUnknownFields(isIgnoreUnknownFields());
        // Get properties under specified prefix from PropertySources
        Map<String, Object> properties = getSubProperties(getPropertySources(), prefix);
        // Convert Map to MutablePropertyValues
        MutablePropertyValues propertyValues = new MutablePropertyValues(properties);
        // Bind
        dataBinder.bind(propertyValues);
    }

}


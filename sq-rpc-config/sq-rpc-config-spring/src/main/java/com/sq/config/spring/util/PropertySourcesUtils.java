package com.sq.config.spring.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;


/**
 * {@link PropertySources} Utilities
 * <p>
 *
 * @since 2.6.6
 */
public abstract class PropertySourcesUtils {

    /**
     * Get Sub {@link Properties}
     *
     * @param propertySources {@link PropertySource} Iterable
     * @param prefix          the prefix of property name
     * @return Map
     * @see Properties
     */
    public static Map<String, Object> getSubProperties(Iterable<PropertySource<?>> propertySources, String prefix) {

        // Non-Extension AbstractEnvironment
        AbstractEnvironment environment = new AbstractEnvironment() {
        };

        MutablePropertySources mutablePropertySources = environment.getPropertySources();

        for (PropertySource<?> source : propertySources) {
            mutablePropertySources.addLast(source);
        }

        return getSubProperties(environment, prefix);

    }

    /**
     * Get Sub {@link Properties}
     *
     * @param environment {@link ConfigurableEnvironment}
     * @param prefix      the prefix of property name
     * @return Map
     * @see Properties
     */
    public static Map<String, Object> getSubProperties(ConfigurableEnvironment environment, String prefix) {

        Map<String, Object> subProperties = new LinkedHashMap<>();

        MutablePropertySources propertySources = environment.getPropertySources();

        String normalizedPrefix = normalizePrefix(prefix);

        for (PropertySource<?> source : propertySources) {
            if (source instanceof EnumerablePropertySource) {
                for (String name : ((EnumerablePropertySource<?>) source).getPropertyNames()) {
                    if (!subProperties.containsKey(name) && name.startsWith(normalizedPrefix)) {
                        String subName = name.substring(normalizedPrefix.length());
                        if (!subProperties.containsKey(subName)) { // take first one
                            Object value = source.getProperty(name);
                            if (value instanceof String) {
                                // Resolve placeholder
                                value = environment.resolvePlaceholders((String) value);
                            }
                            subProperties.put(subName, value);
                        }
                    }
                }
            }
        }

        return Collections.unmodifiableMap(subProperties);

    }

    /**
     * Normalize the prefix
     *
     * @param prefix the prefix
     * @return the prefix
     */
    public static String normalizePrefix(String prefix) {
        return prefix.endsWith(".") ? prefix : prefix + ".";
    }
}

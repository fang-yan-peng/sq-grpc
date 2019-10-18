package com.sq.config.builders;

import java.util.HashMap;
import java.util.Map;

import com.sq.common.utils.StringUtils;
import com.sq.config.AbstractConfig;

/**
 * AbstractBuilder
 *
 * @since 2.7
 */
public abstract class AbstractBuilder<T extends AbstractConfig, B extends AbstractBuilder> {
    /**
     * The config id
     */
    protected String id;
    protected String prefix;

    protected B id(String id) {
        this.id = id;
        return getThis();
    }

    protected B prefix(String prefix) {
        this.prefix = prefix;
        return getThis();
    }

    protected abstract B getThis();

    protected static Map<String, String> appendParameter(Map<String, String> parameters, String key, String value) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put(key, value);
        return parameters;
    }

    protected static Map<String, String> appendParameters(Map<String, String> parameters, Map<String, String> appendParameters) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.putAll(appendParameters);
        return parameters;
    }

    protected void build(T instance) {
        if (!StringUtils.isEmpty(id)) {
            instance.setId(id);
        }
        if (!StringUtils.isEmpty(prefix)) {
            instance.setPrefix(prefix);
        }
    }
}

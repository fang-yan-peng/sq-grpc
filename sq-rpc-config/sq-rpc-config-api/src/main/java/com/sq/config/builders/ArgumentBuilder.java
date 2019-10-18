package com.sq.config.builders;

import com.sq.config.ArgumentConfig;

/**
 * This is a builder for build {@link ArgumentConfig}.
 * @since 2.7
 */
public class ArgumentBuilder {
    /**
     * The argument index: index -1 represents not set
     */
    private Integer index = -1;

    /**
     * Argument type
     */
    private String type;

    /**
     * Whether the argument is the callback interface
     */
    private Boolean callback;

    public ArgumentBuilder index(Integer index) {
        this.index = index;
        return this;
    }

    public ArgumentBuilder type(String type) {
        this.type = type;
        return this;
    }

    public ArgumentBuilder callback(Boolean callback) {
        this.callback = callback;
        return this;
    }

    public ArgumentConfig build() {
        ArgumentConfig argumentConfig = new ArgumentConfig();
        argumentConfig.setIndex(index);
        argumentConfig.setType(type);
        argumentConfig.setCallback(callback);
        return argumentConfig;
    }
}

package com.sq.config;

import java.io.Serializable;

import com.sq.config.annotation.Argument;
import com.sq.config.support.Parameter;


/**
 * The method arguments configuration
 *
 * @export
 */
public class ArgumentConfig implements Serializable {

    private static final long serialVersionUID = -2165482463925213595L;

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

    public ArgumentConfig() {
    }

    public ArgumentConfig(Argument argument) {
        this.index = argument.index();
        this.type = argument.type();
        this.callback = argument.callback();
    }

    @Parameter(excluded = true)
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Parameter(excluded = true)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCallback(Boolean callback) {
        this.callback = callback;
    }

    public Boolean isCallback() {
        return callback;
    }

}

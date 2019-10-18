package com.sq.config.spring.util;

/**
 * Object Utilities
 *
 * @since 2.6.6
 */
public abstract class ObjectUtils {

    /**
     * Convert from variable arguments to array
     *
     * @param values variable arguments
     * @param <T>    The class
     * @return array
     */
    public static <T> T[] of(T... values) {
        return values;
    }

}

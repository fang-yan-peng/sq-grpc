package com.sq.config.spring.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * {@link Class} Utilities
 * <p>
 * The source code is cloned from
 * https://github.com/alibaba/spring-context-support/blob/1.0.2/src/main/java/com/alibaba/spring/util/ClassUtils.java
 *
 * @since 2.6.6
 */
public abstract class ClassUtils {

    public static <T> Class<T> resolveGenericType(Class<?> declaredClass) {
        ParameterizedType parameterizedType = (ParameterizedType) declaredClass.getGenericSuperclass();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        return (Class<T>) actualTypeArguments[0];
    }
}

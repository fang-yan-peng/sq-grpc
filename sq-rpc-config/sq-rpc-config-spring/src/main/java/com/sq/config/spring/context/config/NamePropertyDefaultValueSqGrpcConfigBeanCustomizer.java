package com.sq.config.spring.context.config;

import static com.sq.config.spring.util.ObjectUtils.of;
import static org.springframework.beans.BeanUtils.getPropertyDescriptor;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.util.ReflectionUtils;

import com.sq.config.AbstractConfig;

/**
 * {@link SqGrpcConfigBeanCustomizer} for the default value for the "name" property that will be taken bean name
 * if absent.
 *
 * @since 2.6.6
 */
public class NamePropertyDefaultValueSqGrpcConfigBeanCustomizer implements SqGrpcConfigBeanCustomizer {

    /**
     * The bean name of {@link NamePropertyDefaultValueSqGrpcConfigBeanCustomizer}
     *
     * @since 2.7.1
     */
    public static final String BEAN_NAME = "namePropertyDefaultValueSqGrpcConfigBeanCustomizer";

    /**
     * The name of property that is "name" maybe is absent in target class
     */
    private static final String PROPERTY_NAME = "name";

    @Override
    public void customize(String beanName, AbstractConfig configBean) {

        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(configBean.getClass(), PROPERTY_NAME);

        if (propertyDescriptor != null) { // "name" property is present

            Method getNameMethod = propertyDescriptor.getReadMethod();

            if (getNameMethod == null) { // if "getName" method is absent
                return;
            }

            Object propertyValue = ReflectionUtils.invokeMethod(getNameMethod, configBean);

            if (propertyValue != null) { // If The return value of "getName" method is not null
                return;
            }

            Method setNameMethod = propertyDescriptor.getWriteMethod();
            if (setNameMethod != null) { // "setName" and "getName" methods are present
                if (Arrays.equals(of(String.class), setNameMethod.getParameterTypes())) { // the param type is String
                    // set bean name to the value of the "name" property
                    ReflectionUtils.invokeMethod(setNameMethod, configBean, beanName);
                }
            }
        }
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}

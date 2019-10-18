package com.sq.config.spring.annotation;

import static com.sq.config.spring.util.AnnotationUtils.getAttributes;

import java.lang.annotation.Annotation;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.core.env.PropertyResolver;

/**
 * {@link Annotation} {@link PropertyValues} Adapter
 *
 * @see Annotation
 * @see PropertyValues
 * @since 2.5.11
 */
class AnnotationPropertyValuesAdapter implements PropertyValues {

    private final Annotation annotation;

    private final PropertyResolver propertyResolver;

    private final boolean ignoreDefaultValue;

    private final PropertyValues delegate;

    public AnnotationPropertyValuesAdapter(Annotation annotation, PropertyResolver propertyResolver, boolean ignoreDefaultValue, String... ignoreAttributeNames) {
        this.annotation = annotation;
        this.propertyResolver = propertyResolver;
        this.ignoreDefaultValue = ignoreDefaultValue;
        this.delegate = adapt(annotation, ignoreDefaultValue, ignoreAttributeNames);
    }

    public AnnotationPropertyValuesAdapter(Annotation annotation, PropertyResolver propertyResolver, String... ignoreAttributeNames) {
        this(annotation, propertyResolver, true, ignoreAttributeNames);
    }

    private PropertyValues adapt(Annotation annotation, boolean ignoreDefaultValue, String... ignoreAttributeNames) {
        return new MutablePropertyValues(getAttributes(annotation, propertyResolver, ignoreDefaultValue, ignoreAttributeNames));
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public boolean isIgnoreDefaultValue() {
        return ignoreDefaultValue;
    }

    @Override
    public PropertyValue[] getPropertyValues() {
        return delegate.getPropertyValues();
    }

    @Override
    public PropertyValue getPropertyValue(String propertyName) {
        return delegate.getPropertyValue(propertyName);
    }

    @Override
    public PropertyValues changesSince(PropertyValues old) {
        return delegate.changesSince(old);
    }

    @Override
    public boolean contains(String propertyName) {
        return delegate.contains(propertyName);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }
}

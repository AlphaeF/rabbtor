package com.rabbtor.model.annotation;


import com.rabbtor.model.ModelMetadata;
import com.rabbtor.model.ModelMetadataProvider;
import com.rabbtor.model.ModelPropertyMetadata;
import com.rabbtor.model.UnsupportedModelException;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class AnnotationModelPropertyMetadata implements ModelPropertyMetadata
{

    private PropertyDescriptor propertyDescriptor;
    private ModelMetadataProvider metadataProvider;
    private Class<?> componentType;
    private String displayName;
    private String displayNameKey;
    private boolean bindable;
    private Property property;
    private TypeDescriptor typeDescriptor;


    public AnnotationModelPropertyMetadata(ModelMetadataProvider metadataProvider, PropertyDescriptor propertyDescriptor)
    {
        this.metadataProvider = metadataProvider;
        this.propertyDescriptor = propertyDescriptor;
        this.property = new Property(propertyDescriptor.getReadMethod().getDeclaringClass(),propertyDescriptor.getReadMethod(),propertyDescriptor.getWriteMethod());
        this.typeDescriptor = new TypeDescriptor(property);

        if (hasComponentType())
            this.componentType = resolveComponentType();

        setDefaultValues();
        processAnnotations();
    }

    protected void setDefaultValues()
    {
        bindable = true;
        displayName = null;
    }

    private boolean hasComponentType()
    {
        return isCollection() || isMap();
    }

    protected Class<?> resolveComponentType()
    {
        if (typeDescriptor.isArray())
            return getPropertyType().getComponentType();

        Type type = resolveGenericCompontentType();
        if (type == null)
            throw new UnsupportedModelException(String.format("Could not resolve component type for property %s of class %s", getName(), getDeclaringClass()));

        try {
            return (Class<?>)type;
        }catch (Throwable t) {
            throw new UnsupportedModelException(
                    String.format("Property %s of class %s contains a collection of unsupported type %s", getName(),getDeclaringClass(), type));
        }
    }

    private Type resolveGenericCompontentType()
    {
        Method read = propertyDescriptor.getReadMethod();
        Method write = propertyDescriptor.getWriteMethod();

        ParameterizedType parameterizedType = null;
        if (read != null)
            parameterizedType =(ParameterizedType)read.getGenericReturnType();
        else
            parameterizedType = (ParameterizedType)write.getGenericParameterTypes()[0];

        if (isCollection())
            return parameterizedType.getActualTypeArguments()[0];
        if (isMap())
            return parameterizedType.getActualTypeArguments()[1];

        return null;

    }

    private void processAnnotations()
    {
        ModelProperty anno = AnnotatedElementUtils.findMergedAnnotation(propertyDescriptor.getReadMethod(),ModelProperty.class);
        if (anno == null)
        {
            Field field = getField();
            if (field != null)
                anno = field.getAnnotation(ModelProperty.class);
        }

        if (anno != null) {
            setBindable(anno.bindable());
            setDisplayName(StringUtils.isEmpty(anno.displayName()) ? null : anno.displayName());
            setDisplayNameKey(StringUtils.isEmpty(anno.displayNameKey()) ? null : anno.displayNameKey());
        }

    }

    private Field getField()
    {
        String name = getName();
        if (!StringUtils.hasLength(name)) {
            return null;
        }
        Class<?> declaringClass = getDeclaringClass();
        Field field = ReflectionUtils.findField(declaringClass, name);
        if (field == null) {
            // Same lenient fallback checking as in CachedIntrospectionResults...
            field = ReflectionUtils.findField(declaringClass,
                    name.substring(0, 1).toLowerCase() + name.substring(1));
            if (field == null) {
                field = ReflectionUtils.findField(declaringClass,
                        name.substring(0, 1).toUpperCase() + name.substring(1));
            }
        }
        return field;
    }

    protected Class<?> getDeclaringClass()
    {
        if (propertyDescriptor.getReadMethod() != null) {
            return propertyDescriptor.getReadMethod().getDeclaringClass();
        }
        else {
            return propertyDescriptor.getWriteMethod().getDeclaringClass();
        }
    }

    protected void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    protected void setDisplayNameKey(String displayNameKey)
    {
        this.displayNameKey = displayNameKey;
    }



    protected void setBindable(boolean bindable)
    {
        this.bindable = bindable;
    }





    @Override
    public boolean isReadOnly()
    {

        return propertyDescriptor.getWriteMethod() ==null;
    }

    @Override
    public boolean isWriteOnly()
    {
        return propertyDescriptor.getReadMethod() != null;
    }

    @Override
    public String getName()
    {
        return propertyDescriptor.getName();
    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public String getDisplayNameKey()
    {
        return displayNameKey;
    }

    @Override
    public Class<?> getPropertyType()
    {
        return propertyDescriptor.getPropertyType();
    }

    @Override
    public boolean isPrimitive()
    {
        return typeDescriptor.isPrimitive();
    }

    @Override
    public boolean isCollection()
    {

        return typeDescriptor.isCollection() || typeDescriptor.isArray();
    }

    @Override
    public boolean isMap()
    {
        return typeDescriptor.isMap();
    }

    @Override
    public Class<?> getComponentType()
    {
        return componentType;
    }

    @Override
    public ModelMetadata getComponentMetadata()
    {
        if (getComponentType() == null)
            return null;
        return metadataProvider.getModelMetadata(getComponentType());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnnotationModelPropertyMetadata that = (AnnotationModelPropertyMetadata) o;

        return propertyDescriptor.equals(that.propertyDescriptor);

    }

    @Override
    public int hashCode()
    {
        return propertyDescriptor.hashCode();
    }
}

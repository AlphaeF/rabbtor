package com.rabbtor.model.annotation;


import com.rabbtor.model.ModelMetadata;
import com.rabbtor.model.ModelMetadataProvider;
import com.rabbtor.model.ModelPropertyMetadata;
import com.rabbtor.model.UnsupportedModelException;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class AnnotationBeanPropertyMetadata implements ModelPropertyMetadata
{

    private PropertyDescriptor propertyDescriptor;
    private ModelMetadata declaringModelMetadata;
    private ModelMetadataProvider metadataProvider;
    private Class<?> componentType;
    private String displayName;
    private String modelName;
    private Property property;
    private TypeDescriptor typeDescriptor;


    public AnnotationBeanPropertyMetadata(ModelMetadata declaringModelMetadata,ModelMetadataProvider metadataProvider, PropertyDescriptor propertyDescriptor)
    {
        this.declaringModelMetadata = declaringModelMetadata;
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
        modelName = propertyDescriptor.getName();
        displayName = modelName;
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
            throw new UnsupportedModelException(String.format("Could not resolveOrDefault component type for property %s of class %s", getName(), getDeclaringClass()));

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
        ModelName anno = AnnotatedElementUtils.findMergedAnnotation(propertyDescriptor.getReadMethod(),ModelName.class);
        if (anno == null)
        {
            Field field = getField();
            if (field != null)
                anno = field.getAnnotation(ModelName.class);
        }

        if (anno != null) {
            setModelName(StringUtils.isEmpty(anno.value()) ? null : anno.value());
            setDisplayName(StringUtils.isEmpty(anno.displayName()) ? null : anno.displayName());
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

    public Class<?> getDeclaringClass()
    {
//        if (propertyDescriptor.getReadMethod() != null) {
//            return propertyDescriptor.getReadMethod().getDeclaringClass();
//        }
//        else {
//            return propertyDescriptor.getWriteMethod().getDeclaringClass();
//        }
        return getDeclaringModelMetadata().getModelType();
    }

    protected void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    protected void setModelName(String modelName)
    {
        this.modelName = modelName;
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
    public String getModelName()
    {
        return modelName;
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
    public ModelMetadata getPropertyTypeMetadata()
    {
        return metadataProvider.getModelMetadata(getPropertyType());
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnnotationBeanPropertyMetadata that = (AnnotationBeanPropertyMetadata) o;

        return propertyDescriptor.equals(that.propertyDescriptor);

    }

    public ModelMetadata getDeclaringModelMetadata()
    {
        return declaringModelMetadata;
    }

    @Override
    public int hashCode()
    {
        return propertyDescriptor.hashCode();
    }
}

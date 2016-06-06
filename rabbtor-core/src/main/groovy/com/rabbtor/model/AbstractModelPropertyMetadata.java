
package com.rabbtor.model;


import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbstractModelPropertyMetadata implements ModelPropertyMetadata
{
    private PropertyDescriptor propertyDescriptor;
    private ModelMetadata declaringModelMetadata;
    private ModelMetadataProvider metadataProvider;
    private Class<?> componentType;
    private TypeDescriptor typeDescriptor;
    private String modelName;
    private String displayName;

    protected AbstractModelPropertyMetadata(ModelMetadata declaringModelMetadata,ModelMetadataProvider metadataProvider, PropertyDescriptor propertyDescriptor)
    {
        this.declaringModelMetadata = declaringModelMetadata;
        this.metadataProvider = metadataProvider;
        this.propertyDescriptor = propertyDescriptor;
        Property property = createProperty(propertyDescriptor);
        this.typeDescriptor = new TypeDescriptor(property);

        if (hasComponentType())
            this.componentType = resolveComponentType();
    }


    protected PropertyDescriptor getPropertyDescriptor()
    {
        return propertyDescriptor;
    }

    protected TypeDescriptor getTypeDescriptor()
    {
        return typeDescriptor;
    }

    private Property createProperty(PropertyDescriptor propertyDescriptor)
    {

        return new Property(getDeclaringClass(),propertyDescriptor.getReadMethod(),propertyDescriptor.getWriteMethod());
    }


    protected boolean hasComponentType()
    {
        return isCollection() || isMap();
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

    protected Class<?> resolveComponentType()
    {
        if (typeDescriptor.isArray())
            return getPropertyType().getComponentType();

        Type type = null;
        try
        {
            type = resolveGenericCompontentType();
        } catch (UnsupportedOperationException ex) {
            throw new UnsupportedModelException(String.format("Could not resolve component type for property %s of class %s", getPropertyName(), getDeclaringClass()),ex);
        }

        try {
            return (Class<?>)type;
        }catch (Throwable t) {
            throw new UnsupportedModelException(
                    String.format("Property %s of class %s contains a collection of unsupported type %s", getPropertyName(),getDeclaringClass(), type));
        }
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
    public String getPropertyName()
    {
        return propertyDescriptor.getName();
    }

    protected Type resolveGenericCompontentType()
    {
        Method read = propertyDescriptor.getReadMethod();
        Method write = propertyDescriptor.getWriteMethod();

        ParameterizedType parameterizedType = null;
        if (read != null)
            parameterizedType =(ParameterizedType)read.getGenericReturnType();
        else
            parameterizedType = (ParameterizedType)write.getGenericParameterTypes()[0];

        if (typeDescriptor.isCollection() || typeDescriptor.isArray())
            return parameterizedType.getActualTypeArguments()[0];
        if (typeDescriptor.isMap())
            return parameterizedType.getActualTypeArguments()[1];

        throw new UnsupportedOperationException(String.format("Component type of property could not be resolved for property: %s and type descriptor: %s", propertyDescriptor, typeDescriptor));
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

        AbstractModelPropertyMetadata that = (AbstractModelPropertyMetadata) o;

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

    @Override
    public Class<?> getComponentType()
    {
        if (hasComponentType() && this.componentType == null)
            this.componentType = resolveComponentType();
        return this.componentType;
    }

    @Override
    public ModelMetadata getComponentMetadata()
    {
        if (getComponentType() == null)
            return null;
        return metadataProvider.getModelMetadata(getComponentType());
    }

    protected Field getField()
    {
        String name = getPropertyName();
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

}

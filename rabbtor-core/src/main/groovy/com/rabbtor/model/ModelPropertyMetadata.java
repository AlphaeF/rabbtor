package com.rabbtor.model;


import java.beans.PropertyDescriptor;

public interface ModelPropertyMetadata
{

    boolean isReadOnly();

    boolean isWriteOnly();

    String getPropertyName();

    String getDisplayName();

    String getModelName();

    Class<?> getPropertyType();

    boolean isPrimitive();

    boolean isCollection();

    boolean isMap();

    Class<?> getComponentType();

    ModelMetadata getComponentMetadata();

    ModelMetadata getPropertyTypeMetadata();

    Class<?> getDeclaringClass();

    ModelMetadata getDeclaringModelMetadata();

}

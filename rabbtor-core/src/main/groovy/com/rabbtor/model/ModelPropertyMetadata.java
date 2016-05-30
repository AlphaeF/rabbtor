package com.rabbtor.model;


public interface ModelPropertyMetadata
{

    boolean isReadOnly();

    boolean isWriteOnly();

    String getName();

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

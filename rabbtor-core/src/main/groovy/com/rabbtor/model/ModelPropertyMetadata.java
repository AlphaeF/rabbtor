package com.rabbtor.model;


import org.springframework.core.convert.TypeDescriptor;

import java.beans.PropertyDescriptor;

public interface ModelPropertyMetadata
{

    boolean isReadOnly();

    boolean isWriteOnly();

    String getName();

    String getDisplayName();

    String getDisplayNameKey();

    Class<?> getPropertyType();

    boolean isPrimitive();

    boolean isCollection();

    boolean isMap();

    Class<?> getComponentType();

    ModelMetadata getComponentMetadata();





}

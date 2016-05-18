package com.rabbtor.model;


import org.springframework.core.convert.TypeDescriptor;

import java.util.Enumeration;
import java.util.Set;

public interface ModelMetadata
{
    String getName();

    Class<?> getModelType();

    Set<ModelPropertyMetadata> getProperties();


}

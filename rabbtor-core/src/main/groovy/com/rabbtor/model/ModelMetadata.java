package com.rabbtor.model;


import java.util.Set;

public interface ModelMetadata
{
    String getModelName();

    Class<?> getModelType();

    Set<ModelPropertyMetadata> getProperties();


}

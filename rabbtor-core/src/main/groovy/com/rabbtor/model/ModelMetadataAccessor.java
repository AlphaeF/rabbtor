package com.rabbtor.model;


public interface ModelMetadataAccessor
{
    ModelMetadata getModelMetadata();

    ModelPropertyMetadata getPropertyMetadata(String propertyPath);

    String[] getModelNameCodes(String propertyPath);

    String[] getModelNameCodes(String propertyPath, String modelName);

    String getDisplayName(String propertyPath);

    String getModelName();

    void setModelName(String modelName);


}

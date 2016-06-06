
package com.rabbtor.model;


public interface ModelMetadataProvider
{
    ModelMetadata getModelMetadata(Class<?> modelType);
}

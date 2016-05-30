package com.rabbtor.model;


public interface ModelMetadataAccessorFactory
{
    ModelMetadataAccessor getMetadataAccessor(Class modelType);
}

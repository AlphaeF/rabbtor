package com.rabbtor.model;


import org.springframework.beans.factory.annotation.Autowired;

public class DefaultModelMetadataAccessorFactory implements ModelMetadataAccessorFactory
{
    @Autowired(required = false)
    ModelMetadataRegistry modelMetadataRegistry;

    @Override
    public ModelMetadataAccessor getMetadataAccessor(Class modelType)
    {
        DefaultModelMetadataAccessor accessor = new DefaultModelMetadataAccessor(modelType);
        if (modelMetadataRegistry != null)
            accessor.setMetadataRegistry(modelMetadataRegistry);
        return accessor;
    }

    public void setModelMetadataRegistry(ModelMetadataRegistry modelMetadataRegistry)
    {
        this.modelMetadataRegistry = modelMetadataRegistry;
    }
}

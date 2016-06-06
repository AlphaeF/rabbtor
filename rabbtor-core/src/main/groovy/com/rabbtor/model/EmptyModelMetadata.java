
package com.rabbtor.model;


import java.util.Collections;
import java.util.Set;

public class EmptyModelMetadata implements ModelMetadata
{
    private Class<?> modelType;

    public EmptyModelMetadata(Class<?> modelType)
    {
        this.modelType = modelType;
    }

    @Override
    public String getModelName()
    {
        return modelType.getName();
    }

    @Override
    public Class<?> getModelType()
    {
        return modelType;
    }

    @Override
    public Set<ModelPropertyMetadata> getProperties()
    {
        return Collections.unmodifiableSet(Collections.emptySet());
    }
}

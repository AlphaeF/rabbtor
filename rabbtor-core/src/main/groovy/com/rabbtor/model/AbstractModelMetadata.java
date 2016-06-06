
package com.rabbtor.model;



import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractModelMetadata implements ModelMetadata
{
    private ModelMetadataProvider metadataProvider;
    private Class<?> modelType;
    private Set<ModelPropertyMetadata> properties;
    private String name;
    private boolean nameResolved;

    protected AbstractModelMetadata(ModelMetadataProvider metadataProvider, Class<?> modelType)
    {
        Assert.notNull(metadataProvider,"modelMetadataRegistry must not be null.");
        this.metadataProvider = metadataProvider;
        this.modelType = modelType;
    }

    public ModelMetadataProvider getMetadataProvider()
    {
        return metadataProvider;
    }

    @Override
    public Class<?> getModelType()
    {
        return modelType;
    }

    @Override
    public Set<ModelPropertyMetadata> getProperties()
    {
        if (properties == null) {
            properties = new HashSet<>();
            PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(getModelType());
            for (PropertyDescriptor pd : propertyDescriptors) {
                if (pd.getName().equalsIgnoreCase("class"))
                    continue;

                ModelPropertyMetadata metadata = createPropertyMetadata(pd);
                properties.add(metadata);
            }
        }
        properties = Collections.unmodifiableSet(properties);
        return properties;
    }

    protected abstract ModelPropertyMetadata createPropertyMetadata(PropertyDescriptor pd);

    @Override
    public String getModelName()
    {
        if (!nameResolved)
        {
            name = resolveName();
            nameResolved = true;
        }
        if (name == null)
            name = createDefaultName();

        return name;
    }

    private String createDefaultName()
    {
        return ClassUtils.getShortNameAsProperty(getModelType());
    }

    protected abstract String resolveName();


}

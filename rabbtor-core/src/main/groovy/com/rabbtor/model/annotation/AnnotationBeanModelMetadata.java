package com.rabbtor.model.annotation;


import com.rabbtor.model.ModelMetadata;
import com.rabbtor.model.ModelMetadataProvider;
import com.rabbtor.model.ModelPropertyMetadata;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.util.*;

public class AnnotationBeanModelMetadata implements ModelMetadata
{
    private Class<?> modelType;
    private ModelMetadataProvider metadataProvider;
    private Set<ModelPropertyMetadata> properties;
    private String name;

    public AnnotationBeanModelMetadata(ModelMetadataProvider metadataProvider, Class<?> modelType)
    {
        this.metadataProvider = metadataProvider;
        this.modelType = modelType;
        this.name = resolveName();
    }

    protected String resolveName()
    {
        Model modelAnno = AnnotationUtils.getAnnotation(getModelType(),Model.class);
        if (modelAnno != null) {
            return modelAnno.name();
        }
        return null;
    }

    @Override
    public String getName()
    {
        return name == null ? getModelType().getName() : name;
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

                ModelPropertyMetadata metadata = new AnnotationModelPropertyMetadata(metadataProvider,pd);
                properties.add(metadata);
            }
        }
        properties = Collections.unmodifiableSet(properties);
        return properties;
    }
}


package com.rabbtor.model.annotation;


import com.rabbtor.model.AbstractModelMetadata;
import com.rabbtor.model.ModelMetadataProvider;
import com.rabbtor.model.ModelPropertyMetadata;
import org.springframework.core.annotation.AnnotationUtils;

import java.beans.PropertyDescriptor;

public class AnnotationBeanModelMetadata extends AbstractModelMetadata
{



    public AnnotationBeanModelMetadata(ModelMetadataProvider metadataProvider, Class<?> modelType)
    {
        super(metadataProvider, modelType);

    }

    @Override
    protected ModelPropertyMetadata createPropertyMetadata(PropertyDescriptor pd)
    {
        return new AnnotationBeanPropertyMetadata(this,getMetadataProvider(),pd);
    }

    protected String resolveName()
    {
        Model modelAnno = AnnotationUtils.getAnnotation(getModelType(),Model.class);
        if (modelAnno != null) {
            return modelAnno.name();
        }
        return null;
    }




}

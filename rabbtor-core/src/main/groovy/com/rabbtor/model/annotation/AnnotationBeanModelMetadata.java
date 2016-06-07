/**
 * Copyright 2016 - Rabbytes Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Rabbytes Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Rabbytes Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Rabbytes Incorporated.
 */
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

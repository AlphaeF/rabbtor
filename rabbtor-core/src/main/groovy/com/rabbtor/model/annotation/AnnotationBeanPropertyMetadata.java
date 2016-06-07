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


import com.rabbtor.model.*;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;

import java.beans.*;
import java.lang.reflect.Field;

public class AnnotationBeanPropertyMetadata extends AbstractModelPropertyMetadata
{
    private String modelName;
    private String displayName;

    public AnnotationBeanPropertyMetadata(ModelMetadata declaringModelMetadata, ModelMetadataProvider metadataProvider, PropertyDescriptor propertyDescriptor)
    {
        super(declaringModelMetadata, metadataProvider, propertyDescriptor);
        processAnnotations();
    }

    private void processAnnotations()
    {
        DisplayName anno = AnnotatedElementUtils.findMergedAnnotation(getPropertyDescriptor().getReadMethod(),DisplayName.class);
        if (anno == null)
        {
            Field field = getField();
            if (field != null)
                anno = field.getAnnotation(DisplayName.class);
        }

        if (anno != null) {
            displayName = (StringUtils.isEmpty(anno.value()) ? null : anno.value());
            modelName = (StringUtils.isEmpty(anno.key()) ? null : anno.key());
        }

    }

    @Override
    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public String getModelName()
    {
        return modelName;
    }

    public Class<?> getDeclaringClass()
    {
        return getDeclaringModelMetadata().getModelType();
    }










}

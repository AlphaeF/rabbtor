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


import com.rabbtor.model.ModelMetadata;
import com.rabbtor.model.ModelMetadataProvider;
import org.springframework.core.Ordered;

public class AnnotationModelMetadataProvider implements ModelMetadataProvider,Ordered
{
    private int order = Ordered.LOWEST_PRECEDENCE;

    @Override
    public ModelMetadata getModelMetadata(Class<?> modelType)
    {
        return new AnnotationBeanModelMetadata(this,modelType);
    }


    @Override
    public int getOrder()
    {
        return order;
    }

    public void setOrder(int order)
    {
        this.order = order;
    }
}

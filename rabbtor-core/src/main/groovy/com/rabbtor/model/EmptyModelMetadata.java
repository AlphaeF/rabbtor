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

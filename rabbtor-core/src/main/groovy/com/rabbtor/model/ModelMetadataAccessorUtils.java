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


import org.springframework.beans.factory.ListableBeanFactory;

/**
 * Utility facade to retrieve a {@link ModelMetadataAccessor}
 */
public class ModelMetadataAccessorUtils
{

    /**
     * If the {@code beanFactory} given is not null, searches for a {@link ModelMetadataAccessorFactory}
     * from the {@code beanFactory}. If found, a new {@link ModelMetadataAccessor} is requested from the factory.
     * <p>
     *     If a factory could not be resolved, returns a new instance of {@link DefaultModelMetadataAccessor} for the
     *     given {@code modelType}
     * </p>
     *
     *
     * @param modelType  model class for which a metadata accessor is requested
     * @param beanFactory beanFactory in which the metadata accessor factory is searched
     * @return {@link ModelMetadataAccessor} resolved from the {@code beanFactory} or default implementation
     *
     * @see ModelMetadataAccessorFactory
     * @see DefaultModelMetadataAccessor
     * @see ModelMetadataAccessor
     */
    public static ModelMetadataAccessor lookup(Class modelType, ListableBeanFactory beanFactory) {
        if (beanFactory != null)
        {
            ModelMetadataAccessorFactory factory = null;
            try
            {
                factory = beanFactory.getBean(ModelMetadataAccessorFactory.class);
                return factory.getMetadataAccessor(modelType);
            } catch ( Exception ex) {

            }
        }
        return new DefaultModelMetadataAccessor(modelType);
    }


}

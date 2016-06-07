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

public class ModelMetadataAccessorUtils
{
    public static ModelMetadataAccessor resolveOrDefault(Class modelType, ListableBeanFactory beanFactory) {
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

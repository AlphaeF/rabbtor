
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

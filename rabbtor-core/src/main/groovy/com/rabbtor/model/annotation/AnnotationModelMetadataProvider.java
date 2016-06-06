
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

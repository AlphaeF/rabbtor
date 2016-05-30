package com.rabbtor.model;


import com.rabbtor.model.annotation.AnnotationModelMetadataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultModelMetadataRegistry implements ModelMetadataRegistry
{
    private List<ModelMetadataProvider> providers = new ArrayList<>();
    private Map<Class<?>,ModelMetadata> metadataMap = new ConcurrentHashMap<>();

    public DefaultModelMetadataRegistry()
    {
        providers.add(new AnnotationModelMetadataProvider());
    }

    @Autowired(required = false)
    public void setProviders(List<ModelMetadataProvider> providers)
    {
        if (providers != null)
            this.providers.addAll(providers);
        AnnotationAwareOrderComparator.sort(this.providers);
    }

    public List<ModelMetadataProvider> getProviders()
    {
        return Collections.unmodifiableList(providers);
    }

    @Override
    public ModelMetadata getModelMetadata(Class<?> modelType)
    {
        if (metadataMap.containsKey(modelType))
            return metadataMap.get(modelType);

        ModelMetadata metadata = null;
        for(ModelMetadataProvider provider : providers) {
            metadata = provider.getModelMetadata(modelType);
            if (metadata != null)
                break;
        }
        if (metadata == null)
            metadata = new EmptyModelMetadata(modelType);

        metadataMap.put(modelType,metadata);
        return metadata;
    }


}

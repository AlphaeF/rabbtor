/**
 * Copyright 2016 - Rabbytes Incorporated
 * All Rights Reserved.
 * <p>
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
    private Map<Class<?>, ModelMetadata> metadataMap = new ConcurrentHashMap<>();
    private boolean cacheEnabled = true;

    public DefaultModelMetadataRegistry()
    {
        providers.add(new AnnotationModelMetadataProvider());
    }

    public boolean isCacheEnabled()
    {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled)
    {
        this.cacheEnabled = cacheEnabled;
        if (!cacheEnabled)
            clearCache();
    }

    public void clearCache()
    {
        metadataMap.clear();
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
        if (isCacheEnabled() && metadataMap.containsKey(modelType))
            return metadataMap.get(modelType);

        ModelMetadata metadata = null;
        for (ModelMetadataProvider provider : providers)
        {
            metadata = provider.getModelMetadata(modelType);
            if (metadata != null)
                break;
        }
        if (metadata == null)
            metadata = new EmptyModelMetadata(modelType);

        if (isCacheEnabled())
            metadataMap.put(modelType, metadata);

        return metadata;
    }


}

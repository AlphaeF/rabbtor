
package com.rabbtor.model;


import java.util.Collection;

public interface ModelMetadataProviderRegistry extends ModelMetadataProvider
{
    Collection<ModelMetadataProvider> getProviders();

}

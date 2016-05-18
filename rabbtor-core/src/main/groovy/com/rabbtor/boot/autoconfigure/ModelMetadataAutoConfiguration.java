package com.rabbtor.boot.autoconfigure;


import com.rabbtor.model.DefaultModelMetadataProvider;
import com.rabbtor.model.ModelMetadataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ModelMetadataAutoConfiguration
{
    @Autowired(required = false)
    private List<ModelMetadataProvider> providers;

    @ConditionalOnMissingBean(name = "defaultModelMetadataProvider")
    @Bean
    public ModelMetadataProvider defaultModelMetadataProvider() {
        DefaultModelMetadataProvider provider = new DefaultModelMetadataProvider();
        provider.setProviders(providers);
        return provider;
    }
}

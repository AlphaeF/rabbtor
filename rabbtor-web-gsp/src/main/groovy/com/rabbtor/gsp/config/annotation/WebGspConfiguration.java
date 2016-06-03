package com.rabbtor.gsp.config.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Configuration
public class WebGspConfiguration extends WebGspConfigurationSupport
{
    private List<WebGspConfigurer> configurers;

    @Autowired(required = false)
    public void setConfigurers(List<WebGspConfigurer> configurers)
    {
        if (configurers == null)
            this.configurers = Collections.emptyList();
        else
            this.configurers = configurers;

        if (this.configurers.size() > 0)
            AnnotationAwareOrderComparator.sort(this.configurers);
    }

    @Override
    protected void registerTagLibClasses(Set<Class<?>> tagLibClasses)
    {
        super.registerTagLibClasses(tagLibClasses);
        for (WebGspConfigurer configurer : configurers) {
            configurer.registerGspTagLibraries(tagLibClasses);
        }

    }

    @Override
    protected void configureGsp(GspTemplateEngineConfig config)
    {
        super.configureGsp(config);
        for (WebGspConfigurer configurer : configurers) {
            configurer.configureGsp(config);
        }
    }
}

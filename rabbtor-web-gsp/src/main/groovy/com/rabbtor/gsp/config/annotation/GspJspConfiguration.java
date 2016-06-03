package com.rabbtor.gsp.config.annotation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.Collections;
import java.util.List;

@Configuration
@Conditional(JspCondition.class)
public class GspJspConfiguration extends GspJspConfigurationSupport
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
    protected void registerTldScanPaths(List<String> paths)
    {
        super.registerTldScanPaths(paths);
        for (WebGspConfigurer configurer : configurers) {
            configurer.registerTldScanPaths(paths);
        }
    }
}

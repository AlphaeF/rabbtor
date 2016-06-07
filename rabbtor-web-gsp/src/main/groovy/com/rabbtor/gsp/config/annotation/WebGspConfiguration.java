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
    protected void configureGsp(WebGspSettings config)
    {
        super.configureGsp(config);
        for (WebGspConfigurer configurer : configurers) {
            configurer.configureGsp(config);
        }
    }
}

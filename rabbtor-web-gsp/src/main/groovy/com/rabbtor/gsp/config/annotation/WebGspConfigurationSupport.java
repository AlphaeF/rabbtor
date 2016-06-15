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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.grails.gsp.GroovyPagesTemplateEngine;
import org.grails.gsp.io.GroovyPageLocator;
import org.grails.plugins.web.taglib.RenderTagLib;
import org.grails.plugins.web.taglib.SitemeshTagLib;
import org.grails.web.gsp.GroovyPagesTemplateRenderer;
import org.grails.web.gsp.io.CachingGrailsConventionGroovyPageLocator;
import org.grails.web.gsp.io.GrailsConventionGroovyPageLocator;
import org.grails.web.servlet.view.GrailsLayoutViewResolver;
import org.grails.web.servlet.view.GroovyPageViewResolver;
import org.grails.web.sitemesh.GroovyPageLayoutFinder;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Primary;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ViewResolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class WebGspConfigurationSupport extends GspConfigurationSupport
{

    private static Log LOG = LogFactory.getLog(WebGspConfigurationSupport.class);

    @Override
    protected GroovyPageLocator createGroovyPageLocator()
    {
        final List<String> templateRootsCleaned=resolveTemplateRoots();
        CachingGrailsConventionGroovyPageLocator pageLocator = new CachingGrailsConventionGroovyPageLocator() {
            protected List<String> resolveSearchPaths(String uri) {
                List<String> paths=new ArrayList<String>(templateRootsCleaned.size());
                for(String rootPath : templateRootsCleaned) {
                    paths.add(rootPath + cleanUri(uri));
                }
                return paths;
            }

            protected String cleanUri(String uri) {
                uri = StringUtils.cleanPath(uri);
                if(!uri.startsWith("/")) {
                    uri = "/" + uri;
                }
                return uri;
            }
        };
        pageLocator.setReloadEnabled(gspTemplateEngineConfig().gspReloadingEnabled);
        pageLocator.setCacheTimeout(gspTemplateEngineConfig().locatorCacheTimeout);

        return pageLocator;
    }



    @Bean
    public GroovyPageLayoutFinder groovyPageLayoutFinder() {
        GroovyPageLayoutFinder groovyPageLayoutFinder = new GroovyPageLayoutFinder();
        groovyPageLayoutFinder.setGspReloadEnabled(gspTemplateEngineConfig().gspReloadingEnabled);
        groovyPageLayoutFinder.setCacheEnabled(gspTemplateEngineConfig().gspLayoutCaching);
        groovyPageLayoutFinder.setEnableNonGspViews(false);
        groovyPageLayoutFinder.setDefaultDecoratorName(gspTemplateEngineConfig().defaultLayoutName);
        return groovyPageLayoutFinder;
    }



    @Bean
    public GrailsLayoutViewResolver gspViewResolver() {
        return new GrailsLayoutViewResolver(innerGspViewResolver(), groovyPageLayoutFinder());
    }

    protected ViewResolver innerGspViewResolver() {
        GroovyPageViewResolver innerGspViewResolver = new GroovyPageViewResolver(groovyPagesTemplateEngine(),
                (GrailsConventionGroovyPageLocator) groovyPagesTemplateEngine().getGroovyPageLocator());
        innerGspViewResolver.setAllowGrailsViewCaching(!gspTemplateEngineConfig().gspReloadingEnabled || gspTemplateEngineConfig().viewCacheTimeout != 0);
        innerGspViewResolver.setCacheTimeout(gspTemplateEngineConfig().gspReloadingEnabled ? gspTemplateEngineConfig().viewCacheTimeout : -1);
        return innerGspViewResolver;
    }


    @Bean(autowire=Autowire.BY_NAME)
    GroovyPagesTemplateRenderer groovyPagesTemplateRenderer() {
        GroovyPagesTemplateRenderer groovyPagesTemplateRenderer = new GroovyPagesTemplateRenderer();
        groovyPagesTemplateRenderer.setCacheEnabled(!gspTemplateEngineConfig().gspReloadingEnabled);
        return groovyPagesTemplateRenderer;
    }

    @Bean
    GrailsHoldersConfigurer grailsHoldersConfigurer() {
        return new GrailsHoldersConfigurer();
    }






}

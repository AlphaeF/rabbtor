package com.rabbtor.gsp.config.annotation;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.grails.gsp.io.GroovyPageLocator;
import org.grails.plugins.web.taglib.RenderTagLib;
import org.grails.plugins.web.taglib.SitemeshTagLib;
import org.grails.web.gsp.io.CachingGrailsConventionGroovyPageLocator;
import org.grails.web.gsp.io.GrailsConventionGroovyPageLocator;
import org.grails.web.servlet.view.GrailsLayoutViewResolver;
import org.grails.web.servlet.view.GroovyPageViewResolver;
import org.grails.web.sitemesh.GroovyPageLayoutFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ViewResolver;

import java.util.ArrayList;
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
                (GrailsConventionGroovyPageLocator) groovyPageLocator());
        innerGspViewResolver.setAllowGrailsViewCaching(!gspTemplateEngineConfig().gspReloadingEnabled || gspTemplateEngineConfig().viewCacheTimeout != 0);
        innerGspViewResolver.setCacheTimeout(gspTemplateEngineConfig().gspReloadingEnabled ? gspTemplateEngineConfig().viewCacheTimeout : -1);
        return innerGspViewResolver;
    }


    @Override
    protected void registerDefaultTagLibs(Set<Class<?>> tagLibClasses)
    {
        super.registerDefaultTagLibs(tagLibClasses);
        tagLibClasses.add(RenderTagLib.class);
        tagLibClasses.add(SitemeshTagLib.class);
        try
        {
            tagLibClasses.add(Class.forName("com.rabbtor.gsp.tags.ApplicationTagLib"));
            tagLibClasses.add(Class.forName("com.rabbtor.gsp.tags.FormatTagLib"));


        } catch (ClassNotFoundException e)
        {
            LOG.warn("Error registering Rabbtor's standard GSP tag libraries.Could not load tag library class.",e);
        }
    }


}

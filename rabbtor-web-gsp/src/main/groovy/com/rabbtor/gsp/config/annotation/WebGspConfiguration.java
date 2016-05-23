package com.rabbtor.gsp.config.annotation;


import org.grails.gsp.io.DefaultGroovyPageLocator;
import org.grails.gsp.io.GroovyPageLocator;
import org.grails.web.gsp.GroovyPagesTemplateRenderer;
import org.grails.web.gsp.io.CachingGrailsConventionGroovyPageLocator;
import org.grails.web.gsp.io.GrailsConventionGroovyPageLocator;
import org.grails.web.servlet.view.GrailsLayoutViewResolver;
import org.grails.web.servlet.view.GroovyPageViewResolver;
import org.grails.web.sitemesh.GroovyPageLayoutFinder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ViewResolver;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebGspConfiguration extends GspConfiguration
{

    @Bean
    @ConditionalOnMissingBean(name = "groovyPageLayoutFinder")
    public GroovyPageLayoutFinder groovyPageLayoutFinder() {
        GroovyPageLayoutFinder groovyPageLayoutFinder = new GroovyPageLayoutFinder();
        groovyPageLayoutFinder.setGspReloadEnabled(templateEngineConfig.gspReloadingEnabled);
        groovyPageLayoutFinder.setCacheEnabled(templateEngineConfig.gspLayoutCaching);
        groovyPageLayoutFinder.setEnableNonGspViews(false);
        groovyPageLayoutFinder.setDefaultDecoratorName(templateEngineConfig.defaultLayoutName);
        return groovyPageLayoutFinder;
    }

    @ConditionalOnMissingBean(name = "groovyPagesTemplateRenderer")
    GroovyPagesTemplateRenderer groovyPagesTemplateRenderer() {
        GroovyPagesTemplateRenderer groovyPagesTemplateRenderer = new GroovyPagesTemplateRenderer();
        groovyPagesTemplateRenderer.setCacheEnabled(!templateEngineConfig.gspReloadingEnabled);
        return groovyPagesTemplateRenderer;
    }

    @Bean
    @ConditionalOnMissingBean(name = "gspViewResolver")
    public GrailsLayoutViewResolver gspViewResolver() {
        return new GrailsLayoutViewResolver(innerGspViewResolver(), groovyPageLayoutFinder());
    }

    ViewResolver innerGspViewResolver() {
        GroovyPageViewResolver innerGspViewResolver = new GroovyPageViewResolver(groovyPagesTemplateEngine(),
                (GrailsConventionGroovyPageLocator) groovyPageLocator());
        innerGspViewResolver.setAllowGrailsViewCaching(!templateEngineConfig.gspReloadingEnabled || templateEngineConfig.viewCacheTimeout != 0);
        innerGspViewResolver.setCacheTimeout(templateEngineConfig.gspReloadingEnabled ? templateEngineConfig.viewCacheTimeout : -1);
        return innerGspViewResolver;
    }

    @Override
    protected GroovyPageLocator createGroovyPageLocator()
    {
        final List<String> templateRootsCleaned=resolveTemplateRoots();
        DefaultGroovyPageLocator pageLocator = new CachingGrailsConventionGroovyPageLocator() {
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
        pageLocator.setReloadEnabled(templateEngineConfig.gspReloadingEnabled);

        return pageLocator;
    }
}

package com.rabbtor.gsp.config.annotation;


import org.grails.gsp.io.DefaultGroovyPageLocator;
import org.grails.gsp.io.GroovyPageLocator;
import org.grails.web.gsp.io.CachingGrailsConventionGroovyPageLocator;
import org.grails.web.gsp.io.GrailsConventionGroovyPageLocator;
import org.grails.web.servlet.view.GrailsLayoutViewResolver;
import org.grails.web.servlet.view.GroovyPageViewResolver;
import org.grails.web.sitemesh.GroovyPageLayoutFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ViewResolver;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebGspConfiguration extends GspConfiguration
{



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

    @Configuration
    @Conditional(GspEnabledCondition.class)
    protected class GspViewResolverConfiguration {

        @Bean
        public GroovyPageLayoutFinder groovyPageLayoutFinder() {
            GroovyPageLayoutFinder groovyPageLayoutFinder = new GroovyPageLayoutFinder();
            groovyPageLayoutFinder.setGspReloadEnabled(gspTemplateEngineConfig().gspReloadingEnabled);
            groovyPageLayoutFinder.setCacheEnabled(gspTemplateEngineConfig().gspLayoutCaching);
            groovyPageLayoutFinder.setEnableNonGspViews(false);
            groovyPageLayoutFinder.setDefaultDecoratorName(gspTemplateEngineConfig().defaultLayoutName);
            return groovyPageLayoutFinder;
        }

//    GroovyPagesTemplateRenderer groovyPagesTemplateRenderer() {
//        GroovyPagesTemplateRenderer groovyPagesTemplateRenderer = new GroovyPagesTemplateRenderer();
//        groovyPagesTemplateRenderer.setCacheEnabled(!gspTemplateEngineConfig().gspReloadingEnabled);
//        return groovyPagesTemplateRenderer;
//    }

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
    }
}

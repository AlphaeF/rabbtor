package com.rabbtor.gsp.config.annotation;


import org.grails.gsp.GroovyPagesTemplateEngine;
import org.grails.gsp.io.DefaultGroovyPageLocator;
import org.grails.gsp.io.GroovyPageLocator;
import org.grails.taglib.TagLibraryLookup;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;

@Configuration
public class GspConfiguration extends GrailsApplicationConfigurationSupport
{

    private static final String LOCAL_DIRECTORY_TEMPLATE_ROOT="./src/main/resources/templates/";
    private static final String CLASSPATH_TEMPLATE_ROOT="classpath:/templates/";


    public static abstract class AbstractGspConfig {
        @Value("${spring.gsp.reloadingEnabled:true}")
        boolean gspReloadingEnabled;

        @Value("${spring.gsp.view.cacheTimeout:1000}")
        long viewCacheTimeout;

    }


    @Configuration
    public static class GspTemplateEngineConfig extends AbstractGspConfig {

        @Value("${spring.gsp.templateRoots:}")
        String[] templateRoots;

        @Value("${spring.gsp.locator.cacheTimeout:5000}")
        long locatorCacheTimeout;

        @Value("${spring.gsp.layout.caching:true}")
        boolean gspLayoutCaching;

        @Value("${spring.gsp.layout.default:#{null}")
        String defaultLayoutName;

    }

    @Autowired
    protected GspTemplateEngineConfig templateEngineConfig;





    @ConditionalOnMissingBean(name="groovyPagesTemplateEngine")
    @Bean(autowire = Autowire.BY_NAME)
    GroovyPagesTemplateEngine groovyPagesTemplateEngine() {
        GroovyPagesTemplateEngine templateEngine = new GroovyPagesTemplateEngine();
        templateEngine.setReloadEnabled(templateEngineConfig.gspReloadingEnabled);
        templateEngine.setGroovyPageLocator(groovyPageLocator());
        return templateEngine;
    }

    @ConditionalOnMissingBean(name="groovyPageLocator")
    @Bean(autowire = Autowire.BY_NAME)
    GroovyPageLocator groovyPageLocator() {
        GroovyPageLocator pageLocator = createGroovyPageLocator();
        return pageLocator;
    }


    protected GroovyPageLocator createGroovyPageLocator()
    {
        final List<String> templateRootsCleaned=resolveTemplateRoots();
        DefaultGroovyPageLocator pageLocator = new DefaultGroovyPageLocator() {
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

    protected List<String> resolveTemplateRoots() {
        List<String> templateRoots = new ArrayList();
        if (templateEngineConfig.templateRoots != null)
            templateRoots.addAll(Arrays.asList(templateEngineConfig.templateRoots));

        addTemplateRoots(templateRoots);

        if (templateRoots.size() > 0) {
            List<String> rootPaths = new ArrayList<String>(templateRoots.size());
            for (String rootPath : templateRoots) {
                rootPath = rootPath.trim();
                // remove trailing slash since uri will always be prefixed with a slash
//                if(rootPath.endsWith("/")) {
//                    rootPath = rootPath.substring(0, rootPath.length()-1);
//                }
                if(!StringUtils.isEmpty(rootPath)) {
                    rootPaths.add(rootPath);
                }
            }
            return rootPaths;
        }
        else {
            if (templateEngineConfig.gspReloadingEnabled) {
                File templateRootDirectory = new File(LOCAL_DIRECTORY_TEMPLATE_ROOT);
                if (templateRootDirectory.isDirectory()) {
                    return Collections.singletonList("file:" + LOCAL_DIRECTORY_TEMPLATE_ROOT);
                }
            }
            return Collections.singletonList(CLASSPATH_TEMPLATE_ROOT);
        }
    }

    protected void addTemplateRoots(List<String> templateRoots)
    {
    }

    @Override
    protected void registerGrailsProperties(Set<String> grailsProperties)
    {
        grailsProperties.addAll(Arrays.asList(new String[] {
                "grails.views.default.codec",
                "grails.views.gsp.codecs",
                "grails.views.gsp.encoding",
                "grails.views.gsp.keepgenerateddir",
                "grails.views.gsp.sitemesh.preprocess",
                "grails.views.gsp.codecs"
        }));
    }
}

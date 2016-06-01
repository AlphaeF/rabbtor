package com.rabbtor.gsp.config.annotation;


import com.rabbtor.gsp.config.TagLibraryRegistry;
import org.grails.gsp.GroovyPagesTemplateEngine;
import org.grails.gsp.io.DefaultGroovyPageLocator;
import org.grails.gsp.io.GroovyPageLocator;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;


public abstract class GspConfigurationSupport extends GrailsApplicationConfigurationSupport
{

    private static final String LOCAL_DIRECTORY_TEMPLATE_ROOT="./src/main/resources/templates";
    private static final String WEB_INF_TEMPLATE_ROOT="/WEB-INF/templates";
    private static final String CLASSPATH_TEMPLATE_ROOT="classpath:/templates";



    protected GspTemplateEngineConfig gspTemplateEngineConfig() {
        GspTemplateEngineConfig config = new GspTemplateEngineConfig();
        configure(config);
        return config;
    }



    protected void configure(GspTemplateEngineConfig config)
    {

    }


    @Bean(autowire = Autowire.BY_NAME)
    GroovyPagesTemplateEngine groovyPagesTemplateEngine() {
        GroovyPagesTemplateEngine templateEngine = new GroovyPagesTemplateEngine();
        templateEngine.setReloadEnabled(gspTemplateEngineConfig().gspReloadingEnabled);
        templateEngine.setGroovyPageLocator(groovyPageLocator());
        return templateEngine;
    }

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
        pageLocator.setReloadEnabled(gspTemplateEngineConfig().gspReloadingEnabled);


        return pageLocator;
    }

    protected List<String> resolveTemplateRoots() {
        List<String> templateRoots = new ArrayList();
        if (gspTemplateEngineConfig().templateRoots != null)
            templateRoots.addAll(Arrays.asList(gspTemplateEngineConfig().templateRoots));



        if (templateRoots.size() > 0) {
            List<String> rootPaths = new ArrayList<String>(templateRoots.size());
            for (String rootPath : templateRoots) {
                rootPath = rootPath.trim();
                // remove trailing slash since uri will always be prefixed with a slash
                if(rootPath.endsWith("/")) {
                    rootPath = rootPath.substring(0, rootPath.length()-1);
                }
                if(!StringUtils.isEmpty(rootPath)) {
                    rootPaths.add(rootPath);
                }
            }
            return rootPaths;
        }
        else {
            if (gspTemplateEngineConfig().gspReloadingEnabled) {
                File templateRootDirectory = new File(LOCAL_DIRECTORY_TEMPLATE_ROOT);
                if (templateRootDirectory.isDirectory()) {
                    return Collections.singletonList("file:" + LOCAL_DIRECTORY_TEMPLATE_ROOT);
                }
            }
            return Arrays.asList(new String[] { WEB_INF_TEMPLATE_ROOT, CLASSPATH_TEMPLATE_ROOT});
        }
    }




    @Bean
    TagLibraryRegistry gspTagLibraryRegistry() {
        Set<Class<?>> tagLibClasses = new HashSet<>();
        registerDefaultTagLibs(tagLibClasses);
        registerTagLibClasses(tagLibClasses);

        TagLibraryRegistry registry = new TagLibraryRegistry();
        registry.setTagLibInstances(tagLibClasses);
        return registry;
    }

    protected void registerTagLibClasses(Set<Class<?>> tagLibClasses)
    {

    }

    protected void registerDefaultTagLibs(Set<Class<?>> tagLibClasses) {

    }


    @Override
    protected void registerGrailsProperties(Set<String> grailsProperties)
    {
        grailsProperties.addAll(Arrays.asList(new String[] {
                "grails.views.default.codec",
                "grails.views.gsp.codecs",
                "grails.views.gsp.encoding",
                "grails.views.gsp.keepgenerateddir",
                "grails.views.gsp.sitemesh.preprocess"
        }));
    }
}

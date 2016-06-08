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



import org.grails.gsp.GroovyPagesTemplateEngine;
import org.grails.gsp.io.DefaultGroovyPageLocator;
import org.grails.gsp.io.GroovyPageLocator;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;


public abstract class GspConfigurationSupport extends GrailsApplicationConfigurationSupport
{

    private static final String LOCAL_DIRECTORY_TEMPLATE_ROOT="./src/main/resources/templates";
    private static final String WEB_INF_TEMPLATE_ROOT="/WEB-INF/templates";
    private static final String CLASSPATH_TEMPLATE_ROOT="classpath:/templates";

    private GspSettings gspSettings = null;



    protected GspSettings gspTemplateEngineConfig() {
        if (gspSettings == null)
        {
            gspSettings = new GspSettings();
            configureGsp(gspSettings);
        }
        return gspSettings;
    }



    protected void configureGsp(GspSettings config)
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

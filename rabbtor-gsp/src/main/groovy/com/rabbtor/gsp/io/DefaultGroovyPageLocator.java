/*
 * Copyright 2011 SpringSource
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rabbtor.gsp.io;


import com.rabbtor.gsp.GroovyPage;
import com.rabbtor.io.ResourceUtils;
import com.rabbtor.taglib.TemplateVariableBinding;
import com.rabbtor.util.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

import java.net.URL;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Used to locate GSPs whether in development or WAR deployed mode from static
 * resources, custom resource loaders and binary plugins.
 *
 * @author Graeme Rocher
 * @since 2.0
 */
public class DefaultGroovyPageLocator implements GroovyPageLocator, ResourceLoaderAware, ApplicationContextAware
{

    private static final Log LOG = LogFactory.getLog(DefaultGroovyPageLocator.class);
    public static final String PATH_TO_WEB_INF_VIEWS = "/WEB-INF/views/gsp";
    private static final String SLASHED_VIEWS_DIR_PATH = "/" + ResourceUtils.VIEWS_DIR_PATH;
    private static final String BLANK = "";
    protected Collection<ResourceLoader> resourceLoaders = new ConcurrentLinkedQueue<ResourceLoader>();
    private ConcurrentMap<String, String> precompiledGspMap;
    private Set<String> reloadedPrecompiledGspClassNames = new CopyOnWriteArraySet<String>();


    private boolean reloadEnabled;
    private boolean developmentMode;

    private String[] templateRoots = null;

    public DefaultGroovyPageLocator()
    {
        reloadEnabled = !isWarDeployed();
    }

    private boolean isWarDeployed()
    {
        URL loadedLocation = getClass().getClassLoader().getResource("application.yml");
        if(loadedLocation != null && loadedLocation.getPath().contains("/WEB-INF/classes")) {
            return true;
        }
        return false;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        addResourceLoader(resourceLoader);
    }

    public void addResourceLoader(ResourceLoader resourceLoader) {
        if (resourceLoader != null && !resourceLoaders.contains(resourceLoader)) {
            resourceLoaders.add(resourceLoader);
        }
    }

    public void setPrecompiledGspMap(Map<String, String> precompiledGspMap) {
        if (precompiledGspMap == null) {
            this.precompiledGspMap = null;
        } else {
            this.precompiledGspMap = new ConcurrentHashMap<String, String>(precompiledGspMap);
        }
    }

    public GroovyPageScriptSource findPage(final String uri) {
        GroovyPageScriptSource scriptSource = findResourceScriptSource(uri);
        return scriptSource;
    }

    protected Resource findReloadablePage(final String uri) {
        Resource resource = findResource(uri);
        return resource;
    }


    public void removePrecompiledPage(GroovyPageCompiledScriptSource scriptSource) {
        reloadedPrecompiledGspClassNames.add(scriptSource.getCompiledClass().getName());
        if (scriptSource.getURI() != null && precompiledGspMap != null) {
            precompiledGspMap.remove(scriptSource.getURI());
        }
    }

    public GroovyPageScriptSource findPageInBinding(String uri, TemplateVariableBinding binding) {
         GroovyPageScriptSource scriptSource = findResourceScriptSource(uri);
        return scriptSource;
    }



    protected GroovyPageCompiledScriptSource createGroovyPageCompiledScriptSource(final String uri, String fullPath, Class<?> viewClass) {
        GroovyPageCompiledScriptSource scriptSource = new GroovyPageCompiledScriptSource(uri, fullPath,viewClass);
        if (reloadEnabled) {
            scriptSource.setResourceCallable(new PrivilegedAction<Resource>() {
                public Resource run() {
                    return findReloadablePage(uri);
                }
            });
        }
        return scriptSource;
    }



    protected String removeViewLocationPrefixes(String uri) {
        uri = removePrefix(uri, "/WEB-INF");
        uri = removePrefix(uri, SLASHED_VIEWS_DIR_PATH);
        return uri;
    }

    protected String removePrefix(String uri, String prefix) {
        if (uri.startsWith(prefix)) {
            uri = uri.substring(prefix.length());
        }
        return uri;
    }

    protected GroovyPageScriptSource findResourceScriptSource(final String uri) {
        List<String> searchPaths = resolveSearchPaths(uri);

        return findResourceScriptPathForSearchPaths(uri, searchPaths);
    }

    protected List<String> resolveSearchPaths(String uri) {
        List<String> searchPaths = null;
        if (this.templateRoots != null)
        {
            searchPaths = new ArrayList<>();
            for (String loaderPath : templateRoots) {
                searchPaths.add(ResourceUtils.appendPiecesForUri(loaderPath,uri));
            }
        }

        else {
            searchPaths = CollectionUtils.newList(
                    ResourceUtils.appendPiecesForUri(PATH_TO_WEB_INF_VIEWS, uri),
                    uri);
        }
        return searchPaths;
    }

    @SuppressWarnings("unchecked")
    protected GroovyPageScriptSource findResourceScriptPathForSearchPaths(String uri, List<String> searchPaths) {
        if (isPrecompiledAvailable()) {
            for (String searchPath : searchPaths) {
                String gspClassName = precompiledGspMap.get(searchPath);
                if (gspClassName != null && !reloadedPrecompiledGspClassNames.contains(gspClassName)) {
                    Class<GroovyPage> gspClass = null;
                    try {
                        gspClass = (Class<GroovyPage>) Class.forName(gspClassName, true, Thread.currentThread().getContextClassLoader());
                    }
                    catch (ClassNotFoundException e) {
                        LOG.warn("Cannot load class " + gspClassName + ". Resuming on non-precompiled implementation.", e);
                    }
                    if (gspClass != null) {
                        return createGroovyPageCompiledScriptSource(uri, searchPath, gspClass);
                    }
                }
            }
        }

        Resource foundResource = findResource(searchPaths);
        return foundResource == null ? null : new GroovyPageResourceScriptSource(uri,foundResource);
    }

    protected Resource findResource(String uri) {
        return findResource(resolveSearchPaths(uri));
    }

    protected Resource findResource(List<String> searchPaths) {
        Resource foundResource = null;
        Resource resource;
        for (ResourceLoader loader : resourceLoaders) {
            for (String path : searchPaths) {
                resource = loader.getResource(path);
                if (resource != null && resource.exists()) {
                    foundResource = resource;
                    break;
                }
            }
            if (foundResource != null) break;
        }
        return foundResource;
    }

    private boolean isPrecompiledAvailable() {
        return precompiledGspMap != null && precompiledGspMap.size() > 0 && isDevelopmentMode();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        addResourceLoader(applicationContext);
    }

    public String[] getTemplateRoots()
    {
        return templateRoots;
    }

    public void setTemplateRoots(String[] templateRoots)
    {
        this.templateRoots = templateRoots;
    }

    public boolean isReloadEnabled()
    {
        return reloadEnabled;
    }

    public void setReloadEnabled(boolean reloadEnabled)
    {
        this.reloadEnabled = reloadEnabled;
    }

    public boolean isDevelopmentMode()
    {
        return developmentMode;
    }

    public void setDevelopmentMode(boolean developmentMode)
    {
        this.developmentMode = developmentMode;
    }
}

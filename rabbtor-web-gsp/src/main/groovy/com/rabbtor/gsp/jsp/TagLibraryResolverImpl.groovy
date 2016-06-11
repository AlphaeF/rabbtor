/*
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
package com.rabbtor.gsp.jsp

import grails.core.GrailsApplication
import grails.core.support.GrailsApplicationAware
import org.grails.gsp.jsp.*
import org.springframework.beans.factory.BeanClassLoaderAware
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ResourceLoaderAware
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.web.context.ServletContextAware
import org.springframework.web.context.support.ServletContextResource

import javax.servlet.ServletContext
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

public class TagLibraryResolverImpl implements ServletContextAware, GrailsApplicationAware, TagLibraryResolver, ResourceLoaderAware, BeanClassLoaderAware
{
    protected Map<String, JspTagLib> tagLibs = new ConcurrentHashMap<String, JspTagLib>()
    GrailsApplication grailsApplication
    ServletContext servletContext
    ClassLoader classLoader
    ResourceLoader resourceLoader
    String[] tldScanPatterns = [] as String[];
    volatile boolean initialized = false

    /**
     * Resolves a JspTagLib instance for the given URI
     */
    JspTagLib resolveTagLibrary(String uri) {
        if(!initialized) {
            initialize()
        }
        return tagLibs[uri]
    }

    public synchronized void initialize() {
        if(servletContext) {
            Resource webXml = getWebXmlFromServletContext()
            if (webXml?.exists()) {
                loadTagLibLocations(webXml)
            }
        }
        if(resourceLoader && tldScanPatterns) {
            PathMatchingResourcePatternResolver patternResolver=new PathMatchingResourcePatternResolver(resourceLoader)
            for(String tldResourcePattern : tldScanPatterns) {
                patternResolver.getResources(tldResourcePattern).each { Resource resource ->
                    JspTagLib jspTagLib = loadJspTagLib(resource.getInputStream())
                    if(jspTagLib) {
                        tagLibs[jspTagLib.URI] = jspTagLib
                    }
                }
            }
        }
        initialized = true
    }

    private loadTagLibLocations(Resource webXml) {
        if (!webXml) {
            return
        }
        WebXmlTagLibraryReader webXmlReader = new WebXmlTagLibraryReader(webXml.getInputStream())
        webXmlReader.getTagLocations().each { String uri, String location ->
                JspTagLib jspTagLib
            if (location.startsWith("jar:")) {
                jspTagLib = loadFromJar(uri, location)
            }
            else {
                jspTagLib = loadJspTagLib(getTldFromServletContext(location), uri)
            }
            if(jspTagLib) {
                tagLibs[uri] = jspTagLib
            }
        }
    }

    private JspTagLib loadFromJar(String uri, String loc) {
        JspTagLib jspTagLib = null
        List<URL> jarURLs = resolveJarUrls()
        def fileLoc = loc[4..loc.indexOf('!')-1]
        String pathWithinZip = loc[loc.indexOf('!')+1..-1]
        URL jarFile = jarURLs.find { URL url -> url.toExternalForm() == fileLoc}
        if (jarFile) {
            jarFile.openStream().withStream { InputStream jarFileInputStream ->
                ZipInputStream zipInput = new ZipInputStream(jarFileInputStream)
                ZipEntry entry = zipInput.getNextEntry()
                while (entry) {
                    if (entry.name == pathWithinZip) {
                        jspTagLib = loadJspTagLib(zipInput, uri)
                        break
                    }
                    entry = zipInput.getNextEntry()
                }
            }
        }
        return jspTagLib
    }

    private List resolveJarUrls() {
        List<URL> jarURLs = grailsApplication.isWarDeployed() ? getJarsFromServletContext() : resolveRootLoader()?.getURLs() as List
        return jarURLs
    }

    protected InputStream getTldFromServletContext(String loc) {
        servletContext.getResourceAsStream(loc)
    }

    protected Resource getWebXmlFromServletContext() {
        return new ServletContextResource(servletContext, "/WEB-INF/web.xml")
    }

    protected List<URL> getJarsFromServletContext() {
        def files = servletContext.getResourcePaths("/WEB-INF/lib")
        files = files.findAll { String path ->  path.endsWith(".jar") || path.endsWith(".zip")}
        files.collect { String path -> servletContext.getResource(path) } as List
    }

    /**
     * Obtains a reference to the first parent classloader that is a URLClassLoader and contains some URLs
     *
     */
    protected URLClassLoader resolveRootLoader() {
        def classLoader = getClass().classLoader
        while(classLoader != null) {
            if(classLoader instanceof URLClassLoader && ((URLClassLoader)classLoader).getURLs()) {
                return (URLClassLoader)classLoader
            }
            classLoader = classLoader.parent
        }
        return null
    }

    private JspTagLib loadJspTagLib(InputStream inputStream, String specifiedUri = null) {
        TldReader tldReader = new TldReader(inputStream)
        String uri = specifiedUri?:tldReader.uri
        if(tldReader.tags) {
            return new JspTagLibImpl(uri, tldReader.tags, classLoader)
        } else {
            return null
        }
    }


    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader
    }

    String[] getTldScanPatterns()
    {
        return tldScanPatterns
    }

    void setTldScanPatterns(String[] tldScanPatterns)
    {
        this.tldScanPatterns = tldScanPatterns
    }
}

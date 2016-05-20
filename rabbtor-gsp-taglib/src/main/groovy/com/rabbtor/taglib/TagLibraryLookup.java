/*
 * Copyright 2004-2005 Graeme Rocher
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
package com.rabbtor.taglib;

import com.rabbtor.taglib.encoder.WithCodecHelper;
import com.rabbtor.util.MetaClassUtils;
import groovy.lang.GroovyObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

import java.util.*;

/**
 * Looks up tag library instances.
 *
 * @author Graeme Rocher
 * @since 1.1
 */
public class TagLibraryLookup implements ApplicationContextAware, InitializingBean
{
    protected ApplicationContext applicationContext;
    protected Map<String, Map<String, Object>> tagNamespaces = new HashMap<String, Map<String, Object>>();
    protected Map<String, NamespacedTagDispatcher> namespaceDispatchers = new HashMap<String, NamespacedTagDispatcher>();
    protected Map<String, Set<String>> tagsThatReturnObjectForNamespace = new HashMap<String, Set<String>>();
    protected Map<String, Map<String, Map<String, Object>>> encodeAsForTagNamespaces = new HashMap<String, Map<String, Map<String, Object>>>();
    private Environment environment;

    private static Log LOG = LogFactory.getLog(TagLibraryLookup.class);

    public void afterPropertiesSet() throws Exception
    {
        if (applicationContext == null)
        {
            return;
        }

        environment = applicationContext.getEnvironment();

        scanTagLibraries();
        registerTagLibraries();
        registerTemplateNamespace();

        registerNamespaceDispatchers();
    }

    private void scanTagLibraries()
    {
        applicationContext.getBeansOfType(TagLibClass.class)
                .values().stream().forEach(tagLib -> registerTagLib(tagLib,true));
    }

    private void registerNamespaceDispatchers()
    {
        for (String namespace : tagNamespaces.keySet())
        {
            registerNamespaceDispatcher(namespace);
        }
    }

    protected void registerNamespaceDispatcher(String namespace)
    {
        namespaceDispatchers.put(namespace, new NamespacedTagDispatcher(namespace, null, this, environment.acceptsProfiles("development")));
    }

    protected void registerTagLibraries()
    {

    }

    protected void registerTemplateNamespace()
    {
        namespaceDispatchers.put(TemplateNamespacedTagDispatcher.TEMPLATE_NAMESPACE, new TemplateNamespacedTagDispatcher(null, this, environment.acceptsProfiles("development")));
    }

    /**
     * Registers a tag library for lookup. Each of the tags in the
     * library is mapped by namespace:name to the taglib bean. If the
     * taglib has already been registered, this method will override
     * the existing information and update the tags to use the new
     * version.
     *
     * @param taglib The taglib descriptor class.
     */
    public void registerTagLib(TagLibClass taglib)
    {
        registerTagLib(taglib, false);
    }

    private void registerTagLib(TagLibClass taglib, boolean isInitialization)
    {
        //TagLibraryMetaUtils.enhanceTagLibMetaClass(taglib,this);
        String namespace = taglib.getNamespace();

        if (!isInitialization)
        {
            registerNamespaceDispatcher(namespace);
        }
        Set<String> tagsThatReturnObject = tagsThatReturnObjectForNamespace.get(namespace);
        if (tagsThatReturnObject == null)
        {
            tagsThatReturnObject = new HashSet<String>();
            tagsThatReturnObjectForNamespace.put(namespace, tagsThatReturnObject);
        }
        Map<String, Object> tags = tagNamespaces.get(namespace);
        if (tags == null)
        {
            tags = new HashMap<String, Object>();
            tagNamespaces.put(namespace, tags);
        }

        for (String tagName : taglib.getTagNames())
        {
            putTagLib(tags, tagName, taglib);
            tagsThatReturnObject.remove(tagName);
        }
        for (String tagName : taglib.getTagNamesThatReturnObject())
        {
            tagsThatReturnObject.add(tagName);
        }

        Map<String, Map<String, Object>> encodeAsForTagNamespace = encodeAsForTagNamespaces.get(namespace);
        if (encodeAsForTagNamespace == null)
        {
            encodeAsForTagNamespace = new HashMap<String, Map<String, Object>>();
            encodeAsForTagNamespaces.put(namespace, encodeAsForTagNamespace);
        }

        Map<String, Object> defaultEncodeAsForTagLib = null;
        if (taglib.getDefaultEncodeAs() != null)
        {
            defaultEncodeAsForTagLib = Collections.unmodifiableMap(WithCodecHelper.makeSettingsCanonical(taglib.getDefaultEncodeAs()));
        }

        for (String tagName : taglib.getTagNames())
        {
            Object codecInfo = taglib.getEncodeAsForTag(tagName);
            Map<String, Object> codecInfoMap = WithCodecHelper.mergeSettingsAndMakeCanonical(codecInfo, defaultEncodeAsForTagLib);
            if (codecInfoMap != null)
            {
                encodeAsForTagNamespace.put(tagName, codecInfoMap);
            }
        }
    }

    protected void putTagLib(Map<String, Object> tags, String name, TagLibClass taglib)
    {
        tags.put(name, applicationContext.getBean(taglib.getBeanName()));
    }

    /**
     * Looks up a tag library for the given namespace and tag name.
     *
     * @param namespace The tag library namespace
     * @param tagName   The tag name
     * @return The tag library or null if it wasn't found
     */
    public GroovyObject lookupTagLibrary(String namespace, String tagName)
    {
        Map<String, Object> tags = tagNamespaces.get(namespace);
        if (tags == null)
        {
            return null;
        }
        return (GroovyObject) tags.get(tagName);
    }

    public boolean doesTagReturnObject(String namespace, String tagName)
    {
        Set<String> tagsThatReturnObject = tagsThatReturnObjectForNamespace.get(namespace);
        return tagsThatReturnObject != null && tagsThatReturnObject.contains(tagName);
    }

    public Map<String, Object> getEncodeAsForTag(String namespace, String tagName)
    {
        Map<String, Map<String, Object>> encodeAsForTagNamespace = encodeAsForTagNamespaces.get(namespace);
        return encodeAsForTagNamespace != null ? encodeAsForTagNamespace.get(tagName) : null;
    }

    /**
     * Looks up a namespace dispatcher for the given namespace
     *
     * @param namespace The namespace
     * @return The NamespacedTagDispatcher
     */
    public NamespacedTagDispatcher lookupNamespaceDispatcher(String namespace)
    {
        return namespaceDispatchers.get(namespace);
    }

    /**
     * Returns whether the given namespace is in use
     *
     * @param namespace The namespace
     * @return true if it is in use
     */
    public boolean hasNamespace(String namespace)
    {
        return namespaceDispatchers.containsKey(namespace);
    }

    /**
     * @return The namespaces available
     */
    public Set<String> getAvailableNamespaces()
    {
        return namespaceDispatchers.keySet();
    }

    public Set<String> getAvailableTags(String namespace)
    {
        Map<String, Object> tags = tagNamespaces.get(namespace);
        if (tags == null)
        {
            return Collections.emptySet();
        }
        return tags.keySet();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

}

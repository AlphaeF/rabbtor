/*
 * Copyright 2014 original authors
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
package com.rabbtor.web.servlet.gsp.tags

import com.rabbtor.gsp.DefaultGspEnvironment
import com.rabbtor.gsp.GroovyPagesTemplateEngine
import com.rabbtor.gsp.GspConfiguration
import com.rabbtor.gsp.GspEnvironment
import com.rabbtor.taglib.NamespacedTagDispatcher
import com.rabbtor.taglib.TagLibraryLookup
import com.rabbtor.taglib.TagLibraryMetaUtils
import com.rabbtor.taglib.TagOutput
import com.rabbtor.taglib.encoder.WithCodecHelper
import com.rabbtor.util.BeanFactoryUtils
import com.rabbtor.util.MetaClassUtils
import com.rabbtor.web.servlet.mvc.RabbtorWebRequest
import com.rabbtor.web.servlet.support.WebAttributes
import com.rabbtor.web.util.WebUtils
import groovy.transform.CompileStatic

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

/**
 * A trait that adds the ability invoke tags to any class
 *
 * @author Graeme Rocher
 * @since 3.0
 */
@CompileStatic
trait TagLibraryInvoker extends WebAttributes{


    private GspEnvironment gspEnvironment

    private TagLibraryLookup tagLibraryLookup

    @Autowired
    void setGspEnvironment(GspEnvironment gspEnvironment) {
        this.gspEnvironment = gspEnvironment
    }


    @Autowired
    void setTagLibraryLookup(TagLibraryLookup tagLibraryLookup) {
        this.tagLibraryLookup = tagLibraryLookup
    }

    TagLibraryLookup getTagLibraryLookup() {
        def lookup = this.tagLibraryLookup
        if(lookup == null) {
            lookup = getApplicationContext().getBean(TagLibraryLookup)
            setTagLibraryLookup(lookup)
        }
        return lookup
    }

    String getTaglibNamespace() {
        TagOutput.DEFAULT_NAMESPACE
    }


    /**
     * Method missing implementation that handles tag invocation by method name
     *
     * @param instance The instance
     * @param methodName The method name
     * @param argsObject The arguments
     * @return The result
     */
    Object methodMissing(String methodName, Object argsObject) {
        Object[] args = argsObject instanceof Object[] ? (Object[])argsObject : [argsObject] as Object[]
        if (shouldHandleMethodMissing(methodName, args)) {
            TagLibraryLookup lookup = tagLibraryLookup
            if (lookup) {
                def usedNamespace = getTaglibNamespace()
                GroovyObject tagLibrary = lookup.lookupTagLibrary(usedNamespace, methodName)
                if (tagLibrary == null) {
                    tagLibrary = lookup.lookupTagLibrary(TagOutput.DEFAULT_NAMESPACE, methodName);
                    usedNamespace = TagOutput.DEFAULT_NAMESPACE;
                }

                if (tagLibrary) {
                    if (!developmentMode) {
                        MetaClass thisMc = MetaClassUtils.getMetaClass(this)
                        TagLibraryMetaUtils.registerMethodMissingForTags(thisMc, lookup, usedNamespace, methodName)
                    }
                    return tagLibrary.invokeMethod(methodName, args)
                }
            }
        }
        throw new MissingMethodException(methodName, this.getClass(), args)
    }

    private boolean shouldHandleMethodMissing(String methodName, Object[] args) {
        if("render".equals(methodName)) {
            MetaClass thisMc = MetaClassUtils.getMetaClass(this)
            boolean containsExistingRenderMethod = thisMc.getMethods().any { MetaMethod mm ->
                mm.name == 'render'
            }
            // don't add any new metamethod if an existing render method exists, see GRAILS-11581
            return !containsExistingRenderMethod
        } else {
            return true
        }
    }

    /**
     * Looks up namespaces on missing property
     *
     * @param instance The instance
     * @param propertyName The property name
     * @return The namespace or a MissingPropertyException
     */
     Object propertyMissing(String propertyName) {
        TagLibraryLookup lookup = tagLibraryLookup
        NamespacedTagDispatcher namespacedTagDispatcher = lookup.lookupNamespaceDispatcher(propertyName)
        if (namespacedTagDispatcher) {
            if (!developmentMode) {
                TagLibraryMetaUtils.registerPropertyMissingForTag(MetaClassUtils.getMetaClass(this),propertyName, namespacedTagDispatcher)
            }
            return namespacedTagDispatcher
        }

        throw new MissingPropertyException(propertyName, this.getClass())
    }

    /**
     * @see {@link WithCodecHelper#withCodec(org.springframework.context.ApplicationContext, java.lang.Object, groovy.lang.Closure)}
     */
    def <T> T withCodec(Object codecInfo, Closure<T> body) {
        return WithCodecHelper.withCodec(getApplicationContext(), codecInfo, body)
    }

    RabbtorWebRequest getWebRequest() {
        RabbtorWebRequest webRequest = RabbtorWebRequest.lookup()
        if (webRequest == null)
        {
            def request = currentRequestAttributes().getRequest()
            def response = currentRequestAttributes().getResponse()
            webRequest = new RabbtorWebRequest(request,response,request.getServletContext(),getApplicationContext())
            WebUtils.storeRabbtorWebRequest(webRequest, false)
        }

        webRequest
    }

    public boolean isDevelopmentMode() {
        return gspEnvironment?.developmentMode
    }





}
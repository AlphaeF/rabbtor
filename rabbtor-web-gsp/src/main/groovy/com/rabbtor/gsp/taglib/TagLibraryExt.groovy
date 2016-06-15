/*
 * Copyright 2002-2014 the original author or authors.
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

/* Modifications copyright (C) 2016 - Rabbytes,Inc */

package com.rabbtor.gsp.taglib

import com.rabbtor.gsp.util.GspTagUtils
import grails.artefact.TagLibrary
import groovy.transform.CompileStatic
import org.grails.buffer.GrailsPrintWriter
import org.grails.encoder.CodecLookup
import org.grails.taglib.GroovyPageAttributes
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.ConversionService
import org.springframework.util.ObjectUtils
import org.springframework.validation.DataBinder
import org.springframework.web.bind.support.WebRequestDataBinder
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
import org.springframework.web.servlet.support.RequestContext
import org.springframework.web.util.HtmlUtils
import org.springframework.web.util.JavaScriptUtils

/**
 * GSP tag library extension trait which provides addional utilities to tag library implementors.
 * Unlike the {@link TagLibrary} trait, this trait is not automatically injected during the compile phase
 * so tag libraries that want to use it must extend this trait manually.
 *
 * Part of the implementation is exact copy from Spring Framework JSP tag implemenations. (https://spring.io)
 *
 * @see com.rabbtor.gsp.tags.ApplicationTagLib
 * @see com.rabbtor.gsp.tags.FormTagLib
 */
@CompileStatic
trait TagLibraryExt extends TagLibrary
{

    private CodecLookup codecLookup

    /**
     * Optionally autowired by the application context
     * @param codecLookup
     */
    @Autowired(required = false)
    void setCodecLookup(CodecLookup codecLookup)
    {
        this.codecLookup = codecLookup
    }

    /**
     * Returns the injected codec lookup object which is used for encoding data ( i.e. html escaping )
     */
    CodecLookup getCodecLookup()
    {
        return this.@codecLookup
    }

    RequestContext getRequestContext()
    {
        GspTagUtils.ensureRequestContext(request,response,servletContext)
    }

    /**
     * Checks if 'htmlEscape' attribute is enabled within the given attributes.
     * If not, default value of the RequestContext is used.
     *
     * @param attrs current tag attributes
     * @return whether html escaping should be enabled for the calling tag
     *
     * @see #isDefaultHtmlEscape()
     */
    boolean isHtmlEscape(Map attrs)
    {
        if (!attrs || !attrs.containsKey(GspTagUtils.HTML_ESCAPE_ATTR_NAME))
            return isDefaultHtmlEscape()
        return getHtmlBooleanAttributeValue(attrs, GspTagUtils.HTML_ESCAPE_ATTR_NAME)
    }

    /**
     * Return the applicable default HTML escape setting for this tag.
     * <p>The default implementation checks the RequestContext's setting,
     * falling back to <code>false</code> in case of no explicit default given.
     * @see #getRequestContext()
     */
    boolean isDefaultHtmlEscape()
    {
        return getRequestContext().isDefaultHtmlEscape();
    }

    /**
     * Return the applicable default for the use of response encoding with
     * HTML escaping for this tag.
     * <p>The default implementation checks the RequestContext's setting,
     * falling back to <code>false</code> in case of no explicit default given.
     * @since 4.1.2
     * @see #getRequestContext()
     */
    boolean isResponseEncodedHtmlEscape()
    {
        return getRequestContext().isResponseEncodedHtmlEscape();
    }

    /**
     * HTML-encodes the given String, only if the "htmlEscape" setting is enabled
     * in the given tag attributes.
     * <p>If not, then requestContext default value will be used</p>
     * <p>The response encoding will be taken into account if the
     * "responseEncodedHtmlEscape" setting is enabled as well.
     * @param content the String to escape
     * @return the escaped String
     * @see #isHtmlEscape(java.util.Map)
     * @see #isResponseEncodedHtmlEscape()
     */
    String htmlEscape(Map attrs, String content)
    {
        String out = content;
        if (attrs == null || isHtmlEscape(attrs))
        {
            if (isResponseEncodedHtmlEscape())
            {
                out = HtmlUtils.htmlEscape(content, response.characterEncoding);
            } else
            {
                out = HtmlUtils.htmlEscape(content);
            }
        }
        return out;
    }

    /**
     * HTML encodes the given String
     * @param content
     * @return
     */
    String htmlEscape(String content)
    {
        return htmlEscape(null, content)
    }

    /**
     * Render GSP tag attributes
     * @param attrs attribute map
     * @param writer response writer
     * @param suppressAttrs list of attributes which are for internal use and must not be rendered
     */
    @CompileStatic
    void outputAttributes(Map attrs, GrailsPrintWriter writer, Iterable<String> suppressAttrs)
    {
        def suppress = suppressAttrs?.collectEntries { [(it): true] } ?: [:]
        suppress.tagName = true
        suppress.htmlEscape = true

        ['disabled', 'readonly', 'checked'].each {
            fixHtmlBooleanAttributeValue(attrs, it)
        }

        attrs.findAll { !suppress.containsKey(it.key) }.each { k, v ->
            writer << ' ' + k
            if (v != null)
            {
                writer << '="'
                writer << ObjectUtils.getDisplayString(v)
                writer << '"'
            }
        }
    }

    /**
     * For attributes like 'disabled','readonly','checked', check if the attribute value is <code>true</code>
     * and render or remove if the value is <code>false</code>
     * @param attrs
     * @param attributeName
     */
    void fixHtmlBooleanAttributeValue(Map attrs, String attributeName)
    {
        if (getHtmlBooleanAttributeValue(attrs, attributeName))
            attrs[attributeName] = attributeName
        else
            attrs.remove(attributeName)
    }

    /**
     * For attributes like 'disabled','readonly','checked', resolve boolean value from the supplied attribute value.
     * i.e. <code><g:input disabled='' /></code> or <code><g:input disabled='disabled' /></code> or <code><g:input disabled='true' /></code>
     * will all result in <code>true</code>
     *
     * @param attrs attributes of the calling tag
     * @param attributeName 'disabled','readonly', etc.
     */
    boolean getHtmlBooleanAttributeValue(Map attrs, String attributeName)
    {
        if (attrs.containsKey(attributeName))
        {
            Object value = attrs.get(attributeName)
            boolean boolValue = true
            if (value instanceof Boolean)
                boolValue = ((Boolean) value).booleanValue()
            else
            {
                String valueStr = value?.toString()
                if (!valueStr)
                    boolValue = true
                else
                    boolValue = attributeName.equalsIgnoreCase(valueStr) || Boolean.valueOf(valueStr).booleanValue()
            }

            return boolValue
        }
        return false
    }

    /**
     * Generates incremental id's for the given attribute name
     * Counter is stored with the given name in page scope.
     * Mainly used for html checkbox and radio element id generation since they may have the same name
     * @param name attribute name on which a counter will be incremented
     * @return auto generated elment id unique for the given name within the current page scope
     */
    String nextElementId(String name)
    {
        String attributeName = TagLibraryExt.name + '.' + name;
        Integer currentCount = (Integer) pageScope[attributeName]
        currentCount = (currentCount != null ? currentCount + 1 : 1);
        pageScope[attributeName] = currentCount
        return (name + currentCount);
    }


    GroovyPageAttributes asGroovyAttrs(Map attrs)
    {
        attrs instanceof GroovyPageAttributes ? (GroovyPageAttributes) attrs : new GroovyPageAttributes(attrs)
    }

    boolean isJavascriptEscape(Map attrs)
    {
        if (!attrs)
            return false
        attrs.containsKey('javaScriptEscape') ?
                getHtmlBooleanAttributeValue(attrs, 'javaScriptEscape') : getHtmlBooleanAttributeValue(attrs, 'javascriptEscape')
    }

    String javascriptEscape(String text)
    {
        return javascriptEscape(null, text)
    }

    String javascriptEscape(Map attrs, String text)
    {
        if (!text)
            return text;

        if (attrs == null || isJavascriptEscape(attrs))
            JavaScriptUtils.javaScriptEscape(text)
        else
            text
    }

    DataBinder createBinder(Object target, String objectName)
    {
        WebRequestDataBinder binder = new WebRequestDataBinder(target,objectName)
        NativeWebRequest webRequest = GrailsWebRequest.lookup(request)
        if (!webRequest)
            webRequest = GrailsWebRequest.lookup()
        if (!webRequest)
        {
            def attrs = RequestContextHolder.currentRequestAttributes()
            if (attrs instanceof NativeWebRequest)
                webRequest = (NativeWebRequest) attrs
        }

        if (webRequest && applicationContext)
        {
            def adapter = applicationContext.getBeansOfType(RequestMappingHandlerAdapter).values()[0]
            if (adapter)
            {
                adapter.webBindingInitializer?.initBinder(binder,webRequest)
            }
        }

        if (binder.conversionService == null && applicationContext) {
            def conversionService = applicationContext.getBeansOfType(ConversionService).values()[0]
            if (conversionService)
                binder.conversionService = conversionService
        }

        binder
    }

    ConversionService getConversionService() {
        if (applicationContext)
        {

        }
        return applicationContext?.getBeansOfType(ConversionService).values()[0]

    }

    String escapeImpl(Map attrs,String tag, Closure body)
    {

        def type = attrs.remove('codec') ?: 'HTML'
        def types = []
        if (type instanceof String ) {

            types = ((String)type).split(',').collect { String str-> str.trim() }
        } else {
            types = type instanceof String[] ? ((String[]) type).toList()
                    : ((Collection<String>) type)
        }

        if (body == null && !attrs.containsKey('value'))
            throwTagError("[value] attribute is required for <g:${tag}>")

        String outVal
        if (attrs.containsKey('value'))
            outVal = ObjectUtils.getDisplayString(attrs.value)
        else
            outVal = (String)body()


        types.each { it ->
            String typeString = it.toString()
            if (['Raw','None'].any { raw -> raw.equalsIgnoreCase(typeString)})
                return
            else if ('HTML'.equalsIgnoreCase(typeString))
            {
                outVal = htmlEscape(outVal)
            }
            else if ('JavaScript'.equalsIgnoreCase(typeString) || 'Js'.equalsIgnoreCase(typeString))
                outVal = JavaScriptUtils.javaScriptEscape(outVal)
            else
            {
                def encoder = codecLookup?.lookupEncoder(typeString)
                if (encoder)
                    outVal = encoder.encode(outVal).toString()
                else
                    throwTagError("No suitable encoder found for type attribute [${type}] of ${tag}")
            }
        }


        if (!attrs.containsKey('value')) {
            out << outVal
        }

        outVal
    }
}
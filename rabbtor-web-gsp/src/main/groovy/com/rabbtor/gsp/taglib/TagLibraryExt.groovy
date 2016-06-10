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

import grails.artefact.TagLibrary
import groovy.transform.CompileStatic
import org.grails.buffer.GrailsPrintWriter
import org.grails.encoder.CodecLookup
import org.grails.encoder.Encoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.ObjectUtils
import org.springframework.web.servlet.support.RequestContext
import org.springframework.web.util.HtmlUtils

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

    /**
     *  attribute for the
     * page-level {@link org.springframework.web.servlet.support.RequestContext} instance.
     */
    public static final String REQUEST_CONTEXT_PAGE_ATTRIBUTE =
            "org.springframework.web.servlet.tags.REQUEST_CONTEXT";


    RequestContext getRequestContext()
    {
        def result = (RequestContext) request.getAttribute(REQUEST_CONTEXT_PAGE_ATTRIBUTE)
        if (result == null)
        {
            result = new RequestContext(request, response, servletContext, null)
            request.setAttribute(REQUEST_CONTEXT_PAGE_ATTRIBUTE, result)
        }
        return result
    }

    boolean isHtmlEscape(Map attrs)
    {
        Boolean htmlEscape = attrs?.get('htmlEscape')
        if (htmlEscape == null)
            htmlEscape = isDefaultHtmlEscape()

        htmlEscape
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
     * HTML-encodes the given String, only if the "htmlEscape" setting is enabled.
     * <p>The response encoding will be taken into account if the
     * "responseEncodedHtmlEscape" setting is enabled as well.
     * @param content the String to escape
     * @return the escaped String
     * @since 4.1.2
     * @see #isHtmlEscape()
     * @see #isResponseEncodedHtmlEscape()
     */
    String htmlEscape(Map attrs,String content)
    {
        String out = content;
        if (isHtmlEscape(attrs))
        {
            if (isResponseEncodedHtmlEscape())
            {
                out = HtmlUtils.htmlEscape(content, response.characterEncoding);
            } else
            {
                Encoder htmlEncoder = codecLookup?.lookupEncoder('HTML')
                if (htmlEncoder)
                    out = htmlEncoder.encode(content)
                else
                    out = HtmlUtils.htmlEscape(content);
            }
        }
        return out;
    }

    /**
     * Render GSP tag attributes
     * @param attrs attribute map
     * @param writer  response writer
     * @param suppressAttrs list of attributes which are for internal use and must not be rendered
     */
    @CompileStatic
    void outputAttributes(Map attrs, GrailsPrintWriter writer, List<String> suppressAttrs)
    {
        def suppress = suppressAttrs?.collectEntries {[(it):true] } ?: [:]
        suppress.tagName = true
        suppress.htmlEscape = true

        ['disabled','readonly','checked'].each {
            fixHtmlBooleanAttributeValue(attrs,it)
        }

        attrs.findAll { !suppress.containsKey(it.key)}.each { k, v ->
            writer << ' ' + k
            if ( v != null )
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
    void fixHtmlBooleanAttributeValue(Map attrs,String attributeName)
    {
        if (getHtmlBooleanAttributeValue(attrs,attributeName))
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
    boolean getHtmlBooleanAttributeValue(Map attrs,String attributeName) {
        if (attrs.containsKey(attributeName)) {
            Object value = attrs.get(attributeName) ?: true
            boolean boolValue = false
            if (value instanceof Boolean)
                boolValue = ((Boolean)value).booleanValue()
            else {
                String valueStr = value.toString()
                boolValue = attributeName.equalsIgnoreCase(valueStr) || Boolean.valueOf(valueStr).booleanValue()
            }

            return boolValue
        }
        return false
    }


}
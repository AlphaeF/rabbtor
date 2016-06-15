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

package com.rabbtor.gsp.util

import com.rabbtor.web.servlet.util.BindStatusUtils
import com.rabbtor.web.servlet.util.RequestContextUtils
import groovy.transform.CompileStatic
import org.grails.taglib.TemplateVariableBinding
import org.springframework.beans.PropertyAccessor
import org.springframework.web.servlet.support.BindStatus
import org.springframework.web.servlet.support.RequestContext

import javax.servlet.ServletContext
import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@CompileStatic
public class GspTagUtils
{
    public static String HTML_ESCAPE_ATTR_NAME = 'htmlEscape';
    public static final String NESTED_PATH_VARIABLE_NAME = "nestedPath";
    /**
     *  attribute for the
     * page-level {@link org.springframework.web.servlet.support.RequestContext} instance.
     */
    public static final String REQUEST_CONTEXT_PAGE_ATTRIBUTE =
            "org.springframework.web.servlet.tags.REQUEST_CONTEXT";

    /**
     * bind status variable name used by the <g:bind> tag
     */
    public static final String STATUS_VARIABLE_NAME = "status";


    static String beginNestedPath(String path, HttpServletRequest request)
    {
        String previousNestedPath =
                (String) request.getAttribute(GspTagUtils.NESTED_PATH_VARIABLE_NAME);
        String nestedPath =
                (previousNestedPath != null ? previousNestedPath + path : path);
        request.setAttribute(GspTagUtils.NESTED_PATH_VARIABLE_NAME, nestedPath);
        return previousNestedPath
    }

    static void endNestedPath(String previousNestedPath, HttpServletRequest request)
    {

        if (previousNestedPath != null)
        {
            // Expose previous nestedPath value.
            request.setAttribute(GspTagUtils.NESTED_PATH_VARIABLE_NAME, previousNestedPath);
        } else
        {
            // Remove exposed nestedPath value.
            request.removeAttribute(GspTagUtils.NESTED_PATH_VARIABLE_NAME);
        }
    }

    static Object getModelObject(String propertyPath, HttpServletRequest request, RequestContext requestContext)
    {
        BindStatus bindStatus = getBindStatus(propertyPath,request,requestContext,false)
        return getModelObject(bindStatus,request, requestContext)
    }

    static Object getModelObject(BindStatus bindStatus, HttpServletRequest request, RequestContext requestContext)
    {
        String beanName = BindStatusUtils.getBeanName(bindStatus)
        if (!beanName)
            return null
        return RequestContextUtils.getModelObject(requestContext,request,beanName)
    }


    static BindStatus getBindStatus(String bindPath, ServletRequest request, RequestContext requestContext, boolean htmlEscape = false)
    {
        String pathToUse = getPathWithinNestedPath(bindPath, request)
        return requestContext.getBindStatus(pathToUse, htmlEscape)
    }



    static String getPathWithinNestedPath(String path, ServletRequest request)
    {
        path = fixBindingExpression(path)
        String nestedPath = getNestedPath(request);
        String pathToUse = (nestedPath != null ? nestedPath + path : path);
        if (pathToUse.endsWith(PropertyAccessor.NESTED_PROPERTY_SEPARATOR))
        {
            pathToUse = pathToUse.substring(0, pathToUse.length() - 1);
        }
        return pathToUse
    }

    static String fixBindingExpression(String expression)
    {
        return expression ?: ''
    }

    static String getNestedPath(ServletRequest request)
    {
        (String) request.getAttribute(GspTagUtils.NESTED_PATH_VARIABLE_NAME);
    }

    static RequestContext ensureRequestContext(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        def result = (RequestContext) request.getAttribute(REQUEST_CONTEXT_PAGE_ATTRIBUTE)
        if (result == null)
        {
            result = new RequestContext(request, response, servletContext, null)
            request.setAttribute(GspTagUtils.REQUEST_CONTEXT_PAGE_ATTRIBUTE, result)
        }
        return result
    }

    static BindStatus getBindStatusFromScope(TemplateVariableBinding binding, HttpServletRequest request)
    {
        BindStatus bindStatus = (BindStatus)binding.getVariable(STATUS_VARIABLE_NAME)
        if (!bindStatus && request)
            bindStatus =(BindStatus)request.getAttribute(STATUS_VARIABLE_NAME)
        return bindStatus
    }

    static BindStatus getBindStatusFromScopeOrByPath(TemplateVariableBinding binding, HttpServletRequest request, RequestContext requestContext, String bindPath,boolean htmlEscape = false) {
        if (!bindPath)
            return getBindStatusFromScope(binding,request)
        return getBindStatusFromScope(binding,request) ?: getBindStatus(bindPath,request,requestContext,htmlEscape)
    }
}

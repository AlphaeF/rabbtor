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
package com.rabbtor.web.servlet.jsp.functions;


import com.rabbtor.model.ModelMetadataAccessor;
import com.rabbtor.model.ModelMetadataAccessorUtils;
import com.rabbtor.web.servlet.util.RequestContextUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.PropertyAccessor;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.JspAwareRequestContext;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import static org.springframework.web.servlet.tags.NestedPathTag.NESTED_PATH_VARIABLE_NAME;
import static org.springframework.web.servlet.tags.RequestContextAwareTag.REQUEST_CONTEXT_PAGE_ATTRIBUTE;

public class FormFunctions
{
    public static Log logger = LogFactory.getLog(FormFunctions.class);

    public static String propertyDisplayName(String path, PageContext pageContext) {
        if (!StringUtils.hasText(path))
            throw new IllegalArgumentException("path parameter for propertyDisplayName function must not be empty.");
        if (pageContext == null)
            throw new IllegalArgumentException("pageContext parameter for propertyDisplayName function must not be null.");

        return propertyDisplayName(path,pageContext,getRequestContext(pageContext));
    }

    public static String propertyDisplayName(String path, PageContext pageContext, RequestContext requestContext) {
        try {
            Object model = getModelObject(requestContext,path,pageContext);
            ModelMetadataAccessor metadataAccessor = ModelMetadataAccessorUtils.resolveOrDefault(model.getClass(),requestContext.getWebApplicationContext());
            String[] codes = metadataAccessor.getModelNameCodes(path);
            MessageSourceResolvable messageSourceResolvable = new DefaultMessageSourceResolvable(codes,metadataAccessor.getDisplayName(path));
            return requestContext.getMessage(messageSourceResolvable);
        }
        catch (RuntimeException ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }


    private static RequestContext getRequestContext(PageContext pageContext)
    {
        RequestContext requestContext =(RequestContext) pageContext.getAttribute(REQUEST_CONTEXT_PAGE_ATTRIBUTE);
        if (requestContext == null) {
            requestContext = new JspAwareRequestContext(pageContext);
            pageContext.setAttribute(REQUEST_CONTEXT_PAGE_ATTRIBUTE, requestContext);
        }
        return requestContext;
    }

    public static String nestedFormPath(PageContext pageContext) {
        return (String) pageContext.getAttribute(NESTED_PATH_VARIABLE_NAME, PageContext.REQUEST_SCOPE);
    }

    public static String boundPath(PageContext pageContext,String path) {
        String nestedPath = nestedFormPath(pageContext);
        String pathToUse = (nestedPath != null ? nestedPath + path : path);
        if (pathToUse.endsWith(PropertyAccessor.NESTED_PROPERTY_SEPARATOR)) {
            pathToUse = pathToUse.substring(0, pathToUse.length() - 1);
        }
        return pathToUse;
    }

    public static Object getModelObject(RequestContext requestContext,String path,PageContext pageContext) throws JspException
    {
        String beanName = beanNameFromBoundPath(boundPath(pageContext,path));
        Object modelObject = RequestContextUtils.getModelObject(requestContext,(HttpServletRequest)pageContext.getRequest(),beanName);

        if (modelObject == null) {
            throw new IllegalStateException("Neither BindingResult nor plain target object for bean name '" +
                    beanName + "' available as request attribute");
        }

        return modelObject;

    }

    public static String beanNameFromBoundPath(String boundPath) {
        String beanName;
        int dotPos = boundPath.indexOf('.');
        if (dotPos == -1) {
            // property not set, only the object itself
            beanName = boundPath;
        }
        else {
            beanName = boundPath.substring(0, dotPos);
        }
        return beanName;
    }
}

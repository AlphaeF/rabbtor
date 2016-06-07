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
package com.rabbtor.web.servlet.view.thymeleaf.processor;


import com.rabbtor.web.servlet.support.IncludeResult;
import com.rabbtor.web.servlet.support.IncludeStatusException;
import com.rabbtor.web.servlet.support.RequestIncludeHelper;
import com.rabbtor.web.servlet.support.RequestParams;
import com.rabbtor.web.servlet.view.thymeleaf.RabbtorDialect;
import com.rabbtor.web.servlet.view.thymeleaf.spring.SpringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.Assert;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.engine.*;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class SpringIncludeTagProcessor extends AbstractStandardExpressionAttributeTagProcessor
{
    public static final int ATTR_PRECEDENCE = 1000;
    public static final String TARGET_ATTR_NAME = "include";

    private static final TemplateMode TEMPLATE_MODE = TemplateMode.HTML;

    private static final String PARAMS_ATTR_NAME = "params";
    private static final String INCLUDE_REQUEST_PARAMS_ATTR_NAME = "includeRequestParams";
    private static String dialectPrefix;


    public SpringIncludeTagProcessor(final String dialectPrefix)
    {
        super(TEMPLATE_MODE, dialectPrefix, TARGET_ATTR_NAME, ATTR_PRECEDENCE, true);
        this.dialectPrefix = dialectPrefix;
    }


    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, Object expressionResult, IElementTagStructureHandler structureHandler)
    {
        final String paramsAttr = tag.getAttributeValue(dialectPrefix, PARAMS_ATTR_NAME);
        Assert.isTrue(context instanceof IWebContext, "templateContext must be a IWebContext.");

        RequestParams params = new RequestParams();

        if (paramsAttr != null)
        {
            final IStandardExpression paramsExpression = StandardExpressions.getExpressionParser(context.getConfiguration())
                    .parseExpression(context, paramsAttr);
            Object paramsValue = paramsExpression.execute(context);

            if (paramsValue instanceof RequestParams)
                params = (RequestParams) paramsValue;
            else
            {
                Assert.isTrue(paramsValue instanceof Map<?, ?>);
                params = new RequestParams();
                params.put((Map<String, Object>) paramsValue);
            }
        }

        String includeRequestParamsValue = tag.getAttributeValue(dialectPrefix, INCLUDE_REQUEST_PARAMS_ATTR_NAME);
        Boolean includeRequestParams = includeRequestParamsValue == null ? null : Boolean.valueOf(includeRequestParamsValue);

        RequestIncludeHelper includeHelper = new RequestIncludeHelper();
        if (includeRequestParams != null)
            includeHelper.setIncludeRequestParams(includeRequestParams.booleanValue());
        includeHelper.setParams(params);


        IWebContext webContext = (IWebContext) context;
        String path = expressionResult != null ? expressionResult.toString() : attributeValue;

        HttpServletRequest request = webContext.getRequest();
        HttpServletResponse response = webContext.getResponse();

        ApplicationContext appContext = SpringUtils.getApplicationContext(context);
        if (appContext != null)
        {
            includeHelper.setConversionService(appContext.getBean(ConversionService.class));
        }


        try
        {
            IncludeResult content = includeHelper.include(path, request, response);
            if (content.getRedirectUrl() != null)
            {
                response.sendRedirect(content.getRedirectUrl());
            } else if (content.isError())
                throw new IncludeStatusException(content);

            structureHandler.replaceWith(content.getContent(), false);

        } catch (Exception e)
        {
            throw new org.thymeleaf.exceptions.TemplateProcessingException(e.getMessage(),e);
        }


    }
}

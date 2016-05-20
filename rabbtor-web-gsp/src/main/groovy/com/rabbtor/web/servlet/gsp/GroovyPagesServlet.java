/*
 * Copyright 2004-2005 the original author or authors.
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
package com.rabbtor.web.servlet.gsp;

import com.rabbtor.gsp.GroovyPageTemplate;
import com.rabbtor.gsp.GroovyPagesTemplateEngine;
import com.rabbtor.gsp.io.GroovyPageScriptSource;
import com.rabbtor.web.servlet.mvc.RabbtorWebRequest;
import groovy.text.Template;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.FrameworkServlet;
import org.springframework.web.util.WebUtils;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NOTE: Based on work done by on the GSP standalone project (https://gsp.dev.java.net/)
 * <p>
 * Main servlet class.  Example usage in web.xml:
 * <servlet>
 * <servlet-name>GroovyPagesServlet</servlet-name>
 * <servlet-class>GroovyPagesServlet</servlet-class>
 * <init-param>
 * <param-name>showSource</param-name>
 * <param-value>1</param-value>
 * <description>
 * Allows developers to view the intermediade source code, when they pass
 * a showSource argument in the URL (eg /edit/list?showSource=true.
 * </description>
 * </init-param>
 * </servlet>
 *
 * @author Troy Heninger
 * @author Graeme Rocher
 *         <p>
 *         Date: Jan 10, 2004
 */
public class GroovyPagesServlet extends FrameworkServlet
{

    private static final long serialVersionUID = -1918149859392123495L;

    private static final String WEB_INF = "/WEB-INF";

    private ServletContext context;

    public GroovyPagesServlet()
    {
        // use the root web application context always
        setContextAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    }


    @Override
    protected ServletRequestAttributes buildRequestAttributes(HttpServletRequest request, HttpServletResponse response, RequestAttributes previousAttributes) {
        if(previousAttributes == null || previousAttributes instanceof RabbtorWebRequest )
            return createRabbtorWebRequest(request,response,previousAttributes);
        if (request.getDispatcherType() == DispatcherType.REQUEST && previousAttributes instanceof ServletRequestAttributes) {
            return createRabbtorWebRequest(request,response,previousAttributes);
        }

        else {
            return super.buildRequestAttributes(request, response, previousAttributes);
        }
    }

    private ServletRequestAttributes createRabbtorWebRequest(HttpServletRequest request, HttpServletResponse response, RequestAttributes previousAttributes)
    {
        return new RabbtorWebRequest(request,response,request.getServletContext(),getWebApplicationContext());
    }


    public static final String SERVLET_INSTANCE = "com.rabbtor.GSP_SERVLET";
    private GroovyPagesTemplateEngine groovyPagesTemplateEngine;


    @Override
    protected void initFrameworkServlet() throws ServletException, BeansException
    {
        context = getServletContext();
        context.log("GSP servlet initialized");
        context.setAttribute(SERVLET_INSTANCE, this);

        final WebApplicationContext webApplicationContext = getWebApplicationContext();
        webApplicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
        if (this.groovyPagesTemplateEngine == null)
            groovyPagesTemplateEngine = webApplicationContext.getBean(GroovyPagesTemplateEngine.BEAN_ID,
                    GroovyPagesTemplateEngine.class);

    }

    public void setGroovyPagesTemplateEngine(GroovyPagesTemplateEngine groovyPagesTemplateEngine)
    {
        this.groovyPagesTemplateEngine = groovyPagesTemplateEngine;
    }

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception
    {

        request.setAttribute(GroovyPagesServlet.SERVLET_INSTANCE, this);

        String pageName = getCurrentRequestUri(request);


        boolean isNotInclude = !WebUtils.isIncludeRequest(request);
        if (isNotInclude && isSecurePath(pageName))
        {
            sendNotFound(response, pageName);
        } else
        {

            GroovyPageScriptSource scriptSource = groovyPagesTemplateEngine.findScriptSource(pageName);
            if (scriptSource == null || (isNotInclude && !scriptSource.isPublic()))
            {
                sendNotFound(response, pageName);
                return;
            }
            renderPageWithEngine(groovyPagesTemplateEngine, request, response, scriptSource);
        }
    }

    protected String getCurrentRequestUri(HttpServletRequest request)
    {
        Object includePath = request.getAttribute("javax.servlet.include.servlet_path");
        if (includePath != null)
        {
            return (String) includePath;
        }
        return request.getServletPath();
    }

    public GroovyPagesTemplateEngine getGroovyPagesTemplateEngine()
    {
        return groovyPagesTemplateEngine;
    }

    protected boolean isSecurePath(String pageName)
    {
        return pageName.startsWith(WEB_INF);
    }

    protected void sendNotFound(HttpServletResponse response, String pageName) throws IOException
    {
        context.log("GroovyPagesServlet:  \"" + pageName + "\" not found");
        response.sendError(404, "\"" + pageName + "\" not found.");
    }


    /**
     * Attempts to render the page with the given arguments
     *
     * @param engine       The GroovyPagesTemplateEngine to use
     * @param request      The HttpServletRequest
     * @param response     The HttpServletResponse
     * @param scriptSource The template
     * @throws IOException Thrown when an I/O exception occurs rendering the page
     */
    protected void renderPageWithEngine(GroovyPagesTemplateEngine engine, HttpServletRequest request,
                                        HttpServletResponse response, GroovyPageScriptSource scriptSource) throws Exception
    {

        Template template = engine.createTemplate(scriptSource);
        if (template instanceof GroovyPageTemplate)
        {
            ((GroovyPageTemplate) template).setAllowSettingContentType(true);
        }
        template.make().writeTo(response.getWriter());

    }

}

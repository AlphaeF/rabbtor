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
package com.rabbtor.web.servlet.view;


import com.rabbtor.gsp.GroovyPageTemplate;
import com.rabbtor.gsp.GroovyPagesException;
import com.rabbtor.gsp.GroovyPagesTemplateEngine;
import com.rabbtor.web.servlet.mvc.RabbtorWebRequest;
import com.rabbtor.web.util.WebUtils;
import groovy.text.Template;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.scripting.ScriptSource;
import org.springframework.web.context.request.*;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * A Spring View that renders Groovy Server Pages to the response. It requires an instance
 * of GroovyPagesTemplateEngine to be set and will render to view returned by the getUrl()
 * method of AbstractUrlBasedView
 *
 * This view also requires an instance of GrailsWebRequest to be bound to the currently
 * executing Thread using Spring's RequestContextHolder. This can be done with by adding
 * the GrailsWebRequestFilter.
 *
 * @see #getUrl()
 * @see com.rabbtor.gsp.GroovyPagesTemplateEngine
 * @see org.springframework.web.context.request.RequestContextHolder
 *
 * @author Graeme Rocher
 * @since 0.4
 */
public class GroovyPageView extends AbstractUrlBasedView
{
    private static final Log LOG = LogFactory.getLog(GroovyPageView.class);
    protected GroovyPagesTemplateEngine templateEngine;
    private long createTimestamp = System.currentTimeMillis();
    private static final long LASTMODIFIED_CHECK_INTERVAL =  Long.getLong("grails.gsp.reload.interval", 5000).longValue();
    private ScriptSource scriptSource;
    protected Template template;
    public static final String EXCEPTION_MODEL_KEY = "exception";

    @Override
    protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        exposeModelAsRequestAttributes(model, request);
        renderWithWebRequest(model, request, response);
    }

    private void renderWithWebRequest(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
    {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        boolean attributesChanged = false;
        try {
            RabbtorWebRequest webRequest;
            if(!(requestAttributes instanceof RabbtorWebRequest)) {
                webRequest = new RabbtorWebRequest(request, response, request.getServletContext());
                attributesChanged = true;
                WebUtils.storeRabbtorWebRequest(webRequest, false);
            } else {
                webRequest = (RabbtorWebRequest) requestAttributes;
            }
            renderTemplate(model, webRequest, request, response);
        } finally {
            if(attributesChanged) {
                request.removeAttribute(RabbtorWebRequest.WEB_REQUEST);
                RequestContextHolder.setRequestAttributes(requestAttributes);
            }
        }
    }


    protected void renderTemplate(Map<String, Object> model, ServletRequestAttributes webRequest, HttpServletRequest request,
                                  HttpServletResponse response) {
        request.setAttribute("com.rabbtor.rendering.view", Boolean.TRUE);

        try {
            template.make(model).writeTo(response.getWriter());
        }
        catch (Exception e) {
            handleException(e, templateEngine);
        }
    }

    /**
     * Performs exception handling by attempting to render the Errors view.
     *
     * @param exception The exception that occured

     * @param engine The GSP engine
     */
    protected void handleException(Exception exception,
            GroovyPagesTemplateEngine engine)  {


        if(LOG.isDebugEnabled()) {
            LOG.debug("Error processing GroovyPageView: " + exception.getMessage(), exception);
        }
        if (exception instanceof GroovyPagesException) {
            throw (GroovyPagesException) exception;
        }

        if (engine == null) {
            throw new GroovyPagesException("Error processing GroovyPageView: " + exception.getMessage(),
                 exception, -1, getUrl());
        }

        throw createGroovyPageException(exception, engine, getUrl());
    }

    public static GroovyPagesException createGroovyPageException(Exception exception, GroovyPagesTemplateEngine engine, String pageUrl) {
        GroovyPageTemplate t = (GroovyPageTemplate) engine.createTemplate(pageUrl);
        StackTraceElement[] stackTrace = exception.getStackTrace();
        String className = stackTrace[0].getClassName();
        int lineNumber = stackTrace[0].getLineNumber();
        if (className.contains("_gsp")) {
            int[] lineNumbers = t.getMetaInfo().getLineNumbers();
            if (lineNumber < lineNumbers.length) {
                lineNumber = lineNumbers[lineNumber - 1];
            }
        }

        Resource resource = pageUrl != null ? engine.getResourceForUri(pageUrl) : null;
        String file;
        try {
            file = resource != null && resource.exists() ? resource.getFile().getAbsolutePath() : pageUrl;
        } catch (IOException e) {
            file = pageUrl;
        }

        return new GroovyPagesException("Error processing GroovyPageView: " + exception.getMessage(),
                exception, lineNumber, file);
    }


    public void setTemplateEngine(GroovyPagesTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - createTimestamp > LASTMODIFIED_CHECK_INTERVAL;
    }

    public void setScriptSource(ScriptSource scriptSource) {
        this.scriptSource = scriptSource;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        super.afterPropertiesSet();
        try {
            initTemplate();
        } catch(Exception e) {
            handleException(e, templateEngine);
        }
    }

    protected void initTemplate() throws IOException
    {
        if (template == null) {
            if (scriptSource == null) {
                template = templateEngine.createTemplate(getUrl());
            } else {
                template = templateEngine.createTemplate(scriptSource);
            }
        }
        if (template instanceof GroovyPageTemplate) {
            ((GroovyPageTemplate)template).setAllowSettingContentType(true);
        }
    }
    
    public void rethrowRenderException(Throwable ex, String message) {
        throw new GroovyPagesException(message, ex);
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    @Override
    protected boolean isUrlRequired() {
        return template == null;
    }
}

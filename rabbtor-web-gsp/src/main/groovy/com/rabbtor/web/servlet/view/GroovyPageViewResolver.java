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
package com.rabbtor.web.servlet.view;


import com.rabbtor.gsp.GroovyPagesTemplateEngine;
import com.rabbtor.gsp.io.GroovyPageLocator;
import com.rabbtor.gsp.io.GroovyPageScriptSource;
import com.rabbtor.web.util.WebUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.scripting.ScriptSource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Locale;

/**
 * Evaluates the existance of a view for different extensions choosing which one to delegate to.
 *
 * @author Graeme Rocher
 * @since 0.1
 */
public class GroovyPageViewResolver extends InternalResourceViewResolver
{
    private static final Log LOG = LogFactory.getLog(GroovyPageViewResolver.class);

    public static final String GSP_SUFFIX = ".gsp";

    protected GroovyPagesTemplateEngine templateEngine;
    protected GroovyPageLocator groovyPageLocator;
    private boolean setCacheCalled;


    /**
     * Constructor.
     */
    public GroovyPageViewResolver() {
        this(null,null,GSP_SUFFIX);
    }
    
    public GroovyPageViewResolver(GroovyPagesTemplateEngine templateEngine) {
        this(templateEngine,null,GSP_SUFFIX);
    }

    public GroovyPageViewResolver(GroovyPagesTemplateEngine templateEngine, String prefix, String suffix) {
        super(prefix,suffix);

        setCache(false);
        setOrder(Ordered.LOWEST_PRECEDENCE - 20);
        setTemplateEngine(templateEngine);
        this.groovyPageLocator = templateEngine.getGroovyPageLocator();
    }

    
    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception
    {
        return super.resolveViewName(WebUtils.addViewPrefix(viewName), locale);
    }



    @Override
    protected View loadView(String viewName, Locale locale) throws Exception
    {
        Assert.notNull(templateEngine, "Property [templateEngine] cannot be null");
//        if (StringUtils.hasText(getSuffix()))
//            viewName = viewName.substring(0,viewName.length()-getSuffix().length());

        return createGroovyView(viewName);
    }

    protected View createGroovyView(String viewName) throws Exception
    {

        GroovyPageScriptSource scriptSource;
        scriptSource = groovyPageLocator.findPage(viewName);

        if (scriptSource != null) {
            return createGroovyPageView(scriptSource.getURI(), scriptSource);
        }

        return null;
    }

    private View createGroovyPageView(String gspView, ScriptSource scriptSource) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Resolved GSP view at URI [" + gspView + "]");
        }
        GroovyPageView gspSpringView = new GroovyPageView();
        gspSpringView.setServletContext(getServletContext());
        gspSpringView.setUrl(gspView);
        gspSpringView.setApplicationContext(getApplicationContext());
        gspSpringView.setTemplateEngine(templateEngine);
        gspSpringView.setScriptSource(scriptSource);
        try {
            gspSpringView.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing GroovyPageView", e);
        }
        return gspSpringView;
    }

    @Autowired(required=true)
    @Qualifier(GroovyPagesTemplateEngine.BEAN_ID)
    public void setTemplateEngine(GroovyPagesTemplateEngine templateEngine) {
        Assert.notNull(templateEngine,"templateEngine must not be null.");
        this.templateEngine = templateEngine;
        if (!setCacheCalled) {
            setCache(!templateEngine.isDevelopmentMode());
        }
    }

    @Override
    public void setCache(boolean cache)
    {
        super.setCache(cache);
        setCacheCalled = true;
    }
}

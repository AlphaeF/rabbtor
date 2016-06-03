package com.rabbtor.gsp.config.annotation;

import org.springframework.beans.factory.annotation.Value;


public class GspTemplateEngineConfig extends AbstractGspConfig
{

    private static final String WEB_INF_TEMPLATE_ROOT="/WEB-INF/";
    private static final String CLASSPATH_TEMPLATE_ROOT="classpath:/templates/";


    @Value("${spring.gsp.templateRoots:}")
    String[] templateRoots;

    @Value("${spring.gsp.locator.cacheTimeout:5000}")
    long locatorCacheTimeout;

    @Value("${spring.gsp.layout.caching:true}")
    boolean gspLayoutCaching;

    @Value("${spring.gsp.layout.default:#{null}")
    String defaultLayoutName;



    public String[] getTemplateRoots()
    {
        return templateRoots;
    }

    public void setTemplateRoots(String[] templateRoots)
    {
        this.templateRoots = templateRoots;
    }

    public long getLocatorCacheTimeout()
    {
        return locatorCacheTimeout;
    }

    public void setLocatorCacheTimeout(long locatorCacheTimeout)
    {
        this.locatorCacheTimeout = locatorCacheTimeout;
    }

    public boolean isGspLayoutCaching()
    {
        return gspLayoutCaching;
    }

    public void setGspLayoutCaching(boolean gspLayoutCaching)
    {
        this.gspLayoutCaching = gspLayoutCaching;
    }

    public String getDefaultLayoutName()
    {
        return defaultLayoutName;
    }

    public void setDefaultLayoutName(String defaultLayoutName)
    {
        this.defaultLayoutName = defaultLayoutName;
    }

    public GspTemplateEngineConfig()
    {
        locatorCacheTimeout = 5000;
        gspLayoutCaching = true;
        defaultLayoutName = null;
    }
}

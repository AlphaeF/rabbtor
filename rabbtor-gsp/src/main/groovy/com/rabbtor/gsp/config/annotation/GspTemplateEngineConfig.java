package com.rabbtor.gsp.config.annotation;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by Cagatay on 29.05.2016.
 */
public class GspTemplateEngineConfig extends AbstractGspConfig
{

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

}

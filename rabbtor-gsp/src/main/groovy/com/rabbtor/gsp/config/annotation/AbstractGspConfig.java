package com.rabbtor.gsp.config.annotation;

import org.springframework.beans.factory.annotation.Value;


public abstract class AbstractGspConfig
{
    @Value("${spring.gsp.reloadingEnabled:true}")
    boolean gspReloadingEnabled;

    @Value("${spring.gsp.view.cacheTimeout:1000}")
    long viewCacheTimeout;

    public boolean isGspReloadingEnabled()
    {
        return gspReloadingEnabled;
    }

    public void setGspReloadingEnabled(boolean gspReloadingEnabled)
    {
        this.gspReloadingEnabled = gspReloadingEnabled;
    }

    public long getViewCacheTimeout()
    {
        return viewCacheTimeout;
    }

    public void setViewCacheTimeout(long viewCacheTimeout)
    {
        this.viewCacheTimeout = viewCacheTimeout;
    }

    protected AbstractGspConfig()
    {
        gspReloadingEnabled = true;
        viewCacheTimeout = 1000;
    }
}

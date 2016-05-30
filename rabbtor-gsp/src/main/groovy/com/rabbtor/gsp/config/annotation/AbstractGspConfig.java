package com.rabbtor.gsp.config.annotation;

import org.springframework.beans.factory.annotation.Value;

/**
 * Created by Cagatay on 29.05.2016.
 */
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
}

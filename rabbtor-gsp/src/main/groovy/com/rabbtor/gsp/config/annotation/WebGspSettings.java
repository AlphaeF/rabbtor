
package com.rabbtor.gsp.config.annotation;

public class WebGspSettings extends GspSettings
{
    long viewCacheTimeout;

    public long getViewCacheTimeout()
    {
        return viewCacheTimeout;
    }

    public void setViewCacheTimeout(long viewCacheTimeout)
    {
        this.viewCacheTimeout = viewCacheTimeout;
    }


    public WebGspSettings()
    {
        viewCacheTimeout = 1000;
    }
}

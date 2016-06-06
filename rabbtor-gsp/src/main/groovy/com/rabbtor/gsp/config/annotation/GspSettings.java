
package com.rabbtor.gsp.config.annotation;

public abstract class GspSettings
{

    boolean gspReloadingEnabled;

    long locatorCacheTimeout;

    boolean gspLayoutCaching;

    String defaultLayoutName;

    String[] templateRoots;

    public boolean isGspReloadingEnabled()
    {
        return gspReloadingEnabled;
    }

    public void setGspReloadingEnabled(boolean gspReloadingEnabled)
    {
        this.gspReloadingEnabled = gspReloadingEnabled;
    }



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



    protected GspSettings()
    {
        locatorCacheTimeout = 5000;
        gspLayoutCaching = true;
        defaultLayoutName = null;
        gspReloadingEnabled = true;

    }
}

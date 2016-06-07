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
package com.rabbtor.gsp.config.annotation;

public class GspSettings
{

    boolean gspReloadingEnabled;

    long locatorCacheTimeout;

    boolean gspLayoutCaching;

    String defaultLayoutName;

    String[] templateRoots;

    long viewCacheTimeout;

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



    public long getViewCacheTimeout()
    {
        return viewCacheTimeout;
    }

    public void setViewCacheTimeout(long viewCacheTimeout)
    {
        this.viewCacheTimeout = viewCacheTimeout;
    }


    protected GspSettings()
    {
        locatorCacheTimeout = 5000;
        gspLayoutCaching = true;
        defaultLayoutName = null;
        gspReloadingEnabled = true;
        viewCacheTimeout = 1000;

    }
}

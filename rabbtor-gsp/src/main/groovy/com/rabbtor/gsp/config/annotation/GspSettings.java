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

import org.springframework.beans.factory.annotation.Value;

public class GspSettings
{

    public static final String SUFFIX = ".gsp";

    @Value("${spring.gsp.reload:true}")
    boolean gspReloadingEnabled;

    @Value("${spring.gsp.locator.cacheTimeout:5000}")
    long locatorCacheTimeout;

    @Value("${spring.gsp.layout.cache:true}")
    boolean gspLayoutCaching;

    @Value("${spring.gsp.layout.default:#{null}}")
    String defaultLayoutName;

    @Value("${spring.gsp.templateRoots:#{null}}")
    String[] templateRoots;

    @Value("${spring.gsp.suffix:.gsp}")
    String suffix;

    @Value("${spring.gsp.attributeTagsEnabled:true}")
    boolean attributeTagsEnabled;


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

    public String getSuffix()
    {
        return suffix;
    }

    public void setSuffix(String suffix)
    {
        this.suffix = suffix;
    }

    public boolean isAttributeTagsEnabled()
    {
        return attributeTagsEnabled;
    }

    public void setAttributeTagsEnabled(boolean attributeTagsEnabled)
    {
        this.attributeTagsEnabled = attributeTagsEnabled;
    }

    public GspSettings()
    {
        locatorCacheTimeout = 5000;
        gspLayoutCaching = true;
        defaultLayoutName = null;
        gspReloadingEnabled = true;
        viewCacheTimeout = 1000;
        suffix = SUFFIX;
        attributeTagsEnabled = true;

    }
}

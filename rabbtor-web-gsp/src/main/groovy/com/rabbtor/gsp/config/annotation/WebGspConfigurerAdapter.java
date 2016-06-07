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


import java.util.List;
import java.util.Set;

public class WebGspConfigurerAdapter implements WebGspConfigurer
{
    @Override
    public void configureGsp(GspSettings config)
    {

    }

    @Override
    public void registerGspTagLibraries(Set<Class<?>> tagLibRegistry)
    {

    }

    @Override
    public void registerTldScanPaths(List<String> scanPaths)
    {

    }
}

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


import com.rabbtor.gsp.jsp.TagLibraryResolverImpl;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public abstract class GspJspConfigurationSupport
{
    @Bean(autowire = Autowire.BY_NAME)
    public TagLibraryResolverImpl jspTagLibraryResolver()
    {
        TagLibraryResolverImpl resolver = new TagLibraryResolverImpl();
        resolver.setTldScanPatterns(getTldScanPaths());
        return resolver;
    }


    protected void registerTldScanPaths(List<String> paths) {

    }

    public String[] getTldScanPaths()
    {
        List<String> tldScanPaths = new ArrayList();

        Collection<String> defaultPaths = Arrays.asList("classpath*:/META-INF/spring*.tld,classpath*:/META-INF/fmt.tld,classpath*:/META-INF/c.tld,classpath*:/META-INF/rabbtor*.tld,classpath*:/META-INF/c-1_0-rt.tld"
                .split(","));

        for(String defaultPath : defaultPaths ) {
            tldScanPaths.add(defaultPath.trim());
        }

        registerTldScanPaths(tldScanPaths);
        return tldScanPaths.toArray(new String[tldScanPaths.size()]);
    }
}

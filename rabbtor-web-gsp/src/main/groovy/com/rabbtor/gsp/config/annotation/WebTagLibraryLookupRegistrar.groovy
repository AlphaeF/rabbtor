/*
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

import com.rabbtor.gsp.taglib.config.annotation.TagLibraryLookupRegistrar;
import com.rabbtor.gsp.tags.ApplicationTagLib;
import com.rabbtor.gsp.tags.FormatTagLib;
import org.grails.plugins.web.taglib.RenderTagLib;
import org.grails.plugins.web.taglib.SitemeshTagLib;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.Annotation;
import java.util.Set;


public class WebTagLibraryLookupRegistrar extends TagLibraryLookupRegistrar
{
    @Override
    protected void registerDefaultTagLibClasses(Set<Class<?>> tagLibClasses, AnnotationAttributes attributes, AnnotationMetadata importingClassMetadata)
    {
        tagLibClasses.add(RenderTagLib.class);
        tagLibClasses.add(SitemeshTagLib.class);
        tagLibClasses.add(ApplicationTagLib.class);
        tagLibClasses.add(FormatTagLib.class);
    }

    @Override
    public Class<? extends Annotation> getAnnotationClass()
    {
        return EnableWebGsp.class;
    }
}

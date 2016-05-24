package com.rabbtor.gsp.config.annotation

import com.rabbtor.gsp.tags.ApplicationTagLib
import groovy.transform.CompileStatic
import org.grails.plugins.web.taglib.RenderTagLib
import org.grails.plugins.web.taglib.SitemeshTagLib;

import java.lang.annotation.Annotation;

@CompileStatic
public class WebTagLibraryLookupRegistrar extends TagLibraryLookupRegistrar
{
    @Override
    public Class<? extends Annotation> getAnnotationClass()
    {
        return EnableWebGsp.class;
    }

    @Override
    protected void addDefaultTagLibClasses(Set<Class> tagLibClasses)
    {
        super.addDefaultTagLibClasses(tagLibClasses)
        [ApplicationTagLib, RenderTagLib, SitemeshTagLib].each {
            tagLibClasses.add(it)
        }
    }
}

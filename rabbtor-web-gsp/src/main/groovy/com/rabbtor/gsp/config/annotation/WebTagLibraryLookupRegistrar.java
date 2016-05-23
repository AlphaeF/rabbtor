package com.rabbtor.gsp.config.annotation;


import com.rabbtor.gsp.tags.ApplicationTagLib;
import org.grails.plugins.web.taglib.RenderTagLib;
import org.grails.plugins.web.taglib.SitemeshTagLib;
import org.grails.web.pages.StandaloneTagLibraryLookup;

import java.lang.annotation.Annotation;
import java.util.Set;

public class WebTagLibraryLookupRegistrar extends TagLibraryLookupRegistrar
{
    @Override
    public Class<? extends Annotation> getAnnotationClass()
    {
        return EnableWebGsp.class;
    }

    @Override
    protected void addDefaultTagLibClasses(Set<Class<?>> tagLibClasses)
    {
        tagLibClasses.add(ApplicationTagLib.class);
        tagLibClasses.add(RenderTagLib.class);
        tagLibClasses.add(SitemeshTagLib.class);
        super.addDefaultTagLibClasses(tagLibClasses);
    }

    @Override
    public Class getLookupBeanClass()
    {
        return StandaloneTagLibraryLookup.class;
    }
}

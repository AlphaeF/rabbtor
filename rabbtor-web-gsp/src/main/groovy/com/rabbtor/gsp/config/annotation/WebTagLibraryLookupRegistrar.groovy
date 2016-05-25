package com.rabbtor.gsp.config.annotation

import com.rabbtor.gsp.tags.ApplicationTagLib
import grails.gsp.TagLib
import groovy.transform.CompileStatic
import org.grails.plugins.web.taglib.RenderTagLib
import org.grails.plugins.web.taglib.SitemeshTagLib
import org.grails.web.pages.StandaloneTagLibraryLookup
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter;

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
    Class getLookupBeanClass()
    {
        return StandaloneTagLibraryLookup.class;
    }

    @Override
    protected ClassPathScanningCandidateComponentProvider createComponentProvider()
    {
        ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(
                false);

        componentProvider.addIncludeFilter(new AnnotationTypeFilter(TagLib.class));
        return componentProvider;
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

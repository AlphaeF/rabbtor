package com.rabbtor.gsp.taglib;

import com.rabbtor.gsp.taglib.config.annotation.TagLibraryRegistry;
import grails.core.GrailsTagLibClass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.grails.core.DefaultGrailsTagLibClass;
import org.grails.taglib.TagLibraryLookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.ClassUtils;

import java.util.*;

public class StandaloneTagLibraryLookup extends TagLibraryLookup implements ApplicationListener<ContextRefreshedEvent>
{

    private static Log LOG = LogFactory.getLog(StandaloneTagLibraryLookup.class);

    Set<Object> tagLibInstancesSet;

    @Autowired(required = false)
    TagLibraryRegistry gspTagLibraryRegistry;

    private StandaloneTagLibraryLookup() {

    }

    public void afterPropertiesSet() {
        combine();

        registerTagLibraries();
        registerTemplateNamespace();
    }

    private void combine()
    {
        if (gspTagLibraryRegistry != null) {
            Set<Class<?>> onDemandClasses = gspTagLibraryRegistry.getTagLibInstances();
            if (onDemandClasses != null) {
                if (tagLibInstancesSet == null)
                    tagLibInstancesSet = new HashSet<>();

                tagLibInstancesSet.addAll(onDemandClasses);
            }

        }
    }

    protected void registerTagLibraries() {
        if(tagLibInstancesSet != null) {
            for(Object tagLibInstance : tagLibInstancesSet) {
                registerTagLib(new DefaultGrailsTagLibClass(tagLibInstance.getClass()));
            }
        }
    }

    @Override
    protected void putTagLib(Map<String, Object> tags, String name, GrailsTagLibClass taglib) {
        for(Object tagLibInstance : tagLibInstancesSet) {
            if(tagLibInstance.getClass() == taglib.getClazz()) {
                tags.put(name, tagLibInstance);
                break;
            }
        }
    }

    public void setTagLibInstances(List<Object> tagLibInstances) {
        this.tagLibInstancesSet = new LinkedHashSet<Object>();
        tagLibInstancesSet.addAll(tagLibInstances);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        detectAndRegisterTabLibBeans();
    }

    public void detectAndRegisterTabLibBeans() {
        if (ClassUtils.isPresent("grails.gsp.TagLib", StandaloneTagLibraryLookup.class.getClassLoader())) {
            Class annotationClass = null;
            try
            {
                annotationClass = Class.forName("grails.gsp.TagLib");
                if(tagLibInstancesSet==null) {
                    tagLibInstancesSet = new LinkedHashSet<Object>();
                }
                Collection<Object> detectedInstances = applicationContext.getBeansWithAnnotation(annotationClass).values();
                for(Object instance : detectedInstances) {
                    if(!tagLibInstancesSet.contains(instance)) {
                        tagLibInstancesSet.add(instance);
                        registerTagLib(new DefaultGrailsTagLibClass(instance.getClass()));
                    }
                }
            } catch (ClassNotFoundException e)
            {
                LOG.warn("grails.gsp.TagLib class was not found in classpath. Resolution of tag libraries annotated with this annotation is disabled.",e);
            }

        }


    }
}

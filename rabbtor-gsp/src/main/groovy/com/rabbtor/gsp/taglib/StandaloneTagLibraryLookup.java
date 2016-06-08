/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Modifications copyright (C) 2016 Rabbytes Incorporated */

package com.rabbtor.gsp.taglib;

import grails.core.GrailsTagLibClass;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.grails.core.DefaultGrailsTagLibClass;
import org.grails.taglib.TagLibraryLookup;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.ClassUtils;

import java.util.*;

public class StandaloneTagLibraryLookup extends TagLibraryLookup implements ApplicationListener<ContextRefreshedEvent>
{

    private static Log LOG = LogFactory.getLog(StandaloneTagLibraryLookup.class);

    Set<Object> tagLibInstancesSet;


    private StandaloneTagLibraryLookup()
    {

    }

    public void afterPropertiesSet()
    {


        registerTagLibraries();
        registerTemplateNamespace();
    }


    protected void registerTagLibraries()
    {
        if (tagLibInstancesSet != null)
        {
            for (Object tagLibInstance : tagLibInstancesSet)
            {
                registerTagLib(new DefaultGrailsTagLibClass(tagLibInstance.getClass()));
            }
        }
    }

    @Override
    protected void putTagLib(Map<String, Object> tags, String name, GrailsTagLibClass taglib)
    {
        for (Object tagLibInstance : tagLibInstancesSet)
        {
            if (tagLibInstance.getClass() == taglib.getClazz())
            {
                tags.put(name, tagLibInstance);
                break;
            }
        }
    }

    public void setTagLibInstances(List<Object> tagLibInstances)
    {
        this.tagLibInstancesSet = new LinkedHashSet<Object>();
        tagLibInstancesSet.addAll(tagLibInstances);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        detectAndRegisterTabLibBeans();
    }

    public void detectAndRegisterTabLibBeans()
    {
        if (ClassUtils.isPresent("grails.gsp.TagLib", StandaloneTagLibraryLookup.class.getClassLoader()))
        {
            Class annotationClass = null;
            try
            {
                annotationClass = Class.forName("grails.gsp.TagLib");
                if (tagLibInstancesSet == null)
                {
                    tagLibInstancesSet = new LinkedHashSet<Object>();
                }
                Collection<Object> detectedInstances = applicationContext.getBeansWithAnnotation(annotationClass).values();
                for (Object instance : detectedInstances)
                {
                    if (!tagLibInstancesSet.contains(instance))
                    {
                        tagLibInstancesSet.add(instance);
                        registerTagLib(new DefaultGrailsTagLibClass(instance.getClass()));
                    }
                }
            } catch (ClassNotFoundException e)
            {
                LOG.warn("grails.gsp.TagLib class was not found in classpath. Resolution of tag libraries annotated with this annotation is disabled.", e);
            }

        }


    }
}

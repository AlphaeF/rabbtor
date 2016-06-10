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

package com.rabbtor.gsp.taglib.config.annotation;


import com.rabbtor.gsp.config.annotation.EnableGsp;
import com.rabbtor.gsp.taglib.StandaloneTagLibraryLookup;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.util.*;


public class TagLibraryLookupRegistrar implements ImportBeanDefinitionRegistrar
{

    public static final String GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME = "gspTagLibraryLookup";
    public static final String TAG_LIBRARY_LOOKUP_BEAN_NAME = "tagLibraryLookup";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry)
    {


        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(getAnnotationClass().getName()));

        Set<Class<?>> tagLibClasses = new HashSet();
        registerDefaultTagLibClasses(tagLibClasses, attributes, importingClassMetadata);

        Class<?>[] onDemandTagLibClasses = attributes.getClassArray("tagLibClasses");
        if (onDemandTagLibClasses != null)
            tagLibClasses.addAll(Arrays.asList(onDemandTagLibClasses));


        ManagedList<BeanDefinition> list = new ManagedList();
        for (Class<?> clazz : tagLibClasses)
        {
            list.add(createBeanDefinition(clazz));
        }

        Set<String> packagesToScan = getPackagesToScan(importingClassMetadata);
        scanPackages(packagesToScan, list);

        createOrUpdateBeanDefinition(registry, list);

    }

    protected void registerDefaultTagLibClasses(Set<Class<?>> tagLibClasses, AnnotationAttributes attributes, AnnotationMetadata importingClassMetadata)
    {

    }


    private static void updateBeanDefinition(BeanDefinitionRegistry registry, ManagedList<BeanDefinition> list)
    {
        if (registry.containsBeanDefinition(GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME))
            registry.getBeanDefinition(GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME).getPropertyValues()
                    .add("tagLibInstances", list);
        registry.registerAlias(GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME, TAG_LIBRARY_LOOKUP_BEAN_NAME);
    }


    protected void scanPackages(Set<String> packagesToScan, ManagedList<BeanDefinition> list)
    {
        if (packagesToScan.size() > 0)
        {
            ClassPathScanningCandidateComponentProvider componentProvider = createComponentProvider();
            if (componentProvider != null)
            {
                for (String packageToScan : packagesToScan)
                {
                    scanPackage(componentProvider, packageToScan, list);
                }
            }
        }
    }

    private static void registerBean(BeanDefinitionRegistry registry, ManagedList<BeanDefinition> list)
    {
        GenericBeanDefinition beanDefinition = createBeanDefinition(StandaloneTagLibraryLookup.class);
        beanDefinition.getPropertyValues().addPropertyValue("tagLibInstances", list);
        registry.registerBeanDefinition(GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME, beanDefinition);

        registry.registerAlias(GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME, TAG_LIBRARY_LOOKUP_BEAN_NAME);
    }

    private void scanPackage(ClassPathScanningCandidateComponentProvider componentProvider, String packageToScan, ManagedList<BeanDefinition> beanDefinitions)
    {
        for (BeanDefinition candidate : componentProvider
                .findCandidateComponents(packageToScan))
        {
            if (candidate instanceof ScannedGenericBeanDefinition)
            {
                ScannedGenericBeanDefinition scannedBean = (ScannedGenericBeanDefinition) candidate;
                GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                beanDefinition.setBeanClassName(scannedBean.getBeanClassName());
                beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
                addToBeanDefinitionList(beanDefinition, beanDefinitions);
            }
        }
    }

    private void addToBeanDefinitionList(final GenericBeanDefinition beanDefinition, ManagedList<BeanDefinition> beanDefinitions)
    {
        boolean contains = false;
        for (BeanDefinition bean : beanDefinitions) {
            if (bean.getBeanClassName().equals(beanDefinition.getBeanClassName())) {
                contains = true;
                break;
            }
        }
        if (!contains)
            beanDefinitions.add(beanDefinition);
    }


    protected ClassPathScanningCandidateComponentProvider createComponentProvider()
    {
        ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(
                false);

        try
        {
            Class annoClass = Class.forName("grails.gsp.TagLib");
            componentProvider.addIncludeFilter(new AnnotationTypeFilter(annoClass));
            return componentProvider;
        } catch (ClassNotFoundException e)
        {
            return null;
        }


    }


    protected Set<String> getPackagesToScan(AnnotationMetadata importingClassMetadata)
    {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(GspTagLibScan.class.getName()));

        if (attributes == null)
            return Collections.emptySet();

        String[] value = attributes.getStringArray("value");
        String[] basePackages = attributes.getStringArray("basePackages");
        Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
        if (!ObjectUtils.isEmpty(value))
        {
            Assert.state(ObjectUtils.isEmpty(basePackages),
                    "@GspTagLibScan basePackages and value attributes are"
                            + " mutually exclusive");
        }
        Set<String> packagesToScan = new LinkedHashSet<String>();
        packagesToScan.addAll(Arrays.asList(value));
        packagesToScan.addAll(Arrays.asList(basePackages));
        for (Class<?> basePackageClass : basePackageClasses)
        {
            packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
        }
        if (packagesToScan.isEmpty())
        {
            return Collections
                    .singleton(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return packagesToScan;
    }


    public static GenericBeanDefinition createBeanDefinition(Class<?> beanClass)
    {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
        return beanDefinition;
    }


    public Class<? extends Annotation> getAnnotationClass()
    {
        return EnableGsp.class;
    }

    public static void createOrUpdateBeanDefinition(BeanDefinitionRegistry registry, ManagedList<BeanDefinition> list)
    {
        if (!registry.containsBeanDefinition(GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME))
        {
            registerBean(registry, list);
        } else
        {
            updateBeanDefinition(registry, list);
        }

    }
}

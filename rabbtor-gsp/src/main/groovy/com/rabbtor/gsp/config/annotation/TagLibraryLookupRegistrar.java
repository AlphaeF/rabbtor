package com.rabbtor.gsp.config.annotation;

import grails.gsp.taglib.AnnotationScanTagLibraryLookup;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class TagLibraryLookupRegistrar implements ImportBeanDefinitionRegistrar
{


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry)
    {
        if (!registry.containsBeanDefinition("gspTagLibraryLookup"))
        {
            GenericBeanDefinition beanDefinition = createBeanDefinition(getLookupBeanClass());

            ManagedList<BeanDefinition> list = new ManagedList<BeanDefinition>();

            Set<Class<?>> tagLibClasses = getTagLibClassesFromAnnotation(importingClassMetadata);
            registerTagLibs(list,tagLibClasses);

            beanDefinition.getPropertyValues().addPropertyValue("tagLibInstances", list);

            registry.registerBeanDefinition("gspTagLibraryLookup", beanDefinition);
            registry.registerAlias("gspTagLibraryLookup", "tagLibraryLookup");
        }
    }

    private Set<Class<?>> getTagLibClassesFromAnnotation(AnnotationMetadata metadata)
    {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(EnableGsp.class.getName()));
        if (attributes != null)
            return Arrays.asList(attributes.getClassArray("tagLibClasses"))
                    .stream().collect(Collectors.toSet());

        return Collections.emptySet();

    }

    protected void registerTagLibs(ManagedList<BeanDefinition> list, Set<Class<?>> classesFromAnnotation)
    {
        Set<Class<?>> tagLibClasses = new HashSet<>();
        addDefaultTagLibClasses(tagLibClasses);
        if (classesFromAnnotation != null)
            tagLibClasses.addAll(classesFromAnnotation);

        for (Class<?> taglibClazz : tagLibClasses)
        {
            list.add(createBeanDefinition(taglibClazz));
        }
    }

    protected void addDefaultTagLibClasses(Set<Class<?>> tagLibClasses)
    {

    }

    protected GenericBeanDefinition createBeanDefinition(Class<?> beanClass)
    {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_NAME);
        return beanDefinition;
    }

    public Class<? extends Annotation> getAnnotationClass()
    {
        return EnableGsp.class;
    }

    public Class getLookupBeanClass() {
        return AnnotationScanTagLibraryLookup.class;
    }
}

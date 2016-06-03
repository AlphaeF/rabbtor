package com.rabbtor.gsp.taglib.config.annotation;

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
        if (attributes == null)
            return;

        Set<String> packagesToScan = getPackagesToScan(importingClassMetadata,attributes);
        Set<Class<?>> tagLibClasses = new HashSet();


        ManagedList<GenericBeanDefinition> list = new ManagedList<GenericBeanDefinition>();
        for (Class tagLibClass : tagLibClasses) {
            list.add(createBeanDefinition(tagLibClass));
        }

        scanPackages(importingClassMetadata,registry,packagesToScan,list);

        if (!registry.containsBeanDefinition(GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME)) {
            registerBean(registry,list);
        } else {
            updateBeanDefinition(registry,list);
        }
    }



    private void updateBeanDefinition(BeanDefinitionRegistry registry, ManagedList<GenericBeanDefinition> list)
    {
        if (registry.containsBeanDefinition(GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME))
            registry.getBeanDefinition(GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME).getPropertyValues()
                    .add("tagLibInstances",list);
        registry.registerAlias(GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME, TAG_LIBRARY_LOOKUP_BEAN_NAME);
    }





    protected void scanPackages(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, Set<String> packagesToScan, ManagedList<GenericBeanDefinition> list)
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

    private void registerBean(BeanDefinitionRegistry registry, ManagedList<GenericBeanDefinition> list)
    {
        GenericBeanDefinition beanDefinition = createBeanDefinition(StandaloneTagLibraryLookup.class);
        beanDefinition.getPropertyValues().addPropertyValue("tagLibInstances", list);
        registry.registerBeanDefinition(GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME,beanDefinition);

        registry.registerAlias(GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME, TAG_LIBRARY_LOOKUP_BEAN_NAME);
    }

    private void scanPackage(ClassPathScanningCandidateComponentProvider componentProvider, String packageToScan, ManagedList<GenericBeanDefinition> beanDefinitions)
    {
        for (BeanDefinition candidate : componentProvider
                .findCandidateComponents(packageToScan)) {
            if (candidate instanceof ScannedGenericBeanDefinition) {
                ScannedGenericBeanDefinition scannedBean = (ScannedGenericBeanDefinition) candidate;
                GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                beanDefinition.setBeanClassName(scannedBean.getBeanClassName());
                beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
                addToBeanDefinitionList(beanDefinition,beanDefinitions);
            }
        }
    }

    private void addToBeanDefinitionList(GenericBeanDefinition beanDefinition, ManagedList<GenericBeanDefinition> beanDefinitions)
    {
        if (!beanDefinitions.stream().anyMatch(bean -> bean.getBeanClassName().equals(beanDefinition.getBeanClassName()) )) {
            beanDefinitions.add(beanDefinition);
        }
    }

    private BeanDefinition createBeanDefinition(ScannedGenericBeanDefinition candidate)
    {
        return createBeanDefinition(candidate.getBeanClass());
    }

    protected ClassPathScanningCandidateComponentProvider createComponentProvider() {
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


    private Set<String> getPackagesToScan(AnnotationMetadata metadata,AnnotationAttributes attributes) {

        String[] value = attributes.getStringArray("value");
        String[] basePackages = attributes.getStringArray("basePackages");
        Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
        if (!ObjectUtils.isEmpty(value)) {
            Assert.state(ObjectUtils.isEmpty(basePackages),
                    "@GspTagLibScan basePackages and value attributes are"
                            + " mutually exclusive");
        }
        Set<String> packagesToScan = new LinkedHashSet<String>();
        packagesToScan.addAll(Arrays.asList(value));
        packagesToScan.addAll(Arrays.asList(basePackages));
        for (Class<?> basePackageClass : basePackageClasses) {
            packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
        }
        if (packagesToScan.isEmpty()) {
            return Collections
                    .singleton(ClassUtils.getPackageName(metadata.getClassName()));
        }
        return packagesToScan;
    }



    protected GenericBeanDefinition createBeanDefinition(Class<?> beanClass)
    {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
        return beanDefinition;
    }



    public Class<? extends Annotation> getAnnotationClass()
    {
        return GspTagLibScan.class;
    }
}

package com.rabbtor.gsp.config.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

import static com.rabbtor.gsp.config.annotation.TagLibraryLookupRegistrar.GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME;
import static com.rabbtor.gsp.config.annotation.TagLibraryLookupRegistrar.TAG_LIBRARY_LOOKUP_BEAN_NAME;


public class OnDemandTagLibraryLookupRegistrar implements ImportBeanDefinitionRegistrar
{


    private void updateBeanDefinition(BeanDefinitionRegistry registry, ManagedList<BeanDefinition> list)
    {
        if (registry.containsBeanDefinition(GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME))
            registry.getBeanDefinition(GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME).getPropertyValues()
                    .add("tagLibInstances",list);
        registry.registerAlias(GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME, TAG_LIBRARY_LOOKUP_BEAN_NAME);
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry)
    {
        ManagedList<BeanDefinition> list = new ManagedList<BeanDefinition>();
        registerTagLibs(list,registry);

        if (!registry.containsBeanDefinition(GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME)) {
            registerBean(registry,list);
        } else {
            updateBeanDefinition(registry,list);
        }
    }

    private void registerBean(BeanDefinitionRegistry registry, ManagedList<BeanDefinition> list)
    {
        GenericBeanDefinition beanDefinition = createBeanDefinition(getLookupBeanClassName());
        beanDefinition.getPropertyValues().addPropertyValue("tagLibInstances", list);
        registry.registerBeanDefinition(GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME,beanDefinition);

        registry.registerAlias(GSP_TAG_LIBRARY_LOOKUP_BEAN_NAME, TAG_LIBRARY_LOOKUP_BEAN_NAME);
    }

    protected String getLookupBeanClassName()
    {
        return "org.grails.web.pages.StandaloneTagLibraryLookup";
    }

    private void registerTagLibs(ManagedList<BeanDefinition> list, BeanDefinitionRegistry registry)
    {

        if (registry.containsBeanDefinition("gspTagLibraryRegistry")) {
            Set<Class<?>> classes = (Set<Class<?>>) registry.getBeanDefinition("gspTagLibraryRegistry")
                        .getPropertyValues().get("tagLibClasses");
            if (classes != null) {
                for (Class<?> taglibClazz : classes)
                {
                    list.add(createBeanDefinition(taglibClazz));
                }
            }

        }


    }

    protected GenericBeanDefinition createBeanDefinition(Class<?> beanClass)
    {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
        return beanDefinition;
    }

    protected GenericBeanDefinition createBeanDefinition(String beanClassName)
    {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
        return beanDefinition;
    }
}

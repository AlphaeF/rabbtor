package com.rabbtor.taglib;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;

public class TagLibrariesBeanFactoryPostProcessor implements BeanFactoryPostProcessor,Ordered
{


    private int order = Ordered.LOWEST_PRECEDENCE-50;


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException
    {
        Arrays.asList(beanFactory.getBeanDefinitionNames())
                .stream().filter(name -> name.endsWith(TagLibClass.TAGLIB_BEAN_NAME_SUFFIX))
                .forEach(name -> {
                    BeanDefinition bean = beanFactory.getBeanDefinition(name);
                    try
                    {

                        Class clazz = tryGetClass(bean);
                        if (clazz != null && AnnotatedElementUtils.hasAnnotation(clazz,TagLib.class))
                        {
                            DefaultTagLibClass tagLibClass = new DefaultTagLibClass(clazz,name, "TagLib");
                            beanFactory.registerSingleton(tagLibClass.getFullName()+"Class", tagLibClass);
                        }

                    } catch (ClassNotFoundException e)
                    {
                        throw new RuntimeException("Could not load class " + bean.getBeanClassName());
                    }
                });

    }

    private Class tryGetClass(BeanDefinition bean) throws ClassNotFoundException
    {
        String className = bean.getBeanClassName();
        if (className == null && bean instanceof AnnotatedBeanDefinition) {
            AnnotatedBeanDefinition annotatedBean = (AnnotatedBeanDefinition) bean;
            className = annotatedBean.getFactoryMethodMetadata().getReturnTypeName();
        }


        return className == null ? null : Class.forName(className);

    }

    @Override
    public int getOrder()
    {
        return order;
    }

    public void setOrder(int order)
    {
        this.order = order;
    }
}

package com.rabbtor.util;


import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

public class BeanFactoryUtils
{
    public static <T> Optional<T> tryGetBean(BeanFactory beanFactory, Class<T> beanClass) {
        try {
            return Optional.of(beanFactory.getBean(beanClass));
        } catch (NoSuchBeanDefinitionException e) {
            return Optional.empty();
        }
    }

    public static <T> Optional<T> tryGetBean(BeanFactory beanFactory, String beanName, Class<T> beanClass) {
        try {
            return Optional.of(beanFactory.getBean(beanName,beanClass));
        } catch (NoSuchBeanDefinitionException e) {
            return Optional.empty();
        }
    }

    public static Optional<Object> tryGetBean(BeanFactory beanFactory, String beanName) {
        if (beanFactory.containsBean(beanName))
            return Optional.of(beanFactory.getBean(beanName));
        return Optional.empty();
    }
}

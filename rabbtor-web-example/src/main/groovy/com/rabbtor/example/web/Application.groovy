/*
 * Copyright 2016 - Rabbytes Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Rabbytes Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Rabbytes Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Rabbytes Incorporated.
 */
package com.rabbtor.example.web

import com.rabbtor.gsp.config.annotation.EnableWebGsp
import com.rabbtor.gsp.taglib.config.annotation.GspTagLibScan
import com.rabbtor.model.DefaultModelMetadataAccessorFactory
import com.rabbtor.model.DefaultModelMetadataRegistry
import com.rabbtor.model.ModelMetadataAccessorFactory
import com.rabbtor.model.ModelMetadataRegistry
import com.rabbtor.validation.RabbtorOptionalValidatorFactoryBean
import org.grails.spring.context.support.ReloadableResourceBundleMessageSource
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.validation.Validator
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@SpringBootApplication
@EnableAutoConfiguration(exclude = ThymeleafAutoConfiguration)
@EnableWebGsp
@GspTagLibScan
class Application extends WebMvcConfigurerAdapter
{
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource()
        messageSource.basename = 'classpath:i18n/messages'
        messageSource
    }

    @Bean
    ModelMetadataAccessorFactory modelMetadataAccessorFactory() {
        DefaultModelMetadataAccessorFactory factory = new DefaultModelMetadataAccessorFactory()
        factory.setModelMetadataRegistry(modelMetadataRegistry())
        return factory
    }

    @Override
    Validator getValidator() {
        RabbtorOptionalValidatorFactoryBean validatorFactoryBean =  new RabbtorOptionalValidatorFactoryBean()
        validatorFactoryBean.setModelMetadataAccessorFactory(modelMetadataAccessorFactory())
        return validatorFactoryBean
    }

    @Bean
    ModelMetadataRegistry modelMetadataRegistry() {
        return new DefaultModelMetadataRegistry()
    }
}

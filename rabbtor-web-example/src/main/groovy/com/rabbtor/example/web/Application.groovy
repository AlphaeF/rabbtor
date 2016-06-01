package com.rabbtor.example.web

import com.rabbtor.gsp.config.annotation.EnableWebGsp
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

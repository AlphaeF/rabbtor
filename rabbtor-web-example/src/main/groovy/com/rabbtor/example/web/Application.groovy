package com.rabbtor.example.web

import com.rabbtor.gsp.GspConfiguration
import com.rabbtor.taglib.TagLibrariesBeanFactoryPostProcessor
import com.rabbtor.web.servlet.mvc.config.EnableGsp
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration
import org.springframework.boot.context.embedded.ServletRegistrationBean
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableAutoConfiguration(exclude = ThymeleafAutoConfiguration)
@EnableGsp
class Application
{
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }







}

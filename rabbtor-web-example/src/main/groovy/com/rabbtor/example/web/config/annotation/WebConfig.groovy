package com.rabbtor.example.web.config.annotation

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.ViewResolver
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.thymeleaf.TemplateEngine
import org.thymeleaf.spring4.SpringTemplateEngine
import org.thymeleaf.spring4.view.ThymeleafViewResolver
import org.thymeleaf.templateresolver.ServletContextTemplateResolver
import org.thymeleaf.templateresolver.TemplateResolver

@Configuration
class WebConfig extends WebMvcConfigurerAdapter
{
//    @Autowired
//    ThymeleafProperties thymeleafProperties
//
//    @Bean
//    TemplateResolver templateResolver() {
//        ServletContextTemplateResolver resolver = new ServletContextTemplateResolver()
//        if (this.thymeleafProperties.getEncoding() != null) {
//            resolver.setCharacterEncoding(this.thymeleafProperties.getEncoding().name());
//        }
//        resolver.setCacheable(this.thymeleafProperties.isCache());
//        Integer order = this.thymeleafProperties.getTemplateResolverOrder();
//        if (order != null) {
//            resolver.setOrder(order);
//        }
//        resolver.templateMode = thymeleafProperties.mode
//
//
//        return resolver
//    }
//
//    @Bean
//    TemplateEngine templateEngine() {
//        SpringTemplateEngine engine = new SpringTemplateEngine()
//        engine.templateResolver = templateResolver()
//        return engine
//    }
//
//    @Bean
//    ViewResolver thymeleafViewResolver() {
//        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver()
//        viewResolver.templateEngine = templateEngine()
//        return viewResolver
//    }
//
//    @Override
//    void configureViewResolvers(ViewResolverRegistry registry)
//    {
//        registry.viewResolver(thymeleafViewResolver())
//    }
}

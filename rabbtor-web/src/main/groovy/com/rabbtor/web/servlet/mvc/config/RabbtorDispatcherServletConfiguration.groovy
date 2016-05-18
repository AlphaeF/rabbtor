package com.rabbtor.web.servlet.mvc.config

import com.rabbtor.web.servlet.RabbtorDispatcherServlet
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration
import org.springframework.boot.autoconfigure.web.WebMvcProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.DispatcherServlet

import javax.servlet.ServletRegistration

@CompileStatic
@Configuration
@ConditionalOnClass(ServletRegistration.class)
@EnableConfigurationProperties(WebMvcProperties.class)
class RabbtorDispatcherServletConfiguration
{
    @Autowired
    WebMvcProperties webMvcProperties

    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet() {
        DispatcherServlet dispatcherServlet = new RabbtorDispatcherServlet();
        dispatcherServlet.setDispatchOptionsRequest(
                this.webMvcProperties.isDispatchOptionsRequest());
        dispatcherServlet.setDispatchTraceRequest(
                this.webMvcProperties.isDispatchTraceRequest());
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(
                this.webMvcProperties.isThrowExceptionIfNoHandlerFound());
        return dispatcherServlet;
    }

}

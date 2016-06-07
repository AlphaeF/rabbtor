/**
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
//package com.rabbtor.example.web;
//
//import org.sitemesh.config.ConfigurableSiteMeshFilter;
//import org.springframework.boot.context.embedded.ServletContextInitializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.servlet.DispatcherType;
//import javax.servlet.FilterRegistration;
//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//import java.util.EnumSet;

//@Configuration
//public class SitemeshConfig
//{
//    @Bean
//    public SitemeshInitializer sitemeshInitializer() {
//        return new SitemeshInitializer();
//    }
//
//    private class SitemeshInitializer implements ServletContextInitializer {
//
//        @Override
//        public void onStartup(ServletContext servletContext) throws ServletException
//        {
//            ConfigurableSiteMeshFilter filter = new ConfigurableSiteMeshFilter();
//            FilterRegistration.Dynamic registration = servletContext.addFilter("sitemesh",filter);
//            registration.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class),true,"/*");
//
//        }
//    }
//}

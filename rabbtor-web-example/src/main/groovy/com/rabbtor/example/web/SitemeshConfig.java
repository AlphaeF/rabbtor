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

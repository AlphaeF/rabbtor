package com.rabbtor.web.servlet.mvc.config;


import com.rabbtor.gsp.GroovyPagesTemplateEngine;
import com.rabbtor.gsp.GspConfiguration;
import com.rabbtor.gsp.io.DefaultGroovyPageLocator;
import com.rabbtor.gsp.io.GroovyPageLocator;
import com.rabbtor.taglib.TagLibrariesBeanFactoryPostProcessor;
import com.rabbtor.taglib.TagLibraryLookup;
import com.rabbtor.web.servlet.gsp.GroovyPagesServlet;
import com.rabbtor.web.servlet.gsp.tags.ApplicationTagLib;
import com.rabbtor.web.servlet.view.GroovyPageViewResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

@Configuration
public class WebGspConfigurationSupport
{



    @Autowired(required = false)
    private GspConfiguration configuration;

    @Bean
    GroovyPagesServlet groovyPagesServlet() {
        GroovyPagesServlet servlet = new GroovyPagesServlet();
        servlet.setGroovyPagesTemplateEngine(groovyPagesTemplateEngine());
        return servlet;
    }

    @Bean
    GroovyPagesTemplateEngine groovyPagesTemplateEngine() {
        GroovyPagesTemplateEngine templateEngine = new GroovyPagesTemplateEngine();
        templateEngine.setTagLibraryLookup(gspTagLibraryLookup());
        templateEngine.setGroovyPageLocator(defaultGroovyPageLocator());

        GspConfiguration config = getConfiguration();
        templateEngine.setDevelopmentMode(configuration.isDevelopmentMode());
        templateEngine.setReloadEnabled(config.isReloadEnabled());
        templateEngine.setEncoding(config.getEncoding());
        templateEngine.setSitemeshPreprocessEnabled(config.isSitemeshPreprocessEnabled());

        return templateEngine;
    }

    @Bean
    TagLibraryLookup gspTagLibraryLookup() {
        return new TagLibraryLookup();
    }

    private GspConfiguration getConfiguration()
    {
        if (configuration == null)
            configuration = gspConfiguration();

        setConfigProperties(configuration);

        return configuration;
    }


    protected void setConfigProperties(GspConfiguration configuration)
    {


    }

    @Bean
    @ConditionalOnMissingBean(GspConfiguration.class)
    GspConfiguration gspConfiguration() {
        GspConfiguration config = new GspConfiguration();
        return config;
    }

    @Bean
    GroovyPageLocator defaultGroovyPageLocator() {
        DefaultGroovyPageLocator locator = new DefaultGroovyPageLocator();
        GspConfiguration config = getConfiguration();
        locator.setReloadEnabled(config.isReloadEnabled());
        locator.setTemplateRoots(config.getTemplateRoots());
        locator.setDevelopmentMode(config.isDevelopmentMode());

        return locator;
    }


    @Bean
    GspServletInitializer gspServletInitializer() {
        return new GspServletInitializer(groovyPagesServlet());
    }



    private class GspServletInitializer implements ServletContextInitializer
    {

        private GroovyPagesServlet servlet;

        public GspServletInitializer(GroovyPagesServlet servlet)
        {
            this.servlet = servlet;
        }

        @Override
        public void onStartup(ServletContext servletContext) throws ServletException
        {
            ServletRegistration.Dynamic gspServlet = servletContext.addServlet("groovyPagesServlet",servlet);
            gspServlet.setLoadOnStartup(1);
            gspServlet.addMapping("*.gsp");
        }
    }

    @Bean
    GroovyPageViewResolver groovyPageViewResolver() {
        GroovyPageViewResolver viewResolver = new GroovyPageViewResolver(groovyPagesTemplateEngine());
        return viewResolver;
    }

    /**
     * Tag lib beans must have bean name ending with "TagLib" to be recognized by the
     * {@link TagLibrariesBeanFactoryPostProcessor}
     * @return
     */
    @Bean
    ApplicationTagLib applicationGspTagLib() {
        return new ApplicationTagLib();
    }

    @Bean
    TagLibrariesBeanFactoryPostProcessor gspTagLibrariesBeanFactoryPostProcessor() {
        return new TagLibrariesBeanFactoryPostProcessor();
    }


}

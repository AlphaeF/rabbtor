package com.rabbtor.web.servlet.mvc.config;


import com.rabbtor.encoder.StandaloneCodecLookup;
import com.rabbtor.gsp.DefaultGspEnvironment;
import com.rabbtor.gsp.GroovyPagesTemplateEngine;
import com.rabbtor.gsp.GspConfiguration;
import com.rabbtor.gsp.GspEnvironment;
import com.rabbtor.gsp.io.DefaultGroovyPageLocator;
import com.rabbtor.gsp.io.GroovyPageLocator;
import com.rabbtor.taglib.TagLibrariesBeanFactoryPostProcessor;
import com.rabbtor.taglib.TagLibraryLookup;
import com.rabbtor.web.servlet.gsp.GroovyPagesServlet;
import com.rabbtor.web.servlet.gsp.tags.ApplicationTagLib;
import com.rabbtor.web.servlet.view.GroovyPageViewResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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

    @Autowired(required = false)
    private GspEnvironment gspEnvironment;

    @Bean
    @ConditionalOnMissingBean(GspEnvironment.class)
    GspEnvironment gspEnvironment() {
        return new DefaultGspEnvironment();
    }

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
        templateEngine.setReloadEnabled(config.isReloadEnabled());
        templateEngine.setEncoding(config.getEncoding());
        templateEngine.setSitemeshPreprocessEnabled(config.isSitemeshPreprocessEnabled());

        templateEngine.setDevelopmentMode(getGspEnvironment().isDevelopmentMode());


        return templateEngine;
    }

    @Bean
    TagLibraryLookup gspTagLibraryLookup() {
        return new TagLibraryLookup();
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
        locator.setDevelopmentMode(getGspEnvironment().isDevelopmentMode());

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

    @Bean
    StandaloneCodecLookup codecLookup() {
        StandaloneCodecLookup codecLookup = new StandaloneCodecLookup();
        registerCodecs(codecLookup);
        return codecLookup;
    }

    protected void registerCodecs(StandaloneCodecLookup codecLookup)
    {

    }


    private GspConfiguration getConfiguration()
    {
        if (configuration == null)
            configuration = gspConfiguration();

        setConfigProperties(configuration);

        return configuration;
    }

    private GspEnvironment getGspEnvironment() {
        if (gspEnvironment == null)
            gspEnvironment = gspEnvironment();
        return gspEnvironment;
    }
}


package com.rabbtor.gsp.boot;

import com.rabbtor.gsp.config.annotation.*;
import com.rabbtor.gsp.taglib.config.annotation.TagLibraryLookupRegistrar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.grails.web.servlet.view.GrailsLayoutViewResolver;
import org.grails.web.sitemesh.GroovyPageLayoutFinder;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ConditionalOnWebApplication
@ConditionalOnMissingBean({WebGspConfigurationSupport.class, GspConfigurationSupport.class})
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class WebGspAutoConfiguration
{

    @Configuration
    @Import({EnableWebGspConfiguration.class, TagLibraryLookupAutoConfiguration.class, GspJspConfiguration.class})
    @EnableConfigurationProperties({GspProperties.class})
    @Order(Ordered.LOWEST_PRECEDENCE - 30)
    public static class WebGspAutoConfigurationAdapter extends WebGspConfigurerAdapter
    {
        private static final Log logger = LogFactory
                .getLog(WebGspConfigurerAdapter.class);

        @Autowired
        private GspProperties gspProperties = new GspProperties();

        @Autowired
        private ListableBeanFactory beanFactory;

        @Override
        public void configureGsp(GspSettings config)
        {
            config.setTemplateRoots(gspProperties.getTemplateRoots());
            config.setGspReloadingEnabled(gspProperties.isGspReloadingEnabled());
            config.setGspLayoutCaching(gspProperties.isGspLayoutCaching());
            config.setDefaultLayoutName(gspProperties.getDefaultLayoutName());
            config.setLocatorCacheTimeout(gspProperties.getLocatorCacheTimeout());
            config.setViewCacheTimeout(gspProperties.getViewCacheTimeout());
        }

        @Override
        public void registerTldScanPaths(List<String> scanPaths)
        {
            if (gspProperties.tldScanPaths != null)
                scanPaths.addAll(Arrays.asList(gspProperties.tldScanPaths));
        }
    }

    /**
     * Configuration equivalent to {@code @EnableWebGsp}.
     */
    @Configuration
    public static class EnableWebGspConfiguration extends WebGspConfiguration
    {

        @Autowired(required = false)
        private GspProperties gspProperties;

        @Autowired
        private ListableBeanFactory beanFactory;

        @Override
        @Bean
        @ConditionalOnProperty("spring.gsp.enabled")
        public GrailsLayoutViewResolver gspViewResolver()
        {
            return super.gspViewResolver();
        }

        @Override
        @Bean
        @ConditionalOnProperty("spring.gsp.enabled")
        public GroovyPageLayoutFinder groovyPageLayoutFinder()
        {
            return super.groovyPageLayoutFinder();
        }


    }


    @Configuration
    @EnableConfigurationProperties({GspProperties.class})
    public static class TagLibraryLookupAutoConfiguration extends WebTagLibraryLookupRegistrar implements EnvironmentAware
    {

        private Environment environment;

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry)
        {
            String[] tagLibPackages = environment.getProperty("spring.gsp.taglib.packages", String[].class);
            String[] tagLibClassArray = environment.getProperty("spring.gsp.taglib.classes", String[].class);


            ManagedList<BeanDefinition> list = new ManagedList();
            Set<Class<?>> tagLibClasses = new HashSet();
            registerDefaultTagLibClasses(tagLibClasses,null,importingClassMetadata);

            if (tagLibClassArray != null)
            {
                for (String className : tagLibClassArray)
                {
                    try
                    {
                        tagLibClasses.add(Class.forName(className));
                    } catch (ClassNotFoundException e)
                    {
                        throw new RuntimeException("Unable to find tag library class: " + className, e);
                    }
                }
            }

            for (Class<?> clazz : tagLibClasses)
            {
                list.add(createBeanDefinition(clazz));
            }

            if (tagLibPackages != null)
            {
                Set<String> packagesToScan = new HashSet(Arrays.asList(tagLibPackages));
                scanPackages(packagesToScan, list);
            }

            createOrUpdateBeanDefinition(registry, list);

        }

        @Override
        public void setEnvironment(Environment environment)
        {
            this.environment = environment;
        }
    }


}

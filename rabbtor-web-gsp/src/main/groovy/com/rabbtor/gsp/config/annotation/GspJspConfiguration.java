package com.rabbtor.gsp.config.annotation;


import org.grails.gsp.jsp.TagLibraryResolver;
import org.grails.gsp.jsp.TagLibraryResolverImpl;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

@Configuration
@Conditional(JspCondition.class)
public class GspJspConfiguration implements EnvironmentAware
{
    @Bean(autowire = Autowire.BY_NAME)
    public TagLibraryResolverImpl jspTagLibraryResolver()
    {
        return new TagLibraryResolverImpl();
    }

    @Override
    public void setEnvironment(Environment environment)
    {
        if (environment instanceof ConfigurableEnvironment)
        {
            ConfigurableEnvironment configEnv = (ConfigurableEnvironment) environment;
            if (!environment.containsProperty("spring.gsp.tldScanPattern")) {

                Properties defaultProperties = createDefaultProperties();
                configEnv.getPropertySources().addLast(new PropertiesPropertySource(GspJspConfiguration.class.getName(), defaultProperties));
            }
        }
    }

    protected Properties createDefaultProperties()
    {
        Properties defaultProperties = new Properties();
        // scan for spring JSP taglib tld files by default, also scan for
        defaultProperties.put("spring.gsp.tldScanPattern",
                "classpath*:/META-INF/spring*.tld,classpath*:/META-INF/fmt.tld,classpath*:/META-INF/c.tld,classpath*:/META-INF/rabbtor*.tld,classpath*:/META-INF/c-1_0-rt.tld");
        return defaultProperties;
    }
}

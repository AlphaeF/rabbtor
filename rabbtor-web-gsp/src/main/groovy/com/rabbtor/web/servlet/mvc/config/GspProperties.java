package com.rabbtor.web.servlet.mvc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

@ConfigurationProperties(prefix = "spring.gsp",ignoreUnknownFields = true)
public class GspProperties implements EnvironmentAware,Ordered
{
    private Environment environment;

    @Override
    public void setEnvironment(Environment environment)
    {
        this.environment = environment;
    }

    @Override
    public int getOrder()
    {
        return 0;
    }
}

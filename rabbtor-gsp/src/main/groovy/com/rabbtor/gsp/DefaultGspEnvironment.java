package com.rabbtor.gsp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

public class DefaultGspEnvironment implements GspEnvironment
{
    private Boolean developmentMode;

    @Autowired
    Environment environment;

    @Value("${spring.gsp.devProfiles:'dev,development'}")
    String developmentProfileNames;



    @PostConstruct
    public void postConstruct() {
        if (developmentMode == null) {
            if (environment != null && developmentProfileNames != null)
                this.developmentMode = environment.acceptsProfiles(developmentProfileNames);
        }
    }


    public DefaultGspEnvironment(boolean developmentMode)
    {
        this.developmentMode = developmentMode;
    }

    public DefaultGspEnvironment()
    {
        this("dev,development");
    }


    public DefaultGspEnvironment(String developmentProfileNames)
    {
        Assert.hasText(developmentProfileNames);
        this.developmentProfileNames = developmentProfileNames;
    }

    @Override
    public boolean isDevelopmentMode()
    {
        return developmentMode;
    }
}

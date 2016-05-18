package com.rabbtor;


import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

public class DefaultRabbtorEnvironment implements RabbtorEnvironment
{

    private Environment environment;
    private String devProfileName;
    private String testProfileName;

    public DefaultRabbtorEnvironment(Environment environment)
    {
        this(environment,"development","test");
    }

    public DefaultRabbtorEnvironment(Environment environment, String devProfileName, String testProfileName)
    {
        Assert.notNull(environment,"environment must not be null.");
        if (devProfileName == null)
            devProfileName = "development";

        if (testProfileName == null)
            testProfileName = "test";

        this.devProfileName = devProfileName;
        this.testProfileName = testProfileName;

        this.environment = environment;
    }

    @Override
    public boolean isDevelopment()
    {
        return environment.acceptsProfiles(devProfileName);
    }

    @Override
    public boolean isTest()
    {
        return environment.acceptsProfiles(testProfileName);
    }
}

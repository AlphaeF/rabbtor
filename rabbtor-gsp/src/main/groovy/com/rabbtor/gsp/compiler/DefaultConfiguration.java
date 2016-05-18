package com.rabbtor.gsp.compiler;


import com.rabbtor.DefaultRabbtorEnvironment;
import com.rabbtor.RabbtorEnvironment;
import com.rabbtor.taglib.encoder.DefaultOutputEncodingSettings;
import com.rabbtor.taglib.encoder.OutputEncodingSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.net.URL;

public class DefaultConfiguration implements GspConfiguration
{
    @Value("gsp.sitemesh.preprocessing.enabled")
    private Boolean siteMeshPreprocessingEnabled;

    @Value("gsp.reload.enabled")
    private Boolean reloadEnabled;

    @Value("gsp.default.encoding")
    private String defaultEncoding;

    @Value("gsp.generatedDirectoryLocation")
    private String generatedDirectoryLocation;


    @Autowired(required = false)
    private RabbtorEnvironment environment;

    @Autowired
    private Environment springEnvironment;

    private Object lock = new Object();

    private OutputEncodingSettings outputEncodingSettings;


    @Override
    public boolean isSitemeshPreprocessingEnabled()
    {
        if (siteMeshPreprocessingEnabled != null)
            return siteMeshPreprocessingEnabled.booleanValue();

        return true;
    }

    @Override
    public RabbtorEnvironment getEnvironment()
    {
        if (environment == null)
            setEnvironment(environment = new RabbtorEnvironment()
            {
                @Override
                public boolean isDevelopment()
                {
                    return false;
                }

                @Override
                public boolean isTest()
                {
                    return false;
                }
            });
        return environment;
    }

    @Override
    public OutputEncodingSettings getOutputEncodingSettings()
    {
        synchronized (lock) {
            if (outputEncodingSettings == null) {
                outputEncodingSettings = new DefaultOutputEncodingSettings();
            }
        }
        return null;
    }

    @Override
    public String getDefaultEncoding()
    {
        return defaultEncoding == null ? "UTF-8" : defaultEncoding;
    }

    public void setDefaultEncoding(String defaultEncoding)
    {
        synchronized (lock)
        {
            this.defaultEncoding = defaultEncoding;
        }
    }

    @Override
    public String getGeneratedDirectoryLocation()
    {
        return generatedDirectoryLocation;
    }

    public void setGeneratedDirectoryLocation(String generatedDirectoryLocation)
    {
        synchronized (lock)
        {
            this.generatedDirectoryLocation = generatedDirectoryLocation;
        }
    }

    @Override
    public boolean isReloadEnabled()
    {
        synchronized (lock)
        {
            if (reloadEnabled == null)
            {
                setReloadEnabled(!isWarDeployed());
            }
        }
        return reloadEnabled.booleanValue();
    }

    private void setReloadEnabled(boolean value)
    {
        synchronized (lock) {
            this.reloadEnabled = value;
        }
    }

    private boolean isWarDeployed()
    {
        URL loadedLocation = getClass().getClassLoader().getResource("application.yml");
        if(loadedLocation != null && loadedLocation.getPath().contains("/WEB-INF/classes")) {
            return true;
        }
        return false;
    }

    public void setEnvironment(RabbtorEnvironment environment)
    {
        Assert.notNull(environment);
        synchronized (lock)
        {
            this.environment = environment;
        }
    }

    public void setSpringEnvironment(Environment springEnvironment)
    {
        synchronized (lock)
        {
            this.springEnvironment = springEnvironment;
            if (this.environment == null) {
                this.environment = new DefaultRabbtorEnvironment(springEnvironment);
            }
        }
    }
}

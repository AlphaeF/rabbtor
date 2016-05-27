package com.rabbtor.config;

import groovy.transform.CompileStatic;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import groovy.util.logging.Commons;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.LinkedHashMap;

@CompileStatic
@Order(Ordered.HIGHEST_PRECEDENCE + 50)
public class GroovyConfigPropertySourceLoader implements PropertySourceLoader
{
    private static Log log = LogFactory.getLog(GroovyConfigPropertySourceLoader.class);

    @Override
    public PropertySource<?> load(String name, Resource resource, String profile) throws IOException
    {

        ConfigSlurper configSlurper = profile != null ? new ConfigSlurper(profile) : new ConfigSlurper();

        if (resource.exists())
        {
            try
            {
                ConfigObject configObject = configSlurper.parse(resource.getURL());
                return new MapPropertySource(name, configObject.flatten());
            } catch (Throwable e)
            {
                log.error("Unable to load " + resource.getFilename() + ": " + e.getMessage(), e);
            }

        }

        return null;
    }

    public final String[] getFileExtensions()
    {
        return fileExtensions;
    }

    private static final String[] fileExtensions = new String[]{"groovy"};
}

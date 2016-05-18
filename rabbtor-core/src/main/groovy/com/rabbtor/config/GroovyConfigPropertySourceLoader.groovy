package com.rabbtor.config

import groovy.transform.CompileStatic
import groovy.util.logging.Commons
import org.springframework.boot.env.PropertySourceLoader
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.PropertySource
import org.springframework.core.io.Resource

@CompileStatic
@Commons
class GroovyConfigPropertySourceLoader implements PropertySourceLoader {

    final String[] fileExtensions = ['groovy'] as String[]

    @Override
    PropertySource<?> load(String name, Resource resource, String profile) throws IOException {

        ConfigSlurper configSlurper = profile ? new ConfigSlurper(profile) : new ConfigSlurper()

        configSlurper.setBinding(userHome: System.getProperty('user.home'))

        if(resource.exists()) {
            try {
                def configObject = configSlurper.parse(resource.URL)
                return new MapPropertySource(name, configObject.flatten())
            } catch (Throwable e) {
                log.error("Unable to load $resource.filename: $e.message", e)
            }
        }
        return null
    }
}
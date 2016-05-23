package com.rabbtor.gsp.config.annotation;


import grails.core.StandaloneGrailsApplication;
import org.grails.encoder.CodecLookup;
import org.grails.encoder.impl.StandaloneCodecLookup;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

public abstract class GrailsApplicationConfigurationSupport
{

    @Bean
    SpringBootGrailsApplication grailsApplication() {
        return new SpringBootGrailsApplication();
    }

    @Bean
    CodecLookup codecLookup() {
        return new StandaloneCodecLookup();
    }

    /**
     * Makes Spring Boot application properties available in the GrailsApplication instance's flatConfig
     *
     */
    public static class SpringBootGrailsApplication extends StandaloneGrailsApplication implements EnvironmentAware
    {
        private Environment environment;

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public void updateFlatConfig() {
            super.updateFlatConfig();
            if(this.environment instanceof ConfigurableEnvironment) {
                ConfigurableEnvironment configurableEnv = ((ConfigurableEnvironment)environment);
                for(PropertySource<?> propertySource : configurableEnv.getPropertySources()) {
                    if(propertySource instanceof EnumerablePropertySource) {
                        EnumerablePropertySource<?> enumerablePropertySource = (EnumerablePropertySource)propertySource;
                        for(String propertyName : enumerablePropertySource.getPropertyNames()) {
                            flatConfig.put(propertyName, enumerablePropertySource.getProperty(propertyName));
                        }
                    }
                }
            }
        }

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
            updateFlatConfig();
        }
    }
}

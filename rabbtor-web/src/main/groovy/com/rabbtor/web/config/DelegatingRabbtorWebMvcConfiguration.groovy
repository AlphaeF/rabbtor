package com.rabbtor.web.config


import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

@CompileStatic
@Configuration
class DelegatingRabbtorWebMvcConfiguration extends DelegatingWebMvcConfiguration
{

    private final RabbtorWebMvcConfigurerComposite routeConfigurers = new RabbtorWebMvcConfigurerComposite();

    @Autowired
    ConfigurableBeanFactory beanFactory

    @Autowired(required = false)
    public void setRouteConfigurers(List<RabbtorWebMvcConfigurer> configurers) {
        if (configurers == null || configurers.isEmpty()) {
            return;
        }
        this.routeConfigurers.addWebMvcConfigurers(configurers);
    }

    @Override
    @Bean
    RequestMappingHandlerMapping requestMappingHandlerMapping() {
        return super.requestMappingHandlerMapping()
    }


}

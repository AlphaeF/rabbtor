package com.rabbtor.web.servlet.mvc.method

import com.rabbtor.web.servlet.handler.ActionMethodNamingStrategy
import groovy.transform.CompileStatic
import org.springframework.util.ReflectionUtils
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

import java.lang.reflect.Method

@CompileStatic
class RabbtorRequestMappingHandlerMapping extends RequestMappingHandlerMapping
{
    RabbtorRequestMappingHandlerMapping()
    {
        //ReflectionUtils.setField(ReflectionUtils.getField("mappingRegistry"),this,null)
    }

    @Override
    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        super.registerHandlerMethod(handler, method, mapping)
    }
}

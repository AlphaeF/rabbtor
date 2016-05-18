package com.rabbtor.web.servlet.mvc.method

import com.rabbtor.web.servlet.mvc.util.MvcUtils

import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

import javax.servlet.http.HttpServletRequest
import java.lang.reflect.Method

@CompileStatic
@InheritConstructors
class RabbtorRequestMappingHandlerMapping extends RequestMappingHandlerMapping
{


    @Override
    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
        super.registerHandlerMethod(handler, method, mapping)
    }

    @Override
    protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
        HandlerMethod method =  super.getHandlerInternal(request)
        if (method) {
            MvcUtils.retrieveHandlerContext().handlerMethod = method
        }
    }

    @Override
    protected void handleMatch(RequestMappingInfo info, String lookupPath, HttpServletRequest request) {
        super.handleMatch(info, lookupPath, request)
        MvcUtils.beginHandlerContext(info)

    }
}

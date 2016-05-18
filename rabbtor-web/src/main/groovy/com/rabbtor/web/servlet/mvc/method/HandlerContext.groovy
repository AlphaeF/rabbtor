package com.rabbtor.web.servlet.mvc.method

import groovy.transform.CompileStatic
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo

import javax.servlet.http.HttpServletRequest

@CompileStatic
class HandlerContext
{

    final HttpServletRequest request
    private RouteValuesMap routeValues

    HandlerContext(HttpServletRequest request)
    {
        this.request = request
    }

    RequestMappingInfo mappingInfo

    HandlerMethod handlerMethod

    HandlerContext parentContext

    RouteValuesMap getRouteValues()
    {
        if (routeValues == null)
            resetRouteValues()
        return this.@routeValues

    }

    public void resetRouteValues()
    {
        this.@routeValues = new RouteValuesMap(request)
    }
}

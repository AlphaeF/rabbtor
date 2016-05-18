package com.rabbtor.web.servlet.mvc.util

import com.rabbtor.web.servlet.mvc.method.HandlerContext
import groovy.transform.CompileStatic
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.servlet.mvc.method.RequestMappingInfo

import javax.servlet.http.HttpServletRequest

@CompileStatic
class MvcUtils
{

    public static HandlerContext retrieveHandlerContext()
    {
        return (HandlerContext) RequestContextHolder.currentRequestAttributes().getAttribute(HandlerContext.name, RequestAttributes.SCOPE_REQUEST)
    }

    public static HandlerContext beginHandlerContext(RequestMappingInfo info) {
        def existing = retrieveHandlerContext()
        def newContext = new HandlerContext((HttpServletRequest)RequestContextHolder.currentRequestAttributes().resolveReference('request'))
        newContext.mappingInfo = info

        if (existing)
            newContext.parentContext = existing

        storeHandlerContext(newContext)
        return newContext
    }

    static def storeHandlerContext(HandlerContext handlerContext)
    {
        assert handlerContext
        RequestContextHolder.currentRequestAttributes().setAttribute(HandlerContext.name,handlerContext,RequestAttributes.SCOPE_REQUEST)
    }

    static void clearHandlerContext() {
        RequestContextHolder.currentRequestAttributes().removeAttribute(HandlerContext.name,RequestAttributes.SCOPE_REQUEST)
    }


}

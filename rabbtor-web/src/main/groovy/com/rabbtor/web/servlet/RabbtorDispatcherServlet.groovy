package com.rabbtor.web.servlet

import com.rabbtor.web.servlet.mvc.RabbtorWebRequest
import groovy.transform.CompileStatic
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.DispatcherServlet

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@CompileStatic
class RabbtorDispatcherServlet extends DispatcherServlet
{

    @Override
    protected ServletRequestAttributes buildRequestAttributes(HttpServletRequest request, HttpServletResponse response, RequestAttributes previousAttributes)
    {
        if (previousAttributes == null || !(previousAttributes instanceof RabbtorWebRequest)) {
            def webRequest = new RabbtorWebRequest(request, response, getServletContext(), getWebApplicationContext())
            return webRequest;
        }
        else {
            return (RabbtorWebRequest) previousAttributes;
        }
    }
}

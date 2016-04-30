package com.rabbtor.web.servlet.mvc.method

import com.rabbtor.web.servlet.util.IncludeRequestWrapper
import com.rabbtor.web.servlet.util.IncludeResponseWrapper
import groovy.transform.CompileStatic
import org.springframework.web.method.HandlerMethod
import org.springframework.web.util.HtmlUtils

import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import javax.servlet.http.HttpServletResponse
import java.lang.reflect.Array

@CompileStatic
class HandlerMethodIncludeHelper
{
    String include(String url, HttpServletRequest request, HttpServletResponse response, Map<String,String[]> params) {
        RequestDispatcher dispatcher = request.getRequestDispatcher(url)
        def wrappedResponse = new IncludeResponseWrapper(response)
        def wrappedRequest = new IncludeRequestWrapper(request,params)

        dispatcher.include(wrappedRequest,wrappedResponse)
        wrappedResponse.flushBuffer()
        wrappedResponse.getContent(response.getCharacterEncoding()).toString()
    }

}

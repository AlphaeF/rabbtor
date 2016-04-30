package com.rabbtor.web.servlet.util

import groovy.transform.CompileStatic
import org.apache.catalina.util.ParameterMap

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper


@CompileStatic
class IncludeRequestWrapper extends HttpServletRequestWrapper
{

    private ParameterMap<String,String[]> parameterMap = new ParameterMap<>()

    IncludeRequestWrapper(HttpServletRequest request, Map<String,String[]> parameterMap)
    {
        super(request)
        if (parameterMap != null)
            this.parameterMap.putAll(parameterMap)

        this.parameterMap.setLocked(true)

    }

    @Override
    Map<String, String[]> getParameterMap() {
        return super.getParameterMap()
    }

    @Override
    String getParameter(String name)
    {
        def vals = getParameterValues(name)
        return vals == null ? null : vals[0]
    }

    @Override
    String[] getParameterValues(String name)
    {
        return parameterMap[name]
    }
}

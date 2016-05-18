package com.rabbtor.web.servlet.mvc.method

import com.rabbtor.commons.collections.CaseInsensitiveHashMap
import com.rabbtor.commons.collections.CaseInsensitiveLinkedHashMap
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.springframework.util.MultiValueMap
import org.springframework.web.servlet.HandlerMapping

import javax.servlet.http.HttpServletRequest
import java.lang.reflect.Array

@CompileStatic
class RouteValuesMap
{
    private Map<String, Object> requestParams
    private Map<String, Object> pathVariables
    private Map<String, Object> matrixVariables
    private Map<String, Object> customParams
    private Map<String, Object> allParams

    private HttpServletRequest request

    RouteValuesMap(HttpServletRequest request)
    {
        this.@request = request
        this.@requestParams = new CaseInsensitiveLinkedHashMap<>(request.getParameterMap().size(), Locale.US)
        request.getParameterMap().each {
            add(this.@requestParams, it.key, it.value)
        }

        def pathVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)
        if (pathVars)
        {
            this.@pathVariables = new CaseInsensitiveLinkedHashMap<>(pathVars.size(), Locale.US)
            pathVars.each {
                add(this.@pathVariables, it.key, it.value)
            }
        }

        allParams = new CaseInsensitiveLinkedHashMap<>()

        this.@pathVariables?.each {
            allParams[it.key] = it.value
        }

        this.@requestParams?.each {
            allParams[it.key] = it.value
        }

    }

    private void add(Map<String, Object> map, String name, Object value)
    {
        if (value != null)
        {
            def valueAsCollection = valueAsCollection(value)
            if (valueAsCollection.size() == 1)
                map[name] = valueAsCollection[0]
            else if (valueAsCollection.size() > 1)
                value.eachWithIndex { Object entry, int i ->
                    if (entry != null)
                    {
                        map["${name}[$i]".toString()] = entry
                    }
                }
        }
    }

    Collection<?> valueAsCollection(Object value)
    {
        if (value == null)
            return null

        if (value instanceof Object[])
            return (value as Object[]).toList()
        else if (value instanceof Collection<?>)
            return (value as Collection<?>).toList()

        return [value]

    }

    boolean isMultipleValue(Object value)
    {
        if (value == null)
            return false

        return value.getClass().isArray() || value instanceof Collection<?> || value instanceof List<?> ||
                value instanceof Set<?> || value instanceof Map<?, ?>
    }

    public Object get(String parameterName)
    {
        return allParams[parameterName]
    }

    public Object getRequestParam(String parameterName)
    {
        this.@requestParams ? this.@requestParams[parameterName] : null
    }

    private Object getMatrixVariable(String parameterName)
    {
        this.@matrixVariables ? this.@matrixVariables[parameterName] : null
    }

    public Object getCustomParam(String parameterName)
    {
        this.@customParams ? this.@customParams[parameterName] : null
    }

    public Object getPathVariable(String parameterName)
    {
        this.@pathVariables ? this.@pathVariables[parameterName] : null
    }

    public void setCustomParam(String parameterName, Object value)
    {
        ensureCustomParams()
        if (value == null)
            removeCustomParam(parameterName)
        add(this.@customParams, parameterName, value)
        this.allParams[parameterName] = value
    }

    private void ensureCustomParams()
    {
        if (this.@customParams == null)
            this.@customParams = new CaseInsensitiveLinkedHashMap<>(Locale.US)
    }

    public Object removeCustomParam(String parameterName)
    {
        this.@customParams?.remove(parameterName)
    }

    public String[] getParameterNames()
    {
        return allParams.keySet().toArray()
    }


}

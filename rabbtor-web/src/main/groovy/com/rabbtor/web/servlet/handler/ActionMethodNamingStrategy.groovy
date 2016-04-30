package com.rabbtor.web.servlet.handler

import groovy.transform.CompileStatic
import org.springframework.util.StringUtils
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.handler.HandlerMethodMappingNamingStrategy
import org.springframework.web.servlet.mvc.method.RequestMappingInfo

@CompileStatic
class ActionMethodNamingStrategy
        implements HandlerMethodMappingNamingStrategy<RequestMappingInfo>
{
    private String separator = '#'

    String getSeparator()
    {
        return separator
    }

    void setSeparator(String separator)
    {
        assert separator, 'separator must not be null or empty'
        this.separator = separator
    }

    @Override
    String getName(HandlerMethod handlerMethod, RequestMappingInfo mapping)
    {
        if (mapping.name)
            return mapping.name

        StringBuilder sb = new StringBuilder();
        String simpleTypeName = handlerMethod.getBeanType().getSimpleName();

        if (StringUtils.endsWithIgnoreCase(simpleTypeName,'Controller'))
            sb.append(simpleTypeName.substring(simpleTypeName.length()-'Controller'.length()))
        else
        for (int i = 0 ; i < simpleTypeName.length(); i++) {
            if (Character.isUpperCase(simpleTypeName.charAt(i))) {
                sb.append(simpleTypeName.charAt(i));
            }
        }
        sb.append(separator).append(handlerMethod.getMethod().getName());
        return sb.toString();
    }
}

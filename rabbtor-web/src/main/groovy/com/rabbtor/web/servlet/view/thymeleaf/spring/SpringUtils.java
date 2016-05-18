package com.rabbtor.web.servlet.view.thymeleaf.spring;


import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.spring4.naming.SpringContextVariableNames;

import javax.servlet.http.HttpServletRequest;

public class SpringUtils
{
    public static RequestContext getRequestContext(IContext context)
    {
        return (RequestContext) context.getVariable(SpringContextVariableNames.SPRING_REQUEST_CONTEXT);
    }

    public static ApplicationContext getApplicationContext(IContext context)
    {
        return getApplicationContext(context, false);
    }

    public static ApplicationContext getRequiredApplicationContext(IContext context)
    {
        return getApplicationContext(context, true);
    }


    private static ApplicationContext getApplicationContext(IContext context, boolean required)
    {
        RequestContext requestContext = getRequestContext(context);
        if (requestContext != null)
            return requestContext.getWebApplicationContext();

        if (!(context instanceof IWebContext))
        {
            if (required)
                throw new IllegalStateException("No application context bound to current context.");
            else
                return null;
        }
        IWebContext webContext = ((IWebContext) context);
        if (required)
            return WebApplicationContextUtils.getRequiredWebApplicationContext(webContext.getServletContext());
        else
            return WebApplicationContextUtils.getWebApplicationContext(webContext.getServletContext());
    }

    public static <T> T getBean(IContext context,Class<T> beanClass) {
        return getRequiredApplicationContext(context).getBean(beanClass);
    }

    public static <T> T getBean(IContext context,Class<T> beanClass,String beanName) {
        return getRequiredApplicationContext(context).getBean(beanName,beanClass);
    }
}

package com.rabbtor.web.servlet.util;


import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class RequestContextUtils
{
    public static Object getModelObject(RequestContext requestContext, HttpServletRequest request,String beanName) {
        Errors e = requestContext.getErrors(beanName);
        if (e != null && e instanceof BindingResult) {
            return ((BindingResult)e).getTarget();
        }

        Map<String,Object> model = requestContext.getModel();
        if (model.containsKey(beanName))
            return model.get(beanName);

        return null;

    }
}

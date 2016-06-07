/**
 * Copyright 2016 - Rabbytes Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Rabbytes Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Rabbytes Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Rabbytes Incorporated.
 */
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

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
package com.rabbtor.web.servlet.jsp.tags;


import com.rabbtor.model.*;
import com.rabbtor.web.servlet.jsp.functions.FormFunctions;
import com.rabbtor.web.servlet.util.BindStatusUtils;
import com.rabbtor.web.servlet.util.RequestContextUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.tags.form.AbstractHtmlElementTag;
import org.springframework.web.servlet.tags.form.TagWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

public class LabelTag extends AbstractHtmlElementTag
{



    @Override
    protected int writeTagContent(TagWriter tagWriter) throws JspException
    {

        tagWriter.startTag("label");
        writeDefaultAttributes(tagWriter);
        tagWriter.appendValue(getDisplayString(FormFunctions.propertyDisplayName(getPath(),pageContext)));
        tagWriter.endTag();
        return SKIP_BODY;
    }

    protected Object getModelObject() throws JspException
    {
        String beanName = BindStatusUtils.getBeanName(getBindStatus());

        Object modelObject = RequestContextUtils.getModelObject(getRequestContext(),(HttpServletRequest)pageContext.getRequest(),beanName);

        if (modelObject == null) {
            throw new IllegalStateException("Neither BindingResult nor plain target object for bean name '" +
                    beanName + "' available as request attribute");
        }

        return modelObject;

    }


    @Override
    public String getId()
    {
        String id = super.getId();
        if (id != null)
            id  =  "label." + id;
        return id;
    }

    @Override
    protected String getName() throws JspException
    {
        return null;
    }
}

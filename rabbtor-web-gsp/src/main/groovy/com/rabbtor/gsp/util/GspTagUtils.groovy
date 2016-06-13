/*
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

package com.rabbtor.gsp.util

import com.rabbtor.model.ModelMetadataAccessor
import com.rabbtor.model.ModelMetadataAccessorUtils
import groovy.transform.CompileStatic
import org.springframework.context.ApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils
import org.springframework.web.servlet.support.BindStatus

import javax.servlet.http.HttpServletRequest;

@CompileStatic
public class GspTagUtils
{
    public static String HTML_ESCAPE_ATTR_NAME = 'htmlEscape';


}

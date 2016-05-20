/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rabbtor.web.servlet.support


import groovy.transform.CompileStatic
import org.springframework.context.ApplicationContext
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.context.support.WebApplicationContextUtils

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

/**
 *
 * Inspired by the Grails framework. https://grails.org/
 * Common web attributes inherited by all controllers and tag libraries
 *
 * @author Jeff Brown
 * @author Graeme Rocher
 * @author Cagatay Kalan
 *
 * @since 3.0
 *
 */
@CompileStatic
trait WebAttributes {

    ServletRequestAttributes currentRequestAttributes() {
        return (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()
    }

    private ServletContext servletContext
    private ApplicationContext applicationContext

    HttpServletRequest getRequest() {
        currentRequestAttributes().getRequest()
    }

    HttpSession getSession() {
        return getRequest().getSession()
    }

    /**
     * Obtains the ApplicationContext instance
     * @return The ApplicationContext instance
     */
    ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            this.applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        }
        this.applicationContext
    }

    /**
     * Obtains the HttpServletResponse instance
     *
     * @return The HttpServletResponse instance
     */
    HttpServletResponse getResponse() {
        currentRequestAttributes().getResponse()
    }

    /**
     * Obtains the ServletContext instance
     *
     * @return The ServletContext instance
     */
    ServletContext getServletContext() {
        if (servletContext == null) {
            servletContext = getRequest().getServletContext()
        }
        servletContext
    }

}

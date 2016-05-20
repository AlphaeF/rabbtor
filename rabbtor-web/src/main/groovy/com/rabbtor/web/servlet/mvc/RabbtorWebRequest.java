package com.rabbtor.web.servlet.mvc;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.handler.DispatcherServletWebRequest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;


public class RabbtorWebRequest extends DispatcherServletWebRequest
{

    public static final String WEB_REQUEST = "com.rabbtor.WEB_REQUEST";
    public static final String WRITER = WEB_REQUEST +".WRITER";
    private ApplicationContext applicationContext;
    private ServletContext servletContext;


    public RabbtorWebRequest(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        super(request, response);
        this.applicationContext = WebApplicationContextUtils.findWebApplicationContext(servletContext);
        this.servletContext= servletContext;
    }


    public RabbtorWebRequest(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, ApplicationContext applicationContext) {
        this(request, response, servletContext);
        this.applicationContext = applicationContext;
    }

    public static RabbtorWebRequest lookup(HttpServletRequest request)
    {
        RabbtorWebRequest webRequest = (RabbtorWebRequest) request.getAttribute(RabbtorWebRequest.WEB_REQUEST);
        return webRequest == null ? lookup() : webRequest;
    }

    /**
     * Looks up the current Grails WebRequest instance
     * @return The GrailsWebRequest instance
     */
    public static RabbtorWebRequest lookup() {
        RabbtorWebRequest webRequest = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof RabbtorWebRequest) {
            webRequest = (RabbtorWebRequest) requestAttributes;
        }
        return webRequest;
    }

    /**
     * @return the out
     */
    public Writer getOut() {
        Writer out = (Writer) getAttribute(WRITER,SCOPE_REQUEST);
        if (out == null) {
            try {
                return getResponse().getWriter();
            } catch (IOException e) {
                throw new RuntimeException("Error retrieving response writer: " + e.getMessage(), e);
            }
        }
        return out;
    }

    /**
     * Whether the web request is still active
     * @return true if it is
     */
    public boolean isActive() {
        return super.isRequestActive();
    }

    /**
     * @param out the out to set
     */
    public void setOut(Writer out) {
        setAttribute(WRITER, out, SCOPE_REQUEST);
    }

    /**
     * @return The ServletContext instance
     */
    public ServletContext getServletContext() {
        return this.servletContext;
    }


    public ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }
}

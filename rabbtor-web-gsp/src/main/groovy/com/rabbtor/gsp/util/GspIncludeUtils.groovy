package com.rabbtor.gsp.util

import com.rabbtor.web.servlet.support.DefaultIncludeResult
import com.rabbtor.web.servlet.support.IncludeException
import com.rabbtor.web.servlet.support.IncludeResult
import com.rabbtor.web.servlet.support.RequestIncludeHelper
import com.rabbtor.web.servlet.support.RequestIncludeWrapper
import com.rabbtor.web.servlet.support.RequestParams
import com.rabbtor.web.servlet.support.ResponseIncludeWrapper
import groovy.transform.CompileStatic
import org.grails.web.servlet.WrappedResponseHolder
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.servlet.mvc.exceptions.ControllerExecutionException
import org.grails.web.sitemesh.GrailsContentBufferingResponse
import org.grails.web.util.GrailsApplicationAttributes
import org.grails.web.util.IncludeResponseWrapper
import org.grails.web.util.IncludedContent
import org.grails.web.util.WebUtils
import org.springframework.core.convert.ConversionService
import org.springframework.web.context.request.WebRequest
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.ModelAndView

import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@CompileStatic
class GspIncludeUtils
{
    public static final String MATCHED_REQUEST = "org.grails.url.match.info"

    public static IncludeResult include(String path, RequestIncludeWrapper request, HttpServletResponse response, Map model) {
        String includeUrl = path;

        final GrailsWebRequest webRequest = GrailsWebRequest.lookup(request);

        String currentController = null;
        String currentAction = null;
        String currentId = null;
        ModelAndView currentMv = null;
        Binding currentPageBinding = null;
        Map currentParams = null;
        Object currentLayoutAttribute = null;
        Object currentRenderingView = null;
        if (webRequest != null) {
            currentPageBinding = (Binding) webRequest.getAttribute(GrailsApplicationAttributes.PAGE_SCOPE, 0);
            webRequest.removeAttribute(GrailsApplicationAttributes.PAGE_SCOPE, 0);
            currentLayoutAttribute = webRequest.getAttribute(WebUtils.LAYOUT_ATTRIBUTE, 0);
            if (currentLayoutAttribute != null) {
                webRequest.removeAttribute(WebUtils.LAYOUT_ATTRIBUTE, 0);
            }
            currentRenderingView = webRequest.getAttribute(WebUtils.RENDERING_VIEW, 0);
            if (currentRenderingView != null) {
                webRequest.removeAttribute(WebUtils.RENDERING_VIEW, 0);
            }
            currentController = webRequest.getControllerName();
            currentAction = webRequest.getActionName();
            currentId = webRequest.getId();
            currentParams = new HashMap();
            currentParams.putAll(webRequest.getParameterMap());
            currentMv = (ModelAndView)webRequest.getAttribute(GrailsApplicationAttributes.MODEL_AND_VIEW, 0);
        }
        try {
            if (webRequest!=null) {
                webRequest.getParameterMap().clear();
                webRequest.getParameterMap().putAll(request.parameterMap);
                webRequest.removeAttribute(GrailsApplicationAttributes.MODEL_AND_VIEW, 0);
            }
            return includeForUrl(includeUrl, request, response, model);
        }
        finally {
            if (webRequest!=null) {
                webRequest.setAttribute(GrailsApplicationAttributes.PAGE_SCOPE,currentPageBinding, 0);
                if (currentLayoutAttribute != null) {
                    webRequest.setAttribute(WebUtils.LAYOUT_ATTRIBUTE, currentLayoutAttribute, 0);
                }
                if (currentRenderingView != null) {
                    webRequest.setAttribute(WebUtils.RENDERING_VIEW, currentRenderingView, 0);
                }
                webRequest.getParameterMap().clear();
                webRequest.getParameterMap().putAll(currentParams);
                webRequest.setId(currentId);
                webRequest.setControllerName(currentController);
                webRequest.setActionName(currentAction);
                if (currentMv != null) {
                    webRequest.setAttribute(GrailsApplicationAttributes.MODEL_AND_VIEW, currentMv, 0);
                }
            }
        }
    }

    /**
     * Includes the given URL returning the resulting content as a String
     *
     * @param includeUrl The URL to include
     * @param request The request
     * @param response The response
     * @param model The model
     * @return The content
     */
    @SuppressWarnings([ "unchecked", "rawtypes" ])
    public static IncludeResult includeForUrl(String includeUrl, RequestIncludeWrapper request,
                                              HttpServletResponse response, Map model) {

        HttpServletResponse wrapped = WrappedResponseHolder.getWrappedResponse();
        response = wrapped != null ? wrapped : response;

        WebUtils.exposeIncludeRequestAttributes(request);

        Map toRestore = WebUtils.exposeRequestAttributesAndReturnOldValues(request, model);

        final GrailsWebRequest webRequest = GrailsWebRequest.lookup(request);
        if (webRequest == null)
            webRequest = new GrailsWebRequest(request,response,request.getServletContext())

        final Object previousControllerClass
        final Object previousMatchedRequest

        if (webRequest != null) {
            previousControllerClass = webRequest.getAttribute(GrailsApplicationAttributes.GRAILS_CONTROLLER_CLASS_AVAILABLE, WebRequest.SCOPE_REQUEST);
            previousMatchedRequest = webRequest.getAttribute(MATCHED_REQUEST, WebRequest.SCOPE_REQUEST);
        }


        try {
            webRequest.removeAttribute(GrailsApplicationAttributes.GRAILS_CONTROLLER_CLASS_AVAILABLE, WebRequest.SCOPE_REQUEST);
            webRequest.removeAttribute(MATCHED_REQUEST, WebRequest.SCOPE_REQUEST);
            webRequest.removeAttribute("grailsWebRequestFilter" + OncePerRequestFilter.ALREADY_FILTERED_SUFFIX, WebRequest.SCOPE_REQUEST);


            final ResponseIncludeWrapper responseWrapper = new ResponseIncludeWrapper(response);
            try {
                WrappedResponseHolder.setWrappedResponse(responseWrapper);
                RequestDispatcher dispatcher = request.getRequest().getRequestDispatcher(includeUrl);
                dispatcher.include(request,responseWrapper)
                return new DefaultIncludeResult(responseWrapper);
            }
            finally {
                webRequest.setAttribute(GrailsApplicationAttributes.GRAILS_CONTROLLER_CLASS_AVAILABLE, previousControllerClass,WebRequest.SCOPE_REQUEST);
                webRequest.setAttribute(MATCHED_REQUEST,previousMatchedRequest, WebRequest.SCOPE_REQUEST);

                WrappedResponseHolder.setWrappedResponse(wrapped);
            }
        }
        catch (Exception e) {
            throw new IncludeException("Unable to execute include: " + e.getMessage(), e);
        }
        finally {
            WebUtils.cleanupIncludeRequestAttributes(request, toRestore);
        }
    }
}

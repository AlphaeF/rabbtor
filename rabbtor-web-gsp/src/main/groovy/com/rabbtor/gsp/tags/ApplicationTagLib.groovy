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
/* Modifications copyright (C) 2016 Rabbytes Incorporated */
package com.rabbtor.gsp.tags

import com.rabbtor.gsp.taglib.TagLibraryExt
import com.rabbtor.gsp.util.GspIncludeUtils
import com.rabbtor.web.servlet.support.IncludeStatusException
import com.rabbtor.web.servlet.support.RequestIncludeWrapper
import com.rabbtor.web.servlet.support.RequestParams
import com.rabbtor.web.servlet.util.UrlType
import com.rabbtor.web.servlet.util.UrlUtils
import grails.config.Settings
import grails.core.GrailsApplication
import grails.core.support.GrailsApplicationAware
import grails.gsp.TagLib
import groovy.transform.CompileStatic
import org.grails.encoder.Encoder
import org.grails.taglib.GrailsTagException
import org.grails.taglib.GroovyPageAttributes
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.*
import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.core.convert.ConversionService
import org.springframework.util.StringUtils
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import org.springframework.web.util.JavaScriptUtils
import org.springframework.web.util.UriUtils

/**
 * Implementation of common GSP tags.
 */
@TagLib
class ApplicationTagLib implements
        ApplicationContextAware,
        InitializingBean,
        GrailsApplicationAware,
        TagLibraryExt
{



    static returnObjectForTags = ['set', 'mvcUrl', 'mvcPath', 'message','url']
    //static encodeAsForTags = ['include', 'none']


    ApplicationContext applicationContext
    GrailsApplication grailsApplication

    @Autowired(required = false)
    ConversionService conversionService

    MessageSource messageSource

    static final SCOPES = [page       : 'pageScope',
                           application: 'servletContext',
                           request    : 'request',
                           session    : 'session',
                           flash      : 'flash']

    boolean useJsessionId = false
    boolean hasResourceProcessor = false




    void afterPropertiesSet()
    {
        def config = grailsApplication.config

        useJsessionId = config.getProperty(Settings.GRAILS_VIEWS_ENABLE_JSESSIONID, Boolean, false)
        hasResourceProcessor = applicationContext.containsBean('grailsResourceProcessor')


    }
/**
 * Obtains the value of a cookie.
 *
 * @emptyTag
 *
 * @attr name REQUIRED the cookie name
 */
    Closure cookie = { attrs ->
        request.cookies.find { it.name == attrs.name }?.value
    }

    /**
     * Renders the specified request header value.
     *
     * @emptyTag
     *
     * @attr name REQUIRED the header name
     */
    Closure header = { attrs ->
        attrs.name ? request.getHeader(attrs.name) : null
    }

    /**
     * Sets a variable in the pageContext or the specified scope.
     * The value can be specified directly or can be a bean retrieved from the applicationContext.
     *
     * @attr var REQUIRED the variable name
     * @attr value the variable value; if not specified uses the rendered body
     * @attr bean the name or the type of a bean in the applicationContext; the type can be an interface or superclass
     * @attr scope the scope name; defaults to pageScope
     */
    Closure set = { attrs, body ->
        def var = attrs.var
        if (!var) throw new IllegalArgumentException("[var] attribute must be specified to for <g:set>!")

        def scope = attrs.scope ? SCOPES[attrs.scope] : 'pageScope'
        if (!scope) throw new IllegalArgumentException("Invalid [scope] attribute for tag <g:set>!")

        def value
        if (attrs.bean)
        {
            value = applicationContext.getBean(attrs.bean)
        } else
        {
            value = attrs.value
            def containsValue = attrs.containsKey('value')
            if (!containsValue && body) value = body()
        }

        this."$scope"."$var" = value
        null
    }

    /**
     * Server side include similar to {@code <jsp:include />}
     */
    Closure include = { attrs, body ->
        def path = attrs.path;
        def mapping = attrs.mapping

        if (!path && !mapping) throw new IllegalArgumentException("One of [path] or [mapping] attributes must be specified for <g:include>!")

        if (mapping)
        {
            path = mvcPath(attrs)
        }


        def requestParams = attrs.params ?: new RequestParams();
        if (requestParams)
        {
            if (!(requestParams instanceof RequestParams))
            {
                if (!(requestParams instanceof Map)) throw new IllegalArgumentException("[params] attribute must be a Map for <g:include>!")
                requestParams = new RequestParams(requestParams)
            }
        }

        def includeRequestParams = (Boolean) attrs.includeRequestParams

        def wrappedRequest = new RequestIncludeWrapper(request, requestParams.asRequestParameterMap(conversionService), (boolean) includeRequestParams)


        def includeResult = GspIncludeUtils.include(path, wrappedRequest, response, [:])
        def var = attrs.var;




        if (includeResult.redirectUrl)
        {
            response.sendRedirect(includeResult.redirectUrl)
            return includeResult
        }
        if (includeResult.error)
        {
            throw new IncludeStatusException(includeResult)
        }

        String content = includeResult.getContent()
        if (attrs.var)
            g.set(attrs: [var: var, value: content, scope: attrs.scope])
        out << content


        includeResult
    }


    Closure mvcUrl = { attrs ->
        def mapping = attrs.mapping
        if (!mapping) throw new IllegalArgumentException("[mapping] attribute must be specified for <g:mvcUrl>")

        def args = attrs.args ?: []
        def urivars = attrs.urivars ?: []

        def builder = MvcUriComponentsBuilder.fromMappingName(mapping)
        args.eachWithIndex { def entry, int i ->
            builder.arg(i, entry)
        }
        return builder.buildAndExpand(urivars as Object[])
    }


    Closure mvcPath = { attrs ->
        def mapping = attrs.mapping
        if (!mapping) throw new IllegalArgumentException("[mapping] attribute must be specified for <g:mvcPath>.")

        String url = mvcUrl(attrs)
        if (url)
        {
            def queryStrStart = url.indexOf('?')
            if (queryStrStart != -1)
                return url.substring(0, queryStrStart)
        }

        return url
    }





    Closure elm = { Map attrs, body  ->

        def tagName = attrs.remove('tagName')
        if (!tagName)
            throw new IllegalArgumentException("[tagName] attribute must be specified for <g:elm>.")

        out << "<$tagName "
        outputAttributes(attrs,out, attrs.remove('suppressAttrs'))
        out << ">"
        if (body) {
            out << body()
            out << "</$tagName>"
        } else {
            out << "</$tagName>"
        }
    }



    /**
     * Resolves a message code for a given error or code from the resource bundle.
     *
     * @emptyTag
     *
     * @attr error The error to lookup the message for. Used for built-in Grails messages.
     * @attr message The object to lookup the message for. Objects must implement org.springframework.context.MessageSourceResolvable.
     * @attr code The code to lookup the message for. Used for custom application messages.
     * @attr args A list of argument values to apply to the message, when code is used.
     * @attr default The default message to output if the error or code cannot be found in messages.properties.
     * @attr encodeAs The name of a codec to apply, i.e. HTML, JavaScript, URL etc
     * @attr locale override locale to use instead of the one detected
     */
    Closure message = { attrs ->
        messageImpl(attrs)
    }

    @CompileStatic
    def messageImpl(Map attrs) {
        Locale locale = FormatTagLib.resolveLocale(attrs.locale)
        def tagSyntaxCall = (attrs instanceof GroovyPageAttributes) ? attrs.isGspTagSyntaxCall() : false

        def text
        Object error = attrs.error ?: attrs.message
        if (error) {
            if (!attrs.encodeAs && error instanceof MessageSourceResolvable) {
                MessageSourceResolvable errorResolvable = (MessageSourceResolvable)error
                if (errorResolvable.arguments) {
                    error = new DefaultMessageSourceResolvable(errorResolvable.codes, encodeArgsIfRequired(errorResolvable.arguments) as Object[], errorResolvable.defaultMessage)
                }
            }
            try {
                if (error instanceof MessageSourceResolvable) {
                    text = messageSource.getMessage(error, locale)
                } else {
                    text = messageSource.getMessage(error.toString(), null, locale)
                }
            }
            catch (NoSuchMessageException e) {
                if (error instanceof MessageSourceResolvable) {
                    text = ((MessageSourceResolvable)error).codes[0]
                }
                else {
                    text = error?.toString()
                }
            }
        }
        else if (attrs.code) {
            String code = attrs.code?.toString()
            List args = []
            if (attrs.args) {
                args = attrs.encodeAs ? attrs.args as List : encodeArgsIfRequired(attrs.args)
            }
            String defaultMessage
            if (attrs.containsKey('default')) {
                defaultMessage = attrs['default']?.toString()
            } else {
                defaultMessage = code
            }

            def message = messageSource.getMessage(code, args == null ? null : args.toArray(),
                    defaultMessage, locale)
            if (message != null) {
                text = message
            }
            else {
                text = defaultMessage
            }
        }
        if (text) {
            Encoder encoder = codecLookup?.lookupEncoder(attrs.encodeAs?.toString() ?: 'raw')
            return encoder  ? encoder.encode(text) : text
        }
        ''
    }

    def url = { Map attrs ->

        if (!attrs.containsKey('value'))
            throwTagError("value attribute is required for <g:url>")

        String value = attrs.remove('value') ?: ''
        String context = attrs.context

        // if context given, fix it
        if (context && !context.startsWith('/'))
            context = '/'+context

        UrlType type = UrlUtils.determineUrlType(value)

        if (value.startsWith('~/'))
        {
            value = value.substring(1)
        }



        StringBuilder url = new StringBuilder();
        if (type == UrlType.CONTEXT_RELATIVE) {


            if (context == null) {
                url.append(request.getContextPath());
            }
            else {
                if (context.endsWith("/")) {
                    url.append(context.substring(0, context.length() - 1));
                }
                else {
                    url.append(context);
                }
            }
        }

        Set<String> templateParams = new HashSet()
        def params = attrs.remove('params') ?: []


        if (type != UrlType.RELATIVE && type != UrlType.ABSOLUTE && !value.startsWith("/")) {
            url.append("/");
        }
        url.append(replaceUriTemplateParams(value, params , templateParams));
        url.append(createQueryString(params, templateParams, (url.indexOf("?") == -1)));

        String urlStr = url.toString();
        if (type != UrlType.ABSOLUTE) {
            // Add the session identifier if needed
            // (Do not embed the session identifier in a remote link!)
            urlStr = response.encodeURL(urlStr);
        }

        // HTML and/or JavaScript escape, if demanded.
        urlStr = htmlEscape(attrs,urlStr);
        urlStr = attrs.javaScriptEscape ? JavaScriptUtils.javaScriptEscape(urlStr) : urlStr;

        attrs.remove('javaScriptEscape')

        return urlStr;
    }

    /**
     * Replace template markers in the URL matching available parameters. The
     * name of matched parameters are added to the used parameters set.
     * <p>Parameter values are URL encoded.
     * @param uri the URL with template parameters to replace
     * @param params parameters used to replace template markers
     * @param usedParams set of template parameter names that have been replaced
     * @return the URL with template parameters replaced
     */
    protected String replaceUriTemplateParams(String uri, List<List> params, Set<String> usedParams) {

        String encoding = response.getCharacterEncoding();
        params.each { param->
            def paramName = param[0].toString()
            String template = URL_TEMPLATE_DELIMITER_PREFIX + param[0] + URL_TEMPLATE_DELIMITER_SUFFIX;
            if (uri.contains(template)) {
                usedParams.add(paramName);
                try {
                    uri = uri.replace(template, UriUtils.encodePath(param[1]?.toString(), encoding));
                }
                catch (UnsupportedEncodingException ex) {
                    throw new GrailsTagException(ex)
                }
            }
            else {
                template = URL_TEMPLATE_DELIMITER_PREFIX + "/" + paramName + URL_TEMPLATE_DELIMITER_SUFFIX;
                if (uri.contains(template)) {
                    usedParams.add(paramName);
                    try {
                        uri = uri.replace(template, UriUtils.encodePathSegment(param[1]?.toString(), encoding));
                    }
                    catch (UnsupportedEncodingException ex) {
                        throw new GrailsTagException(ex)
                    }
                }
            }
        }
        return uri;
    }

    /**
     * Build the query string from available parameters that have not already
     * been applied as template params.
     * <p>The names and values of parameters are URL encoded.
     * @param params the parameters to build the query string from
     * @param usedParams set of parameter names that have been applied as
     * template params
     * @param includeQueryStringDelimiter true if the query string should start
     * with a '?' instead of '&'
     * @return the query string
     */
    protected String createQueryString(List<List> params, Set<String> usedParams, boolean includeQueryStringDelimiter) {

        String encoding = response.getCharacterEncoding();
        StringBuilder qs = new StringBuilder();
        params.each { List param ->
            def paramName = param[0].toString()
            if (!usedParams.contains(paramName) && StringUtils.hasLength(paramName)) {
                if (includeQueryStringDelimiter && qs.length() == 0) {
                    qs.append("?");
                }
                else {
                    qs.append("&");
                }
                try {
                    qs.append(UriUtils.encodeQueryParam(paramName, encoding));
                    if (param[1] != null) {
                        qs.append("=");
                        qs.append(UriUtils.encodeQueryParam(param[1]?.toString(), encoding));
                    }
                }
                catch (UnsupportedEncodingException ex) {
                    throw new GrailsTagException(ex)
                }
            }
        }
        return qs.toString();
    }

    def javascript = { Map attrs, body ->
        def src = attrs.src
        if (!src)
            throwTagError("src attribute for <g:javascript /> is required.")

        def urlattrs = new GroovyPageAttributes(attrs.subMap(['htmlEscape','context']))
        urlattrs['value'] = src;

        src = attrs.src = g.url(urlattrs)
        attrs.type = 'text/javascript'
        attrs.tagName ='script'
        out << g.elm(attrs,body)
    }


    private List encodeArgsIfRequired(arguments) {
        arguments.collect { value ->
            if (value == null || value instanceof Number || value instanceof Date) {
                value
            } else {
                htmlEscape([htmlEscape:true],value)
            }
        }
    }


}

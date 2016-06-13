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
import com.rabbtor.gsp.util.GspTagUtils
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
import org.grails.taglib.GrailsTagException
import org.grails.taglib.GroovyPageAttributes
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.*
import org.springframework.core.convert.ConversionService
import org.springframework.util.ObjectUtils
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


    static returnObjectForTags = ['set', 'mvcUrl', 'mvcPath', 'message', 'url', 'encode','cookie','header']

    public static final String DEFAULT_ARGUMENT_SEPARATOR = ",";


    ApplicationContext applicationContext
    GrailsApplication grailsApplication

    @Autowired(required = false)
    ConversionService conversionService

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


    Closure elm = { Map attrs, Closure body ->
        elmImpl(attrs, body)
    }

    @CompileStatic
    protected Object elmImpl(Map attrs, Closure body)
    {
        def tagName = attrs.remove('tagName')
        if (!tagName)
            throw new IllegalArgumentException("[tagName] attribute must be specified for <g:elm>.")

        out << "<$tagName ".toString()
        outputAttributes(attrs, out, (Collection<String>) attrs.remove('suppressAttrs'))
        out << ">"
        if (body)
        {
            out << (String) body()
            out << "</$tagName>".toString()
        } else
        {
            out << "</$tagName>".toString()
        }
    }

    /**
     * Resolves a message code for a given error or code from the resource bundle.
     *
     * @emptyTag
     *
     * @attr message The object to lookup the message for. Objects must implement org.springframework.context.MessageSourceResolvable.
     * @attr code The code to lookup the message for. Used for custom application messages.
     * @attr args|arguments A list of argument values to apply to the message, when code is used.
     * @attr default|text The default message to output if the error or code cannot be found in messages.properties.
     * @attr locale override locale to use instead of the one detected
     * @attr htmlEscape boolean value to html escape or not. 'false' by default.
     * @attr javaScriptEscape boolean value to javascript escape. 'false' by default.
     */
    Closure message = { Map attrs ->
        String msg = resolveMessage(attrs);

        if (!attrs.containsKey(GspTagUtils.HTML_ESCAPE_ATTR_NAME))
            attrs.htmlEscape = false

        msg = htmlEscape(attrs, msg)
        msg = javascriptEscape(attrs,msg)

        return msg
    }

    /**
     * Resolve the specified message into a concrete message String.
     * The returned message String should be unescaped.
     */
    @CompileStatic
    protected String resolveMessage(Map attrs) throws NoSuchMessageException
    {
        MessageSource messageSource = getRequestContext().getMessageSource();
        Locale locale = null
        if (messageSource == null)
        {
            throwTagError("No corresponding MessageSource found for <g:message />");
        }
        MessageSourceResolvable message = (MessageSourceResolvable) attrs.remove('message')

        // Evaluate the specified MessageSourceResolvable, if any.
        if (message != null)
        {
            // We have a given MessageSourceResolvable.
            return messageSource.getMessage(message, locale);
        }

        String code = (String) attrs.remove('code')
        String text = (String) (attrs.remove('text') ?: attrs.remove('default'))


        if (code != null || text != null)
        {
            // We have a code or default text that we need to resolve.
            Object[] argumentsArray = resolveMessageArguments(attrs);


            if (text != null)
            {
                // We have a fallback text to consider.
                return messageSource.getMessage(
                        code, argumentsArray, text, getRequestContext().getLocale());
            } else
            {
                // We have no fallback text to consider.
                return messageSource.getMessage(
                        code, argumentsArray, getRequestContext().getLocale());
            }
        }

        // All we have is a specified literal text.
        return text;
    }

    /**
     * Resolve the given arguments Object into an arguments array.
     * @param arguments the specified arguments Object
     * @return the resolved arguments as array
     */
    @CompileStatic
    protected Object[] resolveMessageArguments(Map attrs)
    {
        def arguments = attrs.remove('args') ?: attrs.remove('arguments')
        String separator = (attrs.remove('separator') ?: attrs.remove('argumentSeparator')) ?: DEFAULT_ARGUMENT_SEPARATOR
        if (arguments instanceof String)
        {
            String[] stringArray =
                    StringUtils.delimitedListToStringArray((String) arguments, separator);
            if (stringArray.length == 1)
            {
                Object argument = stringArray[0];
                if (argument != null && argument.getClass().isArray())
                {
                    return ObjectUtils.toObjectArray(argument);
                } else
                {
                    return [argument] as Object[]
                }
            } else
            {
                return stringArray;
            }
        } else if (arguments instanceof Object[])
        {
            return (Object[]) arguments;
        } else if (arguments instanceof Collection)
        {
            return ((Collection<?>) arguments).toArray();
        } else if (arguments != null)
        {
            // Assume a single argument object.
            return [arguments] as Object[]
        } else
        {
            return null;
        }
    }


    def url = { Map attrs ->
        return urlImpl(attrs)
    }

    @CompileStatic
    protected String urlImpl(Map attrs)
    {
        if (!attrs.containsKey('value'))
            throwTagError("value attribute is required for <g:url>")

        String value = attrs.remove('value') ?: ''
        String context = attrs.context

        // if context given, fix it
        if (context && !context.startsWith('/'))
            context = '/' + context

        UrlType type = UrlUtils.determineUrlType(value)

        if (value.startsWith('~/'))
        {
            value = value.substring(1)
        }



        StringBuilder url = new StringBuilder();
        if (type == UrlType.CONTEXT_RELATIVE)
        {

            if (context == null)
            {
                url.append(request.getContextPath());
            } else
            {
                if (context.endsWith("/"))
                {
                    url.append(context.substring(0, context.length() - 1));
                } else
                {
                    url.append(context);
                }
            }
        }

        Set<String> templateParams = new HashSet()
        def params = (List<List>) (attrs.remove('params') ?: [])


        if (type != UrlType.RELATIVE && type != UrlType.ABSOLUTE && !value.startsWith("/"))
        {
            url.append("/");
        }
        url.append(replaceUriTemplateParams(value, params, templateParams));
        url.append(createQueryString(params, templateParams, (url.indexOf("?") == -1)));

        String urlStr = url.toString();
        if (type != UrlType.ABSOLUTE)
        {
            // Add the session identifier if needed
            // (Do not embed the session identifier in a remote link!)
            urlStr = response.encodeURL(urlStr);
        }

        // HTML and/or JavaScript escape, if demanded.
        urlStr = htmlEscape(attrs, urlStr);
        urlStr = attrs.remove('javaScriptEscape') ? JavaScriptUtils.javaScriptEscape(urlStr) : urlStr;

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
    @CompileStatic
    protected String replaceUriTemplateParams(String uri, List<List> params, Set<String> usedParams)
    {

        String encoding = response.getCharacterEncoding();
        params.each { param ->
            def paramName = param[0].toString()
            String template = UrlUtils.URL_TEMPLATE_DELIMITER_PREFIX + param[0] + UrlUtils.URL_TEMPLATE_DELIMITER_SUFFIX;
            if (uri.contains(template))
            {
                usedParams.add(paramName);
                try
                {
                    uri = uri.replace(template, UriUtils.encodePath(param[1]?.toString(), encoding));
                }
                catch (UnsupportedEncodingException ex)
                {
                    throw new GrailsTagException(ex)
                }
            } else
            {
                template = UrlUtils.URL_TEMPLATE_DELIMITER_PREFIX + "/" + paramName + UrlUtils.URL_TEMPLATE_DELIMITER_SUFFIX;
                if (uri.contains(template))
                {
                    usedParams.add(paramName);
                    try
                    {
                        uri = uri.replace(template, UriUtils.encodePathSegment(param[1]?.toString(), encoding));
                    }
                    catch (UnsupportedEncodingException ex)
                    {
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
    @CompileStatic
    protected String createQueryString(List<List> params, Set<String> usedParams, boolean includeQueryStringDelimiter)
    {

        String encoding = response.getCharacterEncoding();
        StringBuilder qs = new StringBuilder();
        params.each { List param ->
            def paramName = param[0].toString()
            if (!usedParams.contains(paramName) && StringUtils.hasLength(paramName))
            {
                if (includeQueryStringDelimiter && qs.length() == 0)
                {
                    qs.append("?");
                } else
                {
                    qs.append("&");
                }
                try
                {
                    qs.append(UriUtils.encodeQueryParam(paramName, encoding));
                    if (param[1] != null)
                    {
                        qs.append("=");
                        qs.append(UriUtils.encodeQueryParam(param[1]?.toString(), encoding));
                    }
                }
                catch (UnsupportedEncodingException ex)
                {
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

        def urlattrs = new GroovyPageAttributes(attrs.subMap([GspTagUtils.HTML_ESCAPE_ATTR_NAME, 'context']))
        urlattrs['value'] = src;

        src = attrs.src = g.url(urlattrs)
        attrs.type = 'text/javascript'
        attrs.tagName = 'script'
        out << g.elm(attrs, body)
    }

    /**
     * @emptyTag
     *
     * @attr type encoding type. one of : 'html','javascript',
     *
     */
    def encode = { Map attrs ->
        return encodeImpl(attrs)
    }

    @CompileStatic
    String encodeImpl(Map attrs)
    {

        def type = attrs.remove('type') ?: 'html'
        def types = (type instanceof String) ? [type] : type instanceof String[] ? ((String[]) type).toList()
                : ((Collection<String>) type)



        def value = attrs.remove('value')
        if (!value)
            throwTagError("value attribute is required for <g:encode>")

        String valueStr = ObjectUtils.getDisplayString(value)
        String out = valueStr
        types.each { it ->
            String typeString = it.toString()
            if ('html'.equalsIgnoreCase(typeString))
                out = htmlEscape(out)
            else if ('javascript'.equalsIgnoreCase(typeString) || 'js'.equalsIgnoreCase(typeString))
                out = JavaScriptUtils.javaScriptEscape(out)
            else
            {
                def encoder = codecLookup?.lookupEncoder(typeString)
                if (encoder)
                    out = encoder.encode(out).toString()
                else
                    throwTagError("No suitable encoder found for type attribute [${type}] of <g:encode>")
            }


        }

        out
    }


}

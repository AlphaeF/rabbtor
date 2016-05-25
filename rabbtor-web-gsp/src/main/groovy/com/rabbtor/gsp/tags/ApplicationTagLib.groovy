package com.rabbtor.gsp.tags

import com.rabbtor.gsp.util.GrailsIncludeUtils
import com.rabbtor.web.servlet.support.RequestIncludeWrapper
import com.rabbtor.web.servlet.support.RequestParams
import grails.artefact.TagLibrary
import grails.config.Settings
import grails.core.GrailsApplication
import grails.core.support.GrailsApplicationAware
import grails.gsp.TagLib
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.core.convert.ConversionService
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import org.springframework.web.servlet.support.RequestDataValueProcessor

@TagLib
class ApplicationTagLib implements
        ApplicationContextAware,
        InitializingBean,
        GrailsApplicationAware,
        TagLibrary
{
    static returnObjectForTags = ['set', 'applyCodec','mvcUrl','mvcPath']
    static encodeAsForTags = ['include', 'none']


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

    RequestDataValueProcessor requestDataValueProcessor


    void afterPropertiesSet()
    {
        def config = grailsApplication.config

        useJsessionId = config.getProperty(Settings.GRAILS_VIEWS_ENABLE_JSESSIONID, Boolean, false)
        hasResourceProcessor = applicationContext.containsBean('grailsResourceProcessor')

        if (applicationContext.containsBean('requestDataValueProcessor'))
        {
            requestDataValueProcessor = applicationContext.getBean('requestDataValueProcessor', RequestDataValueProcessor)
        }
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

    Closure include = { attrs, body ->
        def path = attrs.path;
        def mapping = attrs.mapping

        if (!path && !mapping) throw new IllegalArgumentException("One of [path] or [mapping] attributes must be specified for <g:include>!")

        if (mapping) {
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

        def includeRequestParams = (Boolean)attrs.includeRequestParams

        def wrappedRequest = new RequestIncludeWrapper(request,requestParams.asRequestParameterMap(conversionService), (boolean)includeRequestParams)


        def includeResult = GrailsIncludeUtils.include(path,wrappedRequest,response,[:])
        def var = attrs.var;
        if (var) {
            g.set(attrs: [(("${var}IncludeResult").toString() ):includeResult, scope: attrs.scope])
        }

        if (!includeResult.redirectUrl && !includeResult.error) {
            def contentStr = includeResult.contentOrEmpty
            if (var)
                g.set(attrs:[(var): contentStr, scope: attrs.scope])
            else
                out << contentStr
        }

        includeResult
    }

    Closure mvcUrl = { attrs ->
        def mapping = attrs.mapping
        if (!mapping) throw new IllegalArgumentException("[mapping] attribute must be specified for <g:mvcUrl>")

        def args = attrs.args ?: []
        def urivars = attrs.urivars ?: []

        def builder = MvcUriComponentsBuilder.fromMappingName(mapping)
        args.eachWithIndex { def entry, int i ->
            builder.arg(i,entry)
        }
        return builder.buildAndExpand(urivars as Object[])
    }

    Closure mvcPath = { attrs ->
        def mapping = attrs.mapping
        if (!mapping) throw new IllegalArgumentException("[mapping] attribute must be specified for <g:path>")

        String url = mvcUrl(attrs)
        if (url) {
            def queryStrStart = url.indexOf('?')
            if (queryStrStart != -1)
                return url.substring(0,queryStrStart)
        }

        return url
    }
}

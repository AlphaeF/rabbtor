package com.rabbtor.gsp.tags

import com.rabbtor.gsp.util.GspIncludeUtils
import com.rabbtor.gsp.util.GspWebUtils
import com.rabbtor.web.servlet.support.IncludeStatusException
import com.rabbtor.web.servlet.support.RequestIncludeWrapper
import com.rabbtor.web.servlet.support.RequestParams
import grails.artefact.TagLibrary
import grails.config.Settings
import grails.core.GrailsApplication
import grails.core.support.GrailsApplicationAware
import grails.gsp.TagLib
import groovy.transform.CompileStatic
import org.grails.buffer.GrailsPrintWriter
import org.grails.encoder.CodecLookup
import org.grails.encoder.Encoder
import org.grails.taglib.GroovyPageAttributes
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceResolvable
import org.springframework.context.NoSuchMessageException
import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.core.convert.ConversionService
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import org.springframework.web.servlet.support.RequestDataValueProcessor
import org.springframework.web.util.HtmlUtils

@TagLib
class ApplicationTagLib implements
        ApplicationContextAware,
        InitializingBean,
        GrailsApplicationAware,
        TagLibrary
{
    static returnObjectForTags = ['set', 'mvcUrl', 'mvcPath', 'message', 'include']
    //static encodeAsForTags = ['include', 'none']


    ApplicationContext applicationContext
    GrailsApplication grailsApplication

    @Autowired(required = false)
    ConversionService conversionService

    MessageSource messageSource
    CodecLookup codecLookup
    RequestDataValueProcessor requestDataValueProcessor

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
        if (var)
        {
            g.set(attrs: [(("${var}IncludeResult").toString()): includeResult, scope: attrs.scope])
        }



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

    Closure elm = { attrs, body  ->
        def tagName = attrs.remove('tagName')
        if (!tagName)
            throw new IllegalArgumentException("[tagName] attribute must be specified for <g:elm>.")

        out << "<$tagName "
        outputAttributes(attrs,out)
        out << ">"
        if (body) {
            out << body()
            out << "</$tagName>"
        } else {
            out << "</$tagName>"
        }
    }

    /**
     * Dump out attributes in HTML compliant fashion.
     */
    @CompileStatic
    void outputAttributes(Map attrs, GrailsPrintWriter writer) {
        attrs.remove('tagName')
        Encoder htmlEncoder = codecLookup?.lookupEncoder('HTML')
        String encoding = GspWebUtils.lookupEncoding(response,request)
        attrs.each { k, v ->
                writer << ' '+k
                writer << '="'
                writer << v == null ? '' : GspWebUtils.htmlEncode(v,htmlEncoder,encoding)
                writer << '"'
        }
    }

    /**
     * Resolves a message code for a given error or code from the resource bundle.
     *
     * @emptyTag
     *
     * @attr error The error to resolveOrDefault the message for. Used for built-in Grails messages.
     * @attr message The object to resolveOrDefault the message for. Objects must implement org.springframework.context.MessageSourceResolvable.
     * @attr code The code to resolveOrDefault the message for. Used for custom application messages.
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

    @CompileStatic
    private List encodeArgsIfRequired(arguments) {
        arguments.collect { value ->
            if (value == null || value instanceof Number || value instanceof Date) {
                value
            } else {
                Encoder encoder = codecLookup?.lookupEncoder('HTML')
                GspWebUtils.htmlEncode(value,encoder,GspWebUtils.lookupEncoding(response,request))
            }
        }
    }
}

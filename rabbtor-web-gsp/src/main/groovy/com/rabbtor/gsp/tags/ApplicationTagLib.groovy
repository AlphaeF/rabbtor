package com.rabbtor.gsp.tags

import grails.artefact.TagLibrary
import grails.config.Settings
import grails.core.GrailsApplication
import grails.core.support.GrailsApplicationAware
import grails.gsp.TagLib
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.web.servlet.support.RequestDataValueProcessor

@TagLib
class ApplicationTagLib implements ApplicationContextAware, InitializingBean, GrailsApplicationAware
{
    static returnObjectForTags = ['createLink', 'resource', 'createLinkTo', 'cookie', 'header', 'img', 'join', 'meta', 'set', 'applyCodec']

    ApplicationContext applicationContext
    GrailsApplication grailsApplication

    static final SCOPES = [page: 'pageScope',
                           application: 'servletContext',
                           request:'request',
                           session:'session',
                           flash:'flash']

    boolean useJsessionId = false
    boolean hasResourceProcessor = false

    RequestDataValueProcessor requestDataValueProcessor


    void afterPropertiesSet() {
        def config = grailsApplication.config

        useJsessionId = config.getProperty(Settings.GRAILS_VIEWS_ENABLE_JSESSIONID, Boolean, false)
        hasResourceProcessor = applicationContext.containsBean('grailsResourceProcessor')

        if (applicationContext.containsBean('requestDataValueProcessor')) {
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
        if (attrs.bean) {
            value = applicationContext.getBean(attrs.bean)
        } else {
            value = attrs.value
            def containsValue = attrs.containsKey('value')
            if (!containsValue && body) value = body()
        }

        this."$scope"."$var" = value
        null
    }
}

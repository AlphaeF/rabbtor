package com.rabbtor.web.servlet.gsp.tags

import com.rabbtor.taglib.TagLib
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.InvokerHelper
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.util.StringUtils


@TagLib
class ApplicationTagLib implements ApplicationContextAware, TagLibrary
{
    static returnObjectForTags = ['createLink', 'resource', 'createLinkTo', 'cookie', 'header', 'img', 'join', 'meta', 'set', 'applyCodec']

    ApplicationContext applicationContext

    static final SCOPES = [page: 'pageScope',
                           application: 'servletContext',
                           request:'request',
                           session:'session']

    boolean useJsessionId = false
    boolean hasResourceProcessor = false

    void afterPropertiesSet() {
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

        switch (scope) {
            case 'pageScope':
                this.pageScope."$var" = value
                break
            case 'request':
                this.request.setAttribute(var,value)
                break
            case 'session':
                this.session.setAttribute(var,value)
                break
            case 'application':
                this.servletContext.setAttribute(var,value)
                break
        }
        null
    }



}

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
package com.rabbtor.gsp.tags

import com.rabbtor.gsp.taglib.TagLibraryExt
import com.rabbtor.gsp.util.GspTagUtils
import grails.gsp.TagLib
import groovy.transform.CompileStatic
import org.grails.taglib.GroovyPageAttributes
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceResolvable
import org.springframework.context.NoSuchMessageException
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.servlet.support.BindStatus

@TagLib
class ValidationTagLib implements TagLibraryExt
{
    public static final String DEFAULT_ARGUMENT_SEPARATOR = ",";

    static returnObjectForTags = ['hasErrors','message']


    Closure eachError = { Map attrs, body ->
        BindStatus bindStatus = getBindStatus(attrs)
        if (bindStatus)
        {
            resolveBindStatusErrors(bindStatus)?.each {
                out << (String) body(it)
            }
        } else
            throwTagError("No BindingResult found while executing <g:eachError>")
    }


    List<ObjectError> resolveBindStatusErrors(BindStatus bindStatus)
    {
        if (bindStatus.expression == null)
            bindStatus.errors.globalErrors

        if ("*".equals(bindStatus.expression)) {
            bindStatus.errors?.getAllErrors();
        }
        else
            bindStatus.errors?.getFieldErrors(bindStatus.expression);
    }

    Closure hasErrors = { Map attrs ->
        BindStatus bindStatus = getBindStatus(attrs)
        if (!bindStatus)
            throwTagError("No BindingResult found while executing <g:hasErrors>")

        return resolveBindStatusErrors(bindStatus)?.any()
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
        return messageImpl(attrs)
    }

    @CompileStatic
    String messageImpl(Map attrs)
    {
        String msg = resolveMessage(attrs);


        if (!attrs.containsKey('escape')) {
            // by default, raw escape
            attrs.codec = 'Raw'
        } else {
            attrs.codec = attrs.remove('escape')
        }

        msg = escapeImpl(new GroovyPageAttributes([codec:attrs.codec,value: msg]),'message',null)

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
        Locale locale = FormatTagLib.resolveLocale(attrs.locale)
        if (locale == null)
            locale = getRequestContext().locale

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
                        code, argumentsArray, text, locale);
            } else
            {
                // We have no fallback text to consider.
                return messageSource.getMessage(
                        code, argumentsArray, locale);
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

    /**
     * Overrides {@link TagLibraryExt#isDefaultHtmlEscape()} to return <code>true</code> if the default value resolved from the
     * {@link org.springframework.web.servlet.support.RequestContext#getDefaultHtmlEscape()}  is null.
     */
    @Override
    boolean isDefaultHtmlEscape()
    {
        Boolean defaultHtmlEscape = requestContext.getDefaultHtmlEscape();
        return (defaultHtmlEscape == null || defaultHtmlEscape.booleanValue());
    }

    @CompileStatic
    protected BindStatus getBindStatus(Map attrs)
    {
        String path = attrs.path
        BindStatus bindStatus = path ? GspTagUtils.getBindStatus(path, request, requestContext, isHtmlEscape(attrs)) :
                GspTagUtils.getBindStatusFromScope(pageScope, request)

        bindStatus
    }


}

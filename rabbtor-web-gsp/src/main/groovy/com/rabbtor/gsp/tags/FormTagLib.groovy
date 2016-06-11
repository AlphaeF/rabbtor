/*
 * Copyright 2002-2014 the original author or authors.
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

/* Modifications copyright (C) 2016 - Rabbytes,Inc */


package com.rabbtor.gsp.tags

import com.rabbtor.gsp.taglib.TagLibraryExt
import com.rabbtor.model.ModelMetadataAccessor
import com.rabbtor.model.ModelMetadataAccessorUtils
import com.rabbtor.web.servlet.util.BindStatusUtils
import com.rabbtor.web.servlet.util.RequestContextUtils
import grails.gsp.TagLib
import org.grails.encoder.CodecLookup
import org.grails.taglib.GroovyPageAttributes
import org.springframework.beans.BeanWrapper
import org.springframework.beans.PropertyAccessor
import org.springframework.beans.PropertyAccessorFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.ConversionService
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.servlet.support.BindStatus
import org.springframework.web.servlet.support.RequestContext
import org.springframework.web.servlet.support.RequestDataValueProcessor
import org.springframework.web.servlet.tags.form.SelectedValueComparator
import org.springframework.web.servlet.tags.form.ValueFormatter
import org.springframework.web.util.HtmlUtils
import org.springframework.web.util.UriUtils

import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.beans.PropertyEditor

/**
 * Form tag library for GSP form tags.
 * Part of the implementation is copied from Spring Framework JSP tag implementations.(https://spring.io/)
 *
 * Unlike Grails form tags, Rabbtor's form tags are data bound just like Spring JSP form tags. All tags except 'select'
 * require a 'path' parameter for the property binding path. 'select' tag also accepts this parameter but can work even without it
 * but in that case, it will not be data-bound.
 *
 * All form tags here accept the following common attributes:
 * <ul>
 * <li>id: element id. if not provided, will be generated from the name without the special characters like '[',']'</li>
 * <li>name: element name. auto generated using the 'path' attribute.</li>
 * <li>path: property binding path of the currently processed command/model object. required for all tags except the 'select' tag.</li>
 * <li>htmlEscape: expression which returns a boolean value to determine whether output will be HTML encoded.If not set, global default value will be used.
 * {@link TagLibraryExt#isDefaultHtmlEscape}
 * {@link TagLibraryExt#isResponseEncodedHtmlEscape}
 * </li>
 * </ul>
 *
 * <p>Special boolean HTML attributes:</p>
 * 'disabled','readonly' and 'checked' attributes can have boolean values as groovy expressions or standard boolean strings: 'true','false'
 * in addition to the default HTML usage patterns. For instance, all the following for the 'disabled' attribute will render disabled='disabled'
 * <ul>
 * <li>{@code <g:input disabled /> }</li>
 * <li>{@code <g:input disabled='' /> }</li>
 * <li>{@code <g:input disabled='disabled' /> }</li>
 * <li>{@code <g:input disabled='true' /> }</li>
 * <li>{@code <g:input disabled='\$&#123;true&#125;' /> }</li>
 * </ul>
 *
 * Only {@code disabled='false'} or {@code disabled='&#123;false&#125;'} will be evaluated to false, and then no disabled attribute will be rendered.
 * <p>
 * Except the mentioned attributes, all HTML attributes are rendered without any processing with the provided values. For these attributes,
 * no HTML encoding will be done so it is the developer's responsibility to encode the attribute value if necessary.
 * <p>
 * @Since 1.0
 * @Author CK
 */
@TagLib
class FormTagLib implements TagLibraryExt
{
    static returnObjectForTags = ['displayNameFor','idFor']

    /** The default HTTP method using which form values are sent to the server: "post" */
    private static final String DEFAULT_METHOD = "post";

    /**
     * Name of the exposed path variable within the scope of this tag: "nestedPath".
     * Same value as {org.springframework.web.servlet.tags.NestedPathTag#NESTED_PATH_VARIABLE_NAME}.
     */
    public static final String NESTED_PATH_VARIABLE_NAME = "nestedPath";

    /** The default attribute name: &quot;command&quot; */
    public static final String DEFAULT_COMMAND_NAME = "command";

    /** The name of the '{@code modelAttribute}' setting */
    private static final String MODEL_ATTRIBUTE = "modelAttribute";

    /**
     * The name of the pageContext attribute under which the
     * form object name is exposed.
     */
    public static
    final String MODEL_ATTRIBUTE_VARIABLE_NAME = "org.springframework.web.servlet.tags.form.${MODEL_ATTRIBUTE}"


    @Autowired(required = false)
    ConversionService conversionService

    CodecLookup codecLookup

    /**
     * Gsp form tag which mimics the way Spring Framework JSP form tag {@code <s:form/>} works.
     */
    Closure form = { attrs, body ->

        String modelAttribute = (attrs.remove(MODEL_ATTRIBUTE) ?: attrs.remove('commandName')) ?: DEFAULT_COMMAND_NAME
        request.setAttribute(MODEL_ATTRIBUTE_VARIABLE_NAME, modelAttribute)

        // Save previous nestedPath value, build and expose current nestedPath value.
        // Use request scope to expose nestedPath to included pages too.
        String previousNestedPath =
                (String) request.getAttribute(NESTED_PATH_VARIABLE_NAME);
        request.setAttribute(NESTED_PATH_VARIABLE_NAME,
                modelAttribute + PropertyAccessor.NESTED_PROPERTY_SEPARATOR);

        // Set action and method
        attrs.method = getHttpMethod(attrs)

        String action = null
        def mvcUrl = attrs.remove('mvcUrl')
        if (mvcUrl && !(mvcUrl instanceof Map))
            throwTagError('mvcUrl attribute of <g:form> must be a map containing the same attributes which apply to the <g:mvcUrl> tag.')
        if (mvcUrl)
            action = processAction(attrs, g.mvcUrl(new GroovyPageAttributes((Map) mvcUrl)))
        else
        {
            action = resolveFormAction(attrs)
        }

        attrs.action = action


        // AJAX
        if (attrs.containsKey('ajax'))
        {
            setAjaxFormAttrs(attrs, attrs.remove('ajax'))
        }

        // RENDER the TAG content
        attrs.tagName = 'form'

        out << g.elm(attrs, body)

        // cleanup
        request.removeAttribute(MODEL_ATTRIBUTE_VARIABLE_NAME);
        if (previousNestedPath != null)
        {
            // Expose previous nestedPath value.
            request.setAttribute(NESTED_PATH_VARIABLE_NAME, previousNestedPath);
        } else
        {
            // Remove exposed nestedPath value.
            request.removeAttribute(NESTED_PATH_VARIABLE_NAME);
        }

    }

    /**
     * Put unobtrusive ajax HTML 5 attributes to the current form tag attributes
     * @param attrs current form tag attributes
     * @param ajax ajax options Map
     */
    private void setAjaxFormAttrs(Map attrs, def ajax)
    {
        if (ajax && ajax instanceof Map)
        {
            def mode = ajax.mode ?: 'update'
            def target = ajax.target

            if (!target)
                throwTagError("ajax target must be set for the ajax attribute of <g:form />")

            attrs['data-ajax'] = true
            attrs['data-ajax-update'] = target
        }
    }

/**
 * Generates HTML input element
 * <p>
 * <strong>type</strong> attribute determines which type of input is generated. text, password, checkbox, radio, email etc.
 * If type is checkbox or radio, <strong>value</strong> attribute is required.
 * </p>
 *
 * Usage: {@code <g:input type='text|checkbox|radio|hidden|password|email|..' path='personCommand.age' />}
 */
    Closure input = { attrs, body ->
        getRequiredAttribute(attrs, 'path', 'g:input')

        attrs.type = attrs.type ?: 'text'

        def bindStatus = getBindStatus(attrs)
        def value = attrs.value
        def boundValue = bindStatus.value

        if (attrs.type == 'checkbox')
        {
            if (!getHtmlBooleanAttributeValue(attrs, 'disabled'))
            {
                Map hiddenAttrs = new GroovyPageAttributes()
                hiddenAttrs.name = WebDataBinder.DEFAULT_FIELD_MARKER_PREFIX + getName(attrs)
                hiddenAttrs.value = processFieldValue(hiddenAttrs.name, 'on', attrs.type)
                hiddenAttrs.type = 'hidden'
                hiddenAttrs.tagName = 'input'

                out << g.elm(hiddenAttrs)
            }
        }

        if (isSingleSelectTag(attrs.type))
        {
            // Generate incremental id
            if (!attrs.id)
                attrs.id = nextElementId(resolveId(attrs))

            Class<?> valueType = bindStatus.getValueType();

            if (Boolean.class == valueType || boolean.class == valueType)
            {
                if (boundValue instanceof String)
                {
                    boundValue = Boolean.valueOf((String) boundValue);
                }
                Boolean booleanValue = (boundValue != null ? (Boolean) boundValue : Boolean.FALSE);
                attrs.checked = booleanValue
                attrs.value = processFieldValue(getName(attrs), 'true', attrs.type)
            } else
            {
                if (value == null)
                {
                    throwTagError("Attribute 'value' for <g:input> with type [${attrs.type}] is required when binding to non-boolean values")
                }
                attrs.value = processFieldValue(getName(attrs), getDisplayString(attrs, value), attrs.type)
                if (isOptionSelected(bindStatus, value))
                {
                    attrs.checked = true
                }
            }
        } else
            attrs.value = processFieldValue(getName(attrs), getDisplayString(attrs, bindStatus.value), attrs.type)

        attrs.tagName = 'input'
        return fieldImpl(attrs, body)
    }

    boolean isSingleSelectTag(String type)
    {
        type && ['checkbox', 'radio', 'select'].any {
            it.equalsIgnoreCase(type)
        }
    }

    /**
     * Generates HTML checkbox input element
     * Usage: {@code <g:checkbox path='..' value='..' />}
     * Same as using {@code <g:input type='checkbox' />}
     * {@see # input}
     */
    Closure checkbox = { attrs, body ->
        attrs.type = 'checkbox'
        out << input(attrs, body)
    }

    /**
     * Generates HTML radio input element.
     * Usage: {@code <g:radio path='..' value='..' />}
     * Same as using {@code <g:input type='radio' />}
     * {@see # input}
     */
    Closure radio = { attrs, body ->
        attrs.type = 'radio'
        out << input(attrs, body)
    }

    /**
     * Generates HTML hidden input element.
     * Usage: {@code <g:hidden path='..' />}
     * {@see # input}
     */
    Closure hidden = { attrs, body ->
        attrs.type = 'hidden'
        return input(attrs, body)
    }

    /**
     * Generates HTML label element for the given property path with the {@code for} attribute matching the auto generated {@code id}
     * using the {@code path} attribute.
     * <p>
     * Label value is first resolved from the message source. If no message found, {@link com.rabbtor.model.annotation.DisplayName} annotation of the model property is used.
     * If none of these were found, property name is used.
     *
     * Usage: {@code <g:label path='..' />}
     *
     * @attr path REQUIRED
     * @attr for overrides the autogenerated {@code for} attribute
     *
     * @see #input
     */
    Closure label = { attrs, body ->

        String displayName = displayNameFor(attrs)
        attrs.for = attrs.for ?: autogenerateId(getName(attrs))
        attrs.remove('bindStatus')
        attrs.remove('path')

        attrs.tagName = attrs.tagName ?: 'label'

        body = { ->
            out << getDisplayString(attrs, displayName)
        }

        out << g.elm(attrs, body)
    }

    Closure displayNameFor = { attrs ->
        getRequiredAttribute(attrs, 'path', 'g:displayName')
        BindStatus bindStatus = getBindStatus(attrs)
        String beanName = BindStatusUtils.getBeanName(bindStatus)
        def model = RequestContextUtils.getModelObject(getRequestContext(), request, beanName)
        if (!model)
        {
            throw new IllegalStateException("Neither BindingResult nor plain target object for bean name '" +
                    beanName + "' available as request attribute");
        }
        ModelMetadataAccessor metadataAccessor = ModelMetadataAccessorUtils.lookup(model.getClass(), requestContext.getWebApplicationContext());
        return metadataAccessor.getDisplayName(bindStatus.path)
    }

    Closure idFor = { attrs ->
        getRequiredAttribute(attrs, 'path', 'g:fieldId')
        return resolveId(attrs)
    }

    /**
     * Generates HTML {@code <select>} element.
     * <p> Given a list of option data, this tag generates options with an optional {@code noSelection} option.
     * <p>Using the model property data, option with the same value is marked as 'selected'. This can be customized using the {@code itemSelected}
     * attribute. Then, that property of the data item of the option will be used for the 'selected' attribute.Hence, model bound data will be
     * ignored for option selection.
     *
     * <p> Unlike other data-bound form tags, this tag can be used without the {@code path}
     * attribute but data-binding features will be disabled. Since this mode does not use model property binding
     * information, marking an option 'selected' using the bound data is not supported. So you must use the {@code itemSelected} attribute
     * for marking the selected option.
     *
     *
     * @attr path optional if not using data-bound select.
     * @attr items:  expression which returns list of objects for option values.(required)</li>
     * @attr itemValue: property name of the item to be used for the option value. If property could not be resolved, item itself will be used.</li>
     * @attr itemLabel: property name of the item to be used for the option text. If property could not be resolved, item itself will be used.</li>
     * @attr itemSelected: property name of the item to be used for the selected state of the option. if property type is not boolean, value will be converted to String and then to boolean</li>
     * @attr optionDisabled: expression which returns boolean or 'true'|'false' string values.determines whether the options should be disabled or not</li>
     * @attr noSelection: expression which returns a map of one entry. Entry key will be option value, Entry value will be option text</li>
     *
     */
    Closure select = { Map attrs, body ->
        String path = attrs.path
        def options = getRequiredAttribute(attrs, 'items', '<g:select>')

        def optionsClass = options.getClass()
        if (!optionsClass.isArray() && !(Collection.isAssignableFrom(optionsClass))
                && !(Map.isAssignableFrom(optionsClass)) && !(optionsClass.isEnum()))
            throwTagError("'items' attribute of <g:select> supports only the array,collection,map and enum types.")

        def multiple = false
        if (attrs.containsKey('multiple'))
        {
            multiple = (attrs.multiple ?: true).toString()
            multiple = 'multiple'.equalsIgnoreCase(multiple) || Boolean.valueOf(multiple)
        } else
        {
            if (path && forceMultiple(attrs))
                multiple = true
        }

        if (multiple)
            attrs.multiple = 'multiple'
        else
            attrs.remove('multiple')

        def valueProperty = attrs.remove('itemValue')
        def labelProperty = attrs.remove('itemLabel')
        def selectedProperty = attrs.remove('itemSelected')

        def noSelection = attrs.remove('noSelection')
        if (noSelection != null)
            if (!(noSelection instanceof Map))
                throwTagError("'noSelection' attribute of <g:select> must be a Map")
            else
                noSelection = ((Map) noSelection).entrySet().iterator().next()



        def value = attrs.value
        BindStatus bindStatus = null
        if (path)
        {
            bindStatus = getBindStatus(attrs)
            if (!attrs.containsKey('value'))
                value = bindStatus.value
        }

        def selectName = getName(attrs)

        def allOptions = []
        boolean anySelected = false
        options.each { item ->
            if (item == null)
            {
                allOptions << [value: null, label: null, selected: false]
                return
            }

            Object optionValue
            Object optionLabel
            Object optionSelected

            BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(item);
            if (valueProperty)
            {
                if (item instanceof Map)
                    optionValue = item[valueProperty]
                else
                    optionValue = wrapper.getPropertyValue(valueProperty)
            } else if (item instanceof Enum)
            {
                optionValue = ((Enum<?>) item).name();
            } else
                optionValue = item

            if (labelProperty)
            {
                if (item instanceof Map)
                    optionLabel = item[labelProperty]
                else
                    optionLabel = wrapper.getPropertyValue(labelProperty)
            } else
            {
                optionLabel = item
            }

            if (selectedProperty)
            {
                optionSelected = wrapper.getPropertyValue(selectedProperty)
            }


            String valueDisplayString = getDisplayString(attrs, optionValue)
            String labelDisplayString = getDisplayString(attrs, optionLabel)



            if (selectName)
                valueDisplayString = processFieldValue(selectName, valueDisplayString, 'option')

            boolean isSelected = false
            if (optionSelected)
            {
                if (Boolean.parseBoolean(optionSelected.toString()))
                {
                    isSelected = true
                }
            } else if (bindStatus)
            {

                if (isOptionSelected(bindStatus, optionValue) || (item != value && isOptionSelected(bindStatus, item)))
                {
                    isSelected = true
                }
            } else
            {
                def selectedValue = value
                if (ObjectUtils.nullSafeEquals(selectedValue, optionValue))
                    isSelected = true
            }

            allOptions << [value: valueDisplayString, label: labelDisplayString, selected: isSelected]
            if (!anySelected)
                anySelected = isSelected
        }

        if (noSelection != null)
        {
            allOptions.add(0, [value: getDisplayString(attrs, processFieldValue(selectName, noSelection.key, 'option')), label: getDisplayString(attrs, noSelection.value), selected: !anySelected])
        }

        def wrappedBody = { ->
            if (body)
                out << body()

            Map optionAttrs = new GroovyPageAttributes()
            allOptions.each { opt ->
                optionAttrs.value = opt.value
                if (opt.selected)
                    optionAttrs.selected = 'selected'
                else
                    optionAttrs.remove('selected')

                def optionBody = { ->
                    out << opt.label
                }
                optionAttrs.tagName = 'option'
                if (attrs.containsKey('optionDisabled'))
                    optionAttrs.disabled = Boolean.parseBoolean(attrs.optionDisabled.toString())
                else
                    optionAttrs.remove('disabled')

                out << g.elm(optionAttrs, optionBody)
                out.println()
            }
        }

        attrs.tagName = 'select'
        ['items', 'optionDisabled', 'noSelection'].each { attrs.remove(it) }

        fieldImpl(attrs, wrappedBody)

    }

    /**
     * Generates HTML {@code <textarea> } tag.
     * If html escape is enabled, value will be html escaped.
     */
    Closure textarea = { attrs, body ->
        getRequiredAttribute(attrs, 'path', '<g:textarea>')
        attrs.tagName = 'textarea'
        attrs.value = processFieldValue(getName(attrs), getDisplayString(attrs, getBindStatus(attrs).value), attrs.type)
        return fieldImpl(attrs, body)
    }

    /**
     * Determines the display value of the supplied {@code Object},
     * HTML-escaped as required.
     */
    protected String getDisplayString(Map attrs, Object value)
    {
        if (attrs.path)
        {
            PropertyEditor editor = (value != null ? getBindStatus(attrs).findEditor(value.getClass()) : null);
            htmlEscape(attrs, ValueFormatter.getDisplayString(value, editor, false));
        } else
        {
            htmlEscape(attrs, ValueFormatter.getDisplayString(value, false))
        }
    }

    /**
     * Returns '{@code true}' if the bound value requires the
     * resultant '{@code select}' tag to be multi-select.
     */
    protected boolean forceMultiple(Map attrs)
    {
        BindStatus bindStatus = getBindStatus(attrs);
        Class<?> valueType = bindStatus.getValueType();
        if (valueType != null && typeRequiresMultiple(valueType))
        {
            return true;
        } else if (bindStatus.getEditor() != null)
        {
            Object editorValue = bindStatus.getEditor().getValue();
            if (editorValue != null && typeRequiresMultiple(editorValue.getClass()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns '{@code true}' for arrays, {@link Collection Collections}
     * and {@link Map Maps}.
     */
    private static boolean typeRequiresMultiple(Class<?> type)
    {
        return (type.isArray() || Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type));
    }

    /**
     * Checks if the tag attributes contains the given attribute name. If not, throws error. If attribute is found,
     * its value is returned.
     * @param attrs provided tag attributes
     * @param name attribute name to look for
     * @param tagName name of the current tag used to display the error message
     */
    Object getRequiredAttribute(Map attrs, String name, String tagName)
    {
        if (!attrs.containsKey(name))
            throwTagError("Attribute '${name}' is required for '${tagName}'")
        return attrs.get(name)
    }

    /**
     * For internal use
     *
     * Renders the field HTML with common attributes like {@code id} and {@code name}.
     * Most form tag implementations delegates to this method for html rendering and cleanup.
     * @param attrs
     * @param body
     * @return
     */
    protected def fieldImpl(attrs, body)
    {
        def name = getName(attrs)
        def id = resolveId(attrs)

        attrs.remove('path')
        attrs.remove('bindStatus')

        if (name)
            attrs.name = name
        if (id)
            attrs.id = id

        out << g.elm(attrs, body)
    }

    /**
     * Returns the auto generated {@code name} attribute value using the {@code path} attribute.
     * If a custom {@code name} attribute value is provided, then it is used.
     *
     * @param attrs attributes supplied to the tag which calls this method
     */
    protected String getName(Map attrs)
    {
        return attrs.name ?: getPropertyPath(attrs)
    }

    /**
     * Gets the current nested path value set by the {@code form} tag.
     * @return
     */
    String getNestedPath()
    {
        (String) request.getAttribute(NESTED_PATH_VARIABLE_NAME);
    }

    /**
     * Gets the {@code path} attribute value or empty string if no {@code path} is set or its value is null.
     * @param attrs
     */
    String getPath(Map attrs)
    {
        String resolvedPath = (String) attrs.path
        resolvedPath ?: ""
    }

    /**
     * Returns the {@link BindStatus} object for the given <code>path</code>.Resolved value is stored in the
     * {@code attrs} map with the name {@code true} for performance so the tag that uses this method
     * is responsible for removing this attribute before render.
     *
     * @param attrs attributes of the tag which calls this method.
     */
    BindStatus getBindStatus(Map attrs)
    {
        if (attrs.bindStatus)
            return attrs.bindStatus

        String nestedPath = getNestedPath();
        String pathToUse = (nestedPath != null ? nestedPath + getPath(attrs) : getPath(attrs));
        if (pathToUse.endsWith(PropertyAccessor.NESTED_PROPERTY_SEPARATOR))
        {
            pathToUse = pathToUse.substring(0, pathToUse.length() - 1);
        }
        BindStatus bindStatus = new BindStatus(getRequestContext(), pathToUse, false);
        attrs.bindStatus = bindStatus
        return bindStatus;
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

    /**
     * Build the property path for this tag, including the nested path
     * but <i>not</i> prefixed with the name of the form attribute.
     * @see #getNestedPath()
     * @see #getPath()
     */
    protected String getPropertyPath(Map attrs)
    {
        String expression = getBindStatus(attrs).getExpression();
        return (expression != null ? expression : "");
    }

    /**
     * Determine the '{@code id}' attribute value for this tag,
     * autogenerating one if none specified.
     * @see #autogenerateId(java.lang.String)
     */
    protected String resolveId(Map attrs)
    {
        Object id = attrs.id
        if (id != null)
        {
            String idString = id.toString();
            return (StringUtils.hasText(idString) ? idString : null);
        }
        return autogenerateId(getName(attrs));
    }

    /**
     * Autogenerate the '{@code id}' attribute value for this tag.
     * <p>The default implementation simply delegates to the name attribute,
     * deleting invalid characters (such as "[" or "]").
     */
    protected String autogenerateId(String name)
    {
        return StringUtils.deleteAny(name, "[]");
    }

    /**
     * Process the field value with the {@link RequestDataValueProcessor} if one can be resolved from the
     * {@link org.springframework.web.servlet.support.RequestContext#getRequestDataValueProcessor()}
     */
    protected String processFieldValue(String name, String value, String type)
    {
        RequestDataValueProcessor processor = getRequestContext().getRequestDataValueProcessor();
        ServletRequest request = request
        if (processor != null && (request instanceof HttpServletRequest))
        {
            value = processor.processFormFieldValue((HttpServletRequest) request, name, value, type);
        }
        return value;
    }

    /**
     * Determines whether the supplied value matched the selected value
     * through delegating to {@link org.springframework.web.servlet.tags.form.SelectedValueComparator#isSelected}.
     */
    private boolean isOptionSelected(BindStatus bindStatus, Object value)
    {
        return SelectedValueComparator.isSelected(bindStatus, value);
    }

    /**
     * Resolve the value of the '{@code action}' attribute.
     * <p>If the user configured an '{@code action}' value then the result of
     * evaluating this value is used. If the user configured an
     * '{@code servletRelativeAction}' value then the value is prepended
     * with the context and servlet paths, and the result is used. Otherwise, the
     * {@link org.springframework.web.servlet.support.RequestContext#getRequestUri()
     * originating URI} is used.
     * @return the value that is to be used for the '{@code action}' attribute
     */
    protected String resolveFormAction(Map attrs)
    {
        RequestContext requestContext = getRequestContext()
        String action = attrs.remove('action')
        String servletRelativeAction = attrs.remove('servletRelativeAction')
        if (action)
        {
            action = getDisplayString(attrs, action);
            return processAction(attrs, action);
        } else if (StringUtils.hasText(servletRelativeAction))
        {
            String pathToServlet = requestContext.getPathToServlet();
            if (servletRelativeAction.startsWith("/") &&
                    !servletRelativeAction.startsWith(requestContext.getContextPath()))
            {
                servletRelativeAction = pathToServlet + servletRelativeAction;
            }
            servletRelativeAction = getDisplayString(attrs, servletRelativeAction);
            return processAction(attrs,servletRelativeAction);
        } else
        {
            String requestUri = requestContext.getRequestUri();
            String encoding = response.getCharacterEncoding();
            try
            {
                requestUri = UriUtils.encodePath(requestUri, encoding);
            }
            catch (UnsupportedEncodingException ex)
            {
                // shouldn't happen - if it does, proceed with requestUri as-is
            }

            if (response instanceof HttpServletResponse)
            {
                requestUri = ((HttpServletResponse) response).encodeURL(requestUri);
                String queryString = requestContext.getQueryString();
                if (StringUtils.hasText(queryString))
                {
                    requestUri += "?" + htmlEscape(queryString);
                }
            }
            if (StringUtils.hasText(requestUri))
            {
                return processAction(attrs,requestUri);
            } else
            {
                throwTagError("Attribute 'action' is required. " +
                        "Attempted to resolve against current request URI but request URI was null.");
            }
        }
    }

    /**
     * Process the action through a {@link RequestDataValueProcessor} instance
     * if one is configured or otherwise returns the action unmodified.
     */
    private String processAction(Map attrs, String action)
    {
        RequestDataValueProcessor processor = getRequestContext().getRequestDataValueProcessor();
        if (processor != null)
        {
            action = processor.processAction(request, action, getHttpMethod(attrs));
        }
        return action;
    }

    /**
     * Gets the http method attribute for the current form tag.
     * @param attrs current tag attributes
     * @return provided 'method' attribute value. if given value is not 'get' or 'post', returns 'post' by default.
     */
    String getHttpMethod(Map attrs)
    {
        String method = attrs.method ?: ''
        if (!['get', 'post'].any { it.equalsIgnoreCase(method) })
            method = DEFAULT_METHOD
    }
}

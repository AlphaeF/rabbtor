/**
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
package com.rabbtor.web.servlet.support;


import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.*;

/**
 * Mutable request parameters map holder mainly used for server side includes where included request may contain its own custom
 * parameters.
 * <p>Enables using object maps instead of String maps and automatic conversion to String values using a {@link ConversionService}</p>
 * <p>Multi valued collection and array parameter values are also supported which are eventually converted to a String based request
 * parameter map.
 *
 *
 * @see RequestIncludeWrapper
 * @see RequestIncludeHelper
 *
 */
public class RequestParams
{
    private Map<String, Collection<Object>> parameters;

    public RequestParams()
    {
    }

    public RequestParams(Map<String, Object> data)
    {
        put(data);
    }

    private void ensureParameters()
    {
        if (parameters == null)
            parameters = new LinkedHashMap();

    }

    public void put(final Map<String, Object> params)
    {
        IterableUtils.forEach(params.keySet(), new Closure<String>()
        {
            @Override
            public void execute(String key)
            {
                set(key,params.get(key));
            }
        });

    }

    public void set(String paramName, Object value)
    {
        ensureParameters();
        if (value != null)
        {
            Collection<Object> valueAsCollection = valueAsCollection(value);
            parameters.put(paramName, valueAsCollection);
        }
    }

    /**
     * Append new values to a multi-valued request parameter
     * @param paramName
     * @param values
     */
    public void append(String paramName, Object... values)
    {
        ensureParameters();
        Collection<Object> old = parameters.get(paramName);
        if (old == null)
            old = new ArrayList();
        old.addAll(Arrays.asList(values));

        set(paramName, old);
    }

    /**
     * Retrieve parameter values as Object[]
     * @param paramName
     * @return
     */
    public Object[] getParameterValues(String paramName)
    {
        if (parameters == null)
            return null;

        return parameters.get(paramName).toArray();
    }

    /**
     * Retrieves the given object value as a collection. If value type is not already a array or collection,
     * a single item list is returned
     * @param value
     * @return
     */
    private Collection<Object> valueAsCollection(Object value)
    {
        ArrayList list = new ArrayList();

        if (value == null)
            return list;
        if (value instanceof Object[])
            list.addAll(Arrays.asList((Object[]) value));
        else if (value instanceof Collection<?>)
            list.addAll((Collection) value);
        else
            list.add(value);
        return list;
    }

    /**
     * Get the parameter value as Object.If parameter is a multi-valued parameter,
     * first value will be returned.
     * @param paramName
     */
    public Object getParameter(String paramName)
    {
        Object[] values = getParameterValues(paramName);
        if (values != null && values.length > 0)
            return values[0];
        return null;
    }

    /**
     * Get all parameters
     * @return
     */
    public Map<String, Object[]> asMap()
    {

        Map<String, Object[]> result = new LinkedHashMap(parameters == null ? 0 : parameters.size());
        if (parameters != null)
            for (Map.Entry<String,Collection<Object>> entry : parameters.entrySet()) {
                result.put(entry.getKey(),entry.getValue().toArray());
            }
        return result;
    }

    /**
     * Flattens the parameter values map so that, for single-valued parameters, the value is returned.
     * For multi-valued parameters, map entry value will be a collection of objects.
     * @return
     */
    public Map<String, Object> asFlattenedMap()
    {

        Map<String, Object> result = new LinkedHashMap(parameters == null ? 0 : parameters.size());
        if (parameters != null)
            for (Map.Entry<String,Collection<Object>> entry : parameters.entrySet())
            {
                Collection<Object> value = entry.getValue();
                if (!value.isEmpty())
                {
                    if (value.size() > 1)
                        result.put(entry.getKey(), value);
                    else
                        result.put(entry.getKey(), value.iterator().next());

                }
            }
        return result;
    }

    /**
     * Converts all parameter values to String representations and returns a map compatible with the http servlet request implementations.
     * @return parameter values in a standard request parameter format
     */
    public Map<String, String[]> asRequestParameterMap()
    {
        return asRequestParameterMap((ConversionService) null);
    }

    /**
     * Converts all parameter values to String representations and returns a map compatible with the http servlet request implementations.
     * @param conversionService conversion service to be used when converting a object value to String. If null, {@link DefaultConversionService}
     *                          is used.
     * @return parameter values in a standard request parameter format
     */
    public Map<String, String[]> asRequestParameterMap(ConversionService conversionService)
    {
        final ConversionService converter = conversionService == null ? new DefaultConversionService() : conversionService;
        return asRequestParameterMap(new ValueFormatter()
        {
            @Override
            public String apply(Object object)
            {
                return converter.convert(object, String.class);
            }
        });
    }

    /**
     * Converts all parameter values to String representations and returns a map compatible with the http servlet request implementations.
     * @param formatter formatter function to be used when converting a object value to a String. If null, {@link DefaultConversionService}
     *                          is used.
     * @return
     */

    public Map<String, String[]> asRequestParameterMap(final ValueFormatter formatter)
    {
        if (parameters == null)
            return null;

        Map<String, String[]> map = new LinkedHashMap();


        final ConversionService converter = new DefaultConversionService();
        final ValueFormatter format = new ValueFormatter()
        {
            @Override
            public String apply(Object o)
            {
                String str = null;
                if (o instanceof String)
                    str = (String) o;
                else if (formatter != null)
                    str = formatter.apply(o);

                if (str == null)
                    str = converter.convert(o, String.class);
                return str;
            }
        };

        for (String key : parameters.keySet()) {
            Collection<?> values = parameters.get(key);
            List<String> valueStrings = new ArrayList();

            for (Object value : values) {
                valueStrings.add(format.apply(value));
            }
            map.put(key, valueStrings.toArray(new String[valueStrings.size()]));
        }

        return map;
    }

    public MutablePropertyValues asPropertyValues()
    {
        if (parameters == null)
            return new MutablePropertyValues();
        else
            return new MutablePropertyValues(asFlattenedMap());
    }

    public int size()
    {
        if (parameters == null)
            return 0;
        return parameters.size();
    }


    public static interface ValueFormatter {
        String apply(Object object);
    }

}

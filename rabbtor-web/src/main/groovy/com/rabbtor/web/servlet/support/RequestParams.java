package com.rabbtor.web.servlet.support;


import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            parameters = new LinkedHashMap<>();

    }

    public void put(Map<String, Object> params)
    {

        params.keySet().stream().forEach(key ->
                set(key, params.get(key))
        );
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

    public void append(String paramName, Object... values)
    {
        ensureParameters();
        Collection<Object> old = parameters.get(paramName);
        if (old == null)
            old = new ArrayList<>();
        old.addAll(Arrays.asList(values));

        set(paramName, old);
    }

    public Object[] getParameterValues(String paramName)
    {
        if (parameters == null)
            return null;

        return parameters.get(paramName).toArray();
    }

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

    public Object getParameter(String paramName)
    {
        Object[] values = getParameterValues(paramName);
        if (values != null && values.length > 0)
            return values[0];
        return null;
    }

    public Map<String, Object[]> asMap()
    {

        Map<String, Object[]> result = new LinkedHashMap<>(parameters == null ? 0 : parameters.size());
        if (parameters != null)
            parameters.entrySet().stream().forEach(entry ->
                    result.put(entry.getKey(), entry.getValue().toArray())
            );
        return result;
    }

    public Map<String, Object> asFlattenedMap()
    {

        Map<String, Object> result = new LinkedHashMap<>(parameters == null ? 0 : parameters.size());
        if (parameters != null)
            parameters.entrySet().stream().forEach(entry -> {
                        Collection<Object> value = entry.getValue();
                        if (!value.isEmpty())
                        {
                            if (value.size() > 1)
                                result.put(entry.getKey(), value);
                            else
                                result.put(entry.getKey(), value.iterator().next());

                        }

                    }
            );
        return result;
    }

    public Map<String, String[]> asRequestParameterMap()
    {
        return asRequestParameterMap(new DefaultConversionService());
    }

    public Map<String, String[]> asRequestParameterMap(ConversionService conversionService)
    {
        return asRequestParameterMap(o -> conversionService.convert(o, String.class));
    }


    public Map<String, String[]> asRequestParameterMap(final Function<Object, String> formatter)
    {
        if (parameters == null)
            return null;

        Map<String, String[]> map = new LinkedHashMap<>();


        final ConversionService converter = new DefaultConversionService();
        final Function<Object, String> format = o -> {
            String str = null;
            if (o instanceof String)
                str = (String) o;
            else if (formatter != null)
                str = formatter.apply(o);

            if (str == null)
                str = converter.convert(o, String.class);
            return str;

        };


        parameters.keySet().forEach(key -> {
                    Collection<?> values = parameters.get(key);

                    String[] valueStrings = values.stream().map(value -> {
                        String strValue = format.apply(value);
                        return strValue;
                    }).collect(Collectors.toList()).toArray(new String[values.size()]);

                    map.put(key, valueStrings);
                }
        );

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

}

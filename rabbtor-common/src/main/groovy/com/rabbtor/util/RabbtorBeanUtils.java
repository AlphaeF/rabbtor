package com.rabbtor.util;


import org.springframework.core.convert.TypeDescriptor;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class RabbtorBeanUtils
{
    public static Type resolveComponentTypeOfProperty(PropertyDescriptor propertyDescriptor, TypeDescriptor typeDescriptor) {
        Method read = propertyDescriptor.getReadMethod();
        Method write = propertyDescriptor.getWriteMethod();

        ParameterizedType parameterizedType = null;
        if (read != null)
            parameterizedType =(ParameterizedType)read.getGenericReturnType();
        else
            parameterizedType = (ParameterizedType)write.getGenericParameterTypes()[0];

        if (typeDescriptor.isCollection() || typeDescriptor.isArray())
            return parameterizedType.getActualTypeArguments()[0];
        if (typeDescriptor.isMap())
            return parameterizedType.getActualTypeArguments()[1];

        throw new UnsupportedOperationException(String.format("Component type of property could not be resolved for property: %s and type descriptor: %s", propertyDescriptor, typeDescriptor));
    }






}

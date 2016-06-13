package com.rabbtor.web.servlet.model;

import com.rabbtor.model.ModelMetadataAccessor;
import com.rabbtor.model.ModelMetadataAccessorUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;

public class WebModelMetadataUtils
{
    public static final String METADATA_ACCESSOR_VARIABLE_NAME_PREFIX = "modelMetadata.";

    public static ModelMetadataAccessor retrieveMetadataAccessor(Class beanClass, HttpServletRequest request) {
        return retrieveMetadataAccessor(beanClass,request,WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext()));

    }

    public static ModelMetadataAccessor retrieveMetadataAccessor(Class beanClass, HttpServletRequest request, ListableBeanFactory beanFactory) {
        String variableName = METADATA_ACCESSOR_VARIABLE_NAME_PREFIX + beanClass.getName();
        ModelMetadataAccessor accessor = (ModelMetadataAccessor) request.getAttribute(variableName);
        if (accessor == null)
        {
            accessor = ModelMetadataAccessorUtils.lookup(beanClass, beanFactory);
            storeMetadataAccessor(request,accessor);
        }
        return accessor;
    }

    private static void storeMetadataAccessor(HttpServletRequest request,ModelMetadataAccessor accessor)
    {
        String variableName = METADATA_ACCESSOR_VARIABLE_NAME_PREFIX + accessor.getModelType().getName();
        request.setAttribute(variableName,accessor);

    }
}

/*
 * Copyright 2002-2012 the original author or authors.
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
/* Modifications Copyright 2016 - Rabbytes Inc */
package com.rabbtor.validation;

import com.rabbtor.model.ModelMetadataAccessor;
import com.rabbtor.model.ModelMetadataAccessorFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.PropertyAccessException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DefaultBindingErrorProcessor;
import org.springframework.validation.FieldError;

public class MetadataAwareBindingErrorProcessor extends DefaultBindingErrorProcessor
{
    private ModelMetadataAccessorFactory modelMetadataAccessorFactory;
    Log LOG = LogFactory.getLog(MetadataAwareBindingErrorProcessor.class);

    public MetadataAwareBindingErrorProcessor(ModelMetadataAccessorFactory modelMetadataAccessorFactory)
    {
        this.modelMetadataAccessorFactory = modelMetadataAccessorFactory;
    }

    public ModelMetadataAccessorFactory getModelMetadataAccessorFactory()
    {
        return modelMetadataAccessorFactory;
    }


    @Override
    public void processMissingFieldError(String missingField, BindingResult bindingResult) {
        // Create field error with code "required".
        String fixedField = bindingResult.getNestedPath() + missingField;
        String[] codes = bindingResult.resolveMessageCodes(MISSING_FIELD_ERROR_CODE, missingField);
        Object[] arguments = getArgumentsForBindError(bindingResult.getObjectName(), fixedField);
        arguments = generateMetadataArgumentsForBindError(arguments,bindingResult,missingField);
        bindingResult.addError(new FieldError(
                bindingResult.getObjectName(), fixedField, "", true,
                codes, arguments, "Field '" + fixedField + "' is required"));
    }

    @Override
    public void processPropertyAccessException(PropertyAccessException ex, BindingResult bindingResult) {
        // Create field error with the exceptions's code, e.g. "typeMismatch".
        String field = ex.getPropertyName();
        String[] codes = bindingResult.resolveMessageCodes(ex.getErrorCode(), field);
        Object[] arguments = getArgumentsForBindError(bindingResult.getObjectName(), field);
        arguments = generateMetadataArgumentsForBindError(arguments,bindingResult,field);
        Object rejectedValue = ex.getValue();
        if (rejectedValue != null && rejectedValue.getClass().isArray()) {
            rejectedValue = StringUtils.arrayToCommaDelimitedString(ObjectUtils.toObjectArray(rejectedValue));
        }
        bindingResult.addError(new FieldError(
                bindingResult.getObjectName(), field, rejectedValue, true,
                codes, arguments, ex.getLocalizedMessage()));
    }

    private Object[] generateMetadataArgumentsForBindError(Object[] args, BindingResult bindingResult, String field)
    {
        if (modelMetadataAccessorFactory == null)
            return args;

        Object target = bindingResult.getTarget();
        if (target == null)
            return args;

        try
        {
            ModelMetadataAccessor metadataAccessor = modelMetadataAccessorFactory.getMetadataAccessor(target.getClass());

            // Add a custom message code for the field name
            if (args.length > 0 && args[0] instanceof DefaultMessageSourceResolvable)
            {
                String nestedField = bindingResult.getNestedPath() + field;
                if (!("".equals(nestedField)))
                {
                    String[] codes = metadataAccessor.getModelNameCodes(nestedField, bindingResult.getObjectName());
                    String defaultDisplayName = metadataAccessor.getDisplayName(nestedField);
                    if (defaultDisplayName == null)
                        defaultDisplayName = field;
                    args[0] = new DefaultMessageSourceResolvable(codes, defaultDisplayName);
                }
            }

        } catch (Exception ex)
        {
            LOG.error("Error retrieving model metadata for field [$field] for object [$target] in class [${target.getClass()}]");
        }

        return args;
    }


}

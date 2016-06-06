
package com.rabbtor.validation

import com.rabbtor.model.DefaultModelMetadataAccessor
import com.rabbtor.model.DefaultModelMetadataAccessorFactory
import com.rabbtor.model.DefaultModelMetadataRegistry
import com.rabbtor.model.ModelMetadataAccessor
import com.rabbtor.model.ModelMetadataAccessorFactory
import com.rabbtor.model.ModelMetadataProvider
import com.rabbtor.model.ModelMetadataRegistry
import com.rabbtor.model.ModelPropertyMetadata
import groovy.transform.CompileStatic
import groovy.util.logging.Commons
import org.springframework.beans.NotReadablePropertyException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.validation.BindingResult
import org.springframework.validation.Errors
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

import javax.validation.ConstraintViolation
import javax.validation.metadata.ConstraintDescriptor

@CompileStatic
@Commons
public class RabbtorLocalValidatorFactoryBean extends LocalValidatorFactoryBean
{

    private ModelMetadataAccessorFactory modelMetadataAccessorFactory

    RabbtorLocalValidatorFactoryBean()
    {
        this(new DefaultModelMetadataAccessorFactory())
    }

    RabbtorLocalValidatorFactoryBean(ModelMetadataAccessorFactory modelMetadataAccessorFactory)
    {
        this.modelMetadataAccessorFactory = modelMetadataAccessorFactory
    }

    ModelMetadataAccessorFactory getModelMetadataAccessorFactory()
    {
        return modelMetadataAccessorFactory
    }

    @Autowired(required = false)
    void setModelMetadataAccessorFactory(ModelMetadataAccessorFactory modelMetadataAccessorFactory)
    {
        this.modelMetadataAccessorFactory = modelMetadataAccessorFactory
    }

    @Override
    protected void processConstraintViolations(Set<ConstraintViolation<Object>> violations, Errors errors) {
        for (ConstraintViolation<Object> violation : violations) {
            String field = determineField(violation);
            FieldError fieldError = errors.getFieldError(field);
            if (fieldError == null || !fieldError.isBindingFailure()) {
                try {
                    ConstraintDescriptor<?> cd = violation.getConstraintDescriptor();
                    String errorCode = determineErrorCode(cd);
                    Object[] errorArgs = getArgumentsForConstraint(errors, field, cd);
                    if (errors instanceof BindingResult) {
                        // Can do custom FieldError registration with invalid value from ConstraintViolation,
                        // as necessary for Hibernate Validator compatibility (non-indexed set path in field)
                        BindingResult bindingResult = (BindingResult) errors;
                        String nestedField = bindingResult.getNestedPath() + field;
                        if ("".equals(nestedField)) {
                            String[] errorCodes = bindingResult.resolveMessageCodes(errorCode);
                            bindingResult.addError(new ObjectError(
                                    errors.getObjectName(), errorCodes, errorArgs, violation.getMessage()));
                        }
                        else {
                            Object rejectedValue = getRejectedValue(field, violation, bindingResult);
                            String[] errorCodes = bindingResult.resolveMessageCodes(errorCode, field);
                            bindingResult.addError(new FieldError(
                                    errors.getObjectName(), nestedField, rejectedValue, false,
                                    errorCodes, errorArgs, violation.getMessage()));
                        }
                    }
                    else {
                        // got no BindingResult - can only do standard rejectValue call
                        // with automatic extraction of the current field value
                        errors.rejectValue(field, errorCode, errorArgs, violation.getMessage());
                    }
                }
                catch (NotReadablePropertyException ex) {
                    throw new IllegalStateException("JSR-303 validated property '" + field +
                            "' does not have a corresponding accessor for Spring data binding - " +
                            "check your DataBinder's configuration (bean property versus direct field access)", ex);
                }
            }
        }
    }

    protected Object[] getArgumentsForConstraint(Errors errors, String field, ConstraintDescriptor<?> descriptor)
    {


        Object[] args = getArgumentsForConstraint(errors.getObjectName(), field, descriptor);

        if (!errors instanceof BindingResult)
            return args;

        BindingResult bindingResult = (BindingResult)errors;
        Object target = bindingResult.getTarget()
        if (target == null)
            return args

        if (modelMetadataAccessorFactory == null)
            return args

        try {
            ModelMetadataAccessor metadataAccessor = modelMetadataAccessorFactory.getMetadataAccessor(target.getClass())

            // Add a custom message code for the field name
            if (args.length > 0 && args[0] instanceof DefaultMessageSourceResolvable) {
                String nestedField = bindingResult.getNestedPath() + field;
                if (!("".equals(nestedField))) {
                    def codes = metadataAccessor.getModelNameCodes(nestedField,errors.getObjectName())
                    args[0] = new DefaultMessageSourceResolvable(codes as String[], metadataAccessor.getDisplayName(nestedField) ?: field);
                }
            }

        } catch (Exception ex) {
            log.error("Error retrieving model metadata for field [$field] for object [$target] in class [${target.getClass()}]")
        }

        return args;

    }
}

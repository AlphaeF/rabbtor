
package com.rabbtor.validation

import groovy.transform.CompileStatic
import org.apache.commons.logging.LogFactory
import org.springframework.validation.beanvalidation.OptionalValidatorFactoryBean

import javax.validation.ValidationException

@CompileStatic
class RabbtorOptionalValidatorFactoryBean extends RabbtorLocalValidatorFactoryBean
{
    @Override
    public void afterPropertiesSet() {
        try {
            super.afterPropertiesSet();
        }
        catch (ValidationException ex) {
            LogFactory.getLog(getClass()).debug("Failed to set up a Bean Validation modelMetadataRegistry", ex);
        }
    }
}

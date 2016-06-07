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

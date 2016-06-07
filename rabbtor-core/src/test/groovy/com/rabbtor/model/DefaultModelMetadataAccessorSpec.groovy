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
package com.rabbtor.model

import com.rabbtor.model.annotation.AnnotationModelMetadataProvider
import spock.lang.Specification


class DefaultModelMetadataAccessorSpec extends Specification
{
    def provider = new AnnotationModelMetadataProvider()


    def 'test get model meta'()
    {
        given:
        def accessor = new DefaultModelMetadataAccessor(PersonModel)
        when:
        def addressMap = accessor.getPropertyMetadata('addressMap')
        def addressMapItem = accessor.getPropertyMetadata('addressMap[].zipCode')
        def address = accessor.getPropertyMetadata('address')
        def zipCode = accessor.getPropertyMetadata('address.zipCode')
        def addresses = accessor.getPropertyMetadata('addresses')
        def addressesItem = accessor.getPropertyMetadata('addresses[].zipCode')

        then:
        addressMap != null
        addressMapItem != null
        address != null
        zipCode != null
        addresses != null
        addressesItem != null
    }
}

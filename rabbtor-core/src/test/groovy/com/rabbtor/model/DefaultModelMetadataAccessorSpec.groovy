
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


package com.rabbtor.model

import com.rabbtor.model.annotation.AnnotationModelMetadataProvider
import org.springframework.beans.BeanUtils
import spock.lang.Shared
import spock.lang.Specification

import java.beans.PropertyDescriptor


class AnnotationModelMetadataSpec extends Specification
{
    private @Shared
    ModelMetadataProvider provider
    private @Shared
    PropertyDescriptor[] personProperties
    private @Shared
    ModelMetadata personMeta
    private @Shared
    ModelMetadata driverMeta
    private @Shared
    ModelMetadata pilotMeta

    def setupSpec()
    {
        provider = new AnnotationModelMetadataProvider()
        personProperties = BeanUtils.getPropertyDescriptors(PersonModel)
                .findAll { it.name != 'class' }
        personMeta = provider.getModelMetadata(PersonModel)
        driverMeta = provider.getModelMetadata(DriverModel)
        pilotMeta = provider.getModelMetadata(PilotModel)
    }

    def setup()
    {

    }


    def 'test only public props'()
    {
        when:
        true
        then:
        // 'class' property is not returned so size will be 1 less than the bean props
        personMeta.properties.size() == personProperties.size()
    }

    def 'test readonly'()
    {
        when:
        true
        then:
        personMeta.properties.count { it.readOnly } == personProperties.count { !it.writeMethod }
    }

    def 'test primitives'()
    {
        when:
        def primitives = personMeta.properties.findAll { it.primitive }
        then:
        primitives.size() == 1
    }

    def 'test collections'()
    {
        when:
        def collections = personMeta.properties.findAll { it.collection }
        then:
        collections.size() == 3
    }

    def 'test component types of collections'()
    {
        when:
        def addressProp = personMeta.properties.find { it.propertyName == 'addresses' }
        def rolesProp = personMeta.properties.find { it.propertyName == 'roles' }
        def runsProp = personMeta.properties.find { it.propertyName == 'runs' }

        then:
        addressProp.componentType == AddressModel
        rolesProp.componentType == String
        runsProp.componentType == Double
    }

    def 'test inheritance'()
    {
        when:
        true
        then:
        pilotMeta.properties.size() == personMeta.properties.size()
        driverMeta.properties.size() > personMeta.properties.size()
    }

    def 'test model names'()
    {
        when:
        def nameProp = personMeta.properties.find { it.propertyName == 'name' }
        def pilotNameProp = pilotMeta.properties.find { it.propertyName == 'name' }
        def dateOfBirthProp = personMeta.properties.find { it.propertyName == 'dateOfBirth' }
        def pilotDateOfBirthProp = pilotMeta.properties.find { it.propertyName == 'dateOfBirth' }


        then:
        personMeta.modelName == 'person'
        driverMeta.modelName == 'driverModel'
        pilotMeta.modelName == 'pilot'

        // getter method overrides field
        nameProp.displayName == 'getName'

        // pilot overrides person
        pilotNameProp.displayName == 'Pilot Name'

        // display name and key must be null if not set
        dateOfBirthProp.displayName == null
        dateOfBirthProp.modelName == null

        // but overridable
        pilotDateOfBirthProp.displayName == "Date of Birth"
        pilotDateOfBirthProp.modelName == "dateofbirth"


    }

}

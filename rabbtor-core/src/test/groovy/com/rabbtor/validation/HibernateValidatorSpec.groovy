
package com.rabbtor.validation

import spock.lang.Specification

import javax.validation.Valid
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory
import javax.validation.constraints.NotNull

class HibernateValidatorSpec extends Specification
{
    ValidatorFactory factory;
    Validator validator;

    def setup() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    def 'test get constraints'() {
        when:
        def bean = validator.getConstraintsForClass(Person)
        then:
        bean.isBeanConstrained()


    }


    public static class Person {
        @NotNull
        Long id

        @NotNull
        @Valid
        List<Address> names;
    }

    public static class Address {

        @NotNull
        String zipCode

    }

}

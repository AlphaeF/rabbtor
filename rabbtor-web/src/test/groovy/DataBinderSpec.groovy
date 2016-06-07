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
import com.rabbtor.web.servlet.support.RequestParams
import org.springframework.beans.MutablePropertyValues
import org.springframework.validation.DataBinder
import spock.lang.Specification


class DataBinderSpec extends Specification
{
    def 'test property accessors'()
    {
        given:
        def model = new Person()
        def binder = new DataBinder(model)
        MutablePropertyValues propertyValues = null
        RequestParams requestParams = null

        when:
        requestParams = new RequestParams([name:['joe'], roles:['user', 'admin']])
        binder.bind(requestParams.asPropertyValues())
        def bindingResult = binder.getBindingResult()

        then:
        bindingResult.hasErrors() == false
        model.name == 'joe'
        model.roles == requestParams.getParameterValues('roles') as String[]

        when:
        requestParams = new RequestParams([name:'joe', 'roles[0]':'user', 'roles[1]':'admin', 'address.zipCode':'00001'])
        binder.bind(requestParams.asPropertyValues())
        bindingResult = binder.getBindingResult()

        then:
        model.roles == ['user','admin'] as String[]
        model.address
        model.address.zipCode == requestParams.getParameter('address.zipCode')

        when:
        requestParams = new RequestParams([id:'test'])
        binder.bind(requestParams.asPropertyValues())
        bindingResult = binder.getBindingResult()

        then:
        bindingResult.hasErrors()
        bindingResult.hasFieldErrors('id')



    }


    public static class Person
    {
        String name
        Long id
        long age

        Address address
        String[] roles
        List<String> locations
        Set<Integer> runs

        Date registered
    }

    public static class Address
    {
        String zipCode
        double lattitude
        Double longitude
    }
}

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
package com.rabbtor.gsp.tags

import com.rabbtor.gsp.taglib.AbstractTagLibTests
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.BindingResult
import org.springframework.validation.Validator

@RunWith(SpringJUnit4ClassRunner)
class ValidationTagLibTests extends AbstractTagLibTests
{
    @Autowired
    Validator validator

    @Test
    public void testMessage()
    {
        assert executeTemplate("<g:message code='name' />").contains('foo')
        assert executeTemplate("<g:message code='missing' default='defaultMessage' />").contains('defaultMessage')
        assert executeTemplate("<g:message code='missing' text='defaultMessage' />").contains('defaultMessage')

        def out = executeTemplate("<g:message code='name' text='defaultMessage' />")
        assert out.contains('foo') && !out.contains('defaultMessage')

        // by default, output must be raw
        assert executeTemplate("<g:message code='sample.html' />").contains('<')

        // test html escape
        out = executeTemplate("<g:message code='sample.html' escape='HTML' />")
        assert out.trim() == applicationContext.getMessage("sample.html.encoded", null, null)

        out = executeTemplate("<g:message code='sample.javascript' escape='Js' />")
        assert out.trim() == applicationContext.getMessage("sample.javascript.encoded", null, null)

        // test args
        out = executeTemplate("<g:message code='msgWithArgs' args=\"[10,'test']\" />")
        assert out.trim() == applicationContext.getMessage("msgWithArgs", [10, 'test'] as Object[], null)

        // args and html escape
        out = executeTemplate("<g:message code='msgWithArgs' args=\"[10,'test']\" escape='HTML' />")
        assert out.trim() == applicationContext.getMessage("msgWithArgs.html.encoded", [10, 'test'] as Object[], null)

        // args with default message instead of code
        out = executeTemplate("<g:message code='missing' default='{0}-{1}' args=\"[10,'test']\" />")
        assert out.trim() == applicationContext.getMessage("missing", [10, 'test'] as Object[], "{0}-{1}", Locale.US)

        // test message tag as call
        executeTemplate('${g.message(code:"name")}').trim() == applicationContext.getMessage('name', null, null)
        executeTemplate('${g.message(code:"sample.html",htmlEscape:true)}').trim() == applicationContext.getMessage('sample.html.encoded', null, null)

    }

    @Test
    public void testIfMissingMessageThrowsError()
    {
        thrown.expect(Exception)
        executeTemplate("<g:message code='missing' />")
    }

    @Test
    public void testEachError() {
        def cmd = new PersonCommand()
        cmd.addresses = [new AddressCommand()]

        def errors = new BeanPropertyBindingResult(cmd,"cmd")
        errors.rejectValue('name','null','Name null')
        errors.rejectValue('addresses[0].name','null', 'Address id null')



        request.setAttribute('cmd',cmd)
        request.setAttribute(BindingResult.MODEL_KEY_PREFIX+'cmd',errors)

        String template = '''
            <g:bind path="cmd.*" >
                <g:eachError>
                    <li>${it}</li>
                </g:eachError>
            </g:bind>
        '''

        String out = executeTemplate(template)
        def doc = parseDoc(out)

        def items = doc.getElementsByTag('li')
        assert items.size() == errors.getAllErrors().size()
        assert items[0].html().trim() == errors.getFieldError('name').toString()
        assert items[1].html().trim() == errors.getFieldError('addresses[0].name').toString()
    }

    @Test
    public void testEachErrorWithMessage() {
        def cmd = new PersonCommand()
        cmd.addresses = [new AddressCommand()]

        def errors = new BeanPropertyBindingResult(cmd,"cmd")
        errors.rejectValue( 'name','null',['name'] as Object[],'Name null')
        errors.rejectValue('addresses[0].name','null',['address name'] as Object[], 'Address name null')

        request.setAttribute('cmd',cmd)
        request.setAttribute(BindingResult.MODEL_KEY_PREFIX+'cmd',errors)

        String template = '''
            <g:bind path="cmd.*" >
                <g:eachError>
                    <li><g:message message="${it}" /></li>
                </g:eachError>
            </g:bind>
        '''

        String out = executeTemplate(template)
        def doc = parseDoc(out)

        def items = doc.getElementsByTag('li')
        assert items.size() == errors.getAllErrors().size()
        assert items[0].html().trim() == applicationContext.getMessage("null",['name'] as Object[], null)
        assert items[1].html().trim() == applicationContext.getMessage("null",['address name'] as Object[], null)
    }



}

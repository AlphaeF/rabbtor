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
import com.rabbtor.model.annotation.DisplayName
import com.rabbtor.model.annotation.Model
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Entities
import org.jsoup.parser.Parser
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.web.util.HtmlUtils

@RunWith(SpringJUnit4ClassRunner)
class FormTagLibTests extends AbstractTagLibTests
{
    CarCommand cmd

    @Override
    void setup()
    {
        super.setup()
        writer.buffer.length = 0

        request.setContextPath("/myapp")
        request.setRequestURI("/myapp/index")
        request.setQueryString("a=1&b=2")

        cmd = new CarCommand()
        cmd.parts << new CarPartCommand()
        cmd.parts[0].dateProduced = cmd.dateProduced
        cmd.parts[0].id = 99

        request.setAttribute("carCommand", cmd)
    }

    @Test
    public void testFormAction()
    {

        def forms = executeTemplateAndParse('''
            <g:form class="cls" id="myForm" />
            <g:form action="/go" class="cls" id="myForm" />
            <g:form action="~/go" class="cls" id="myForm" />
            <g:form servletRelativeAction="/go" class="cls" id="myForm" />
''').getElementsByTag('form')

        assert forms[0].attr('action') == request.getRequestURI() + (request.queryString ? '?' + request.queryString : '')
        assert forms[1].attr('action') == '/go'
        assert forms[2].attr('action') == '/myapp/go'
        assert forms[3].attr('action') == '/myapp/go'

        assert forms.every { it.attr('class') == 'cls' && it.attr('id') == 'myForm' }
    }

    @Test
    public void testInputsWithDefaults()
    {

        String template = '''
            <g:input path="brand" />
            <g:input type="text" path="brand" />
            <g:input type="password" path="brand" />
            <g:checkbox path="brand" value="test" />
            <g:radio path="brand" value="test" />
            <g:hidden path="brand" />
        '''
        String out = executeTemplateWithinForm(template)
        def inputs = Jsoup.parse(out).getElementsByTag('input')

        // all must be inputs
        assert inputs.size() == 6

    }

    @Test public void testInputIdsAndNames() {
        String template = '''
            <g:input path="parts[0].id" />
            <g:input path="parts[0].id" id="customId" />
            <g:input path="parts[0].id" name="customName" />
            <g:input path="parts[0].id" name="customName" id="customId" />
        '''
        String out = executeTemplateWithinForm(template)
        def inputs = Jsoup.parse(out).getElementsByTag('input')
        assert inputs[0].id() == 'parts0.id'
        assert inputs[1].id() == 'customId'
        assert inputs[2].id() == 'customName'
        assert inputs[3].id() == 'customId'

        assert inputs[0].attr('name') == 'parts[0].id'
        assert inputs[1].attr('name') == 'parts[0].id'
        assert inputs[2].attr('name') == 'customName'
        assert inputs[3].attr('name') == 'customName'

    }

    @Test public void testDisabledAndReadonly() {
        String template = '''
            <g:input path="brand" disabled="" readonly="" />
            <g:input path="brand" disabled="true" readonly="true" />
            <g:input path="brand" disabled="false" readonly="false" />
            <g:input path="brand" disabled="dummy" readonly="dummy" />
        '''
        String out = executeTemplateWithinForm(template)
        def inputs = Jsoup.parse(out).getElementsByTag('input')

        assert inputs.count { it.attr('disabled') == 'disabled'} == 2
        assert inputs.count { it.attr('readonly') == 'readonly'} == 2

        assert !inputs[2].hasAttr('disabled') && !inputs[2].attr('readonly')
    }

    @Test public void testIfHtmlEscapedByDefault() {
        cmd.brand = "<b>Brand</b>"

        String template = '''
            <g:input path="brand"  />
            <g:input type='password' path="brand"  />
            <g:checkbox path="brand" value="${carCommand.brand}" />
            <g:radio path="brand" value="${carCommand.brand}" />
            <g:textarea path="brand">${carCommand.brand}</g:textarea>" />
        '''
        String out = executeTemplateWithinForm(template)

        assert !out.contains(cmd.brand)
        assert out.contains("value=\"${HtmlUtils.htmlEscape(cmd.brand)}\"")

    }

    @Test public void testIfHtmlEscapeSettingWorks() {
        cmd.brand = "<b>Brand</b>"

        String template = '''
            <g:input path="brand" htmlEscape="false"  />
            <g:input type='password' path="brand" htmlEscape="false"  />
            <g:checkbox path="brand" value="${carCommand.brand}" htmlEscape="false" />
            <g:radio path="brand" value="${carCommand.brand}" htmlEscape="false" />
            <g:textarea path="brand" value="${carCommand.brand}" htmlEscape="false" />
        '''
        String out = executeTemplateWithinForm(template)

        assert out.contains("value=\"${cmd.brand}\"")
        assert !out.contains("value=\"${HtmlUtils.htmlEscape(cmd.brand)}\"")

    }


    @Test
    void testInputPassword()
    {
        cmd.brand = "<b>Brand</b>"
        cmd.produced = true


        String template = '''
            <g:input type='password' path="brand" />
            <g:input type='password' path="brand" show="" />
            <g:input type='password' path="brand" show="true" />
            <g:input type='password' path="brand" show="false" />
            <g:input type='password' path="brand" showPassword="" />
            <g:input type='password' path="brand" showPassword="false" />
            <g:input type='password' path="brand" show="true" htmlEscape='false' />
        '''
        String out = executeTemplateWithinForm(template)
        def inputs = Jsoup.parse(out).getElementsByTag('input')

        assert inputs[0].val() == ''
        assert inputs[3].val() == ''
        assert inputs[5].val() == ''
        assert inputs[1].val() != ''
        assert inputs[2].val() != ''
        assert inputs[4].val() != ''

        assert out.contains(HtmlUtils.htmlEscape(cmd.brand))
        assert out.contains(cmd.brand)

    }

    @Test void testUniqueIdGenerationForCheckboxAndRadios() {
        String template = '''
            <g:checkbox path="id" value="10" />
            <g:checkbox path="id" value="11" />
            <g:checkbox path="id" value="12" />
            <g:radio path="id" value="10" />
            <g:radio path="id" value="11" />
            <g:radio path="id" value="12" />

        '''
        String out = executeTemplateWithinForm(template)
        def inputs = Jsoup.parse(out).getElementsByTag('input')
            .findAll { it.attr('type') != 'hidden'}

        def ids = inputs.collect { it.attr('id') }
        assert ids.size() == ids.unique().size()
    }

    @Test void testCheckboxForBooleans() {
        cmd.produced = true
        cmd.onSale = null
        cmd.hasDiscount = false

        String template = '''
            <div id="produced" >
                <g:checkbox path="produced"  />
                <g:checkbox path="produced" value="false" />
                <g:checkbox path="produced" value="${false}" />
                <g:checkbox path="produced" value="anyvalue" />
            </div>

            <div id="onSale" >
                <g:checkbox path="onSale"  />
                <g:checkbox path="onSale" value="false" />
                <g:checkbox path="onSale" value="${false}" />
                <g:checkbox path="onSale" value="anyvalue" />
            </div>

            <div id="hasDiscount" >
                <g:checkbox path="hasDiscount"  />
                <g:checkbox path="hasDiscount" value="false" />
                <g:checkbox path="hasDiscount" value="${false}" />
                <g:checkbox path="hasDiscount" value="anyvalue" />
            </div>

        '''
        String out = executeTemplateWithinForm(template)
        def produced = Jsoup.parse(out).getElementById('produced').getElementsByTag('input').findAll { !it.attr('type') == 'hidden'}
        def sale = Jsoup.parse(out).getElementById('onSale').getElementsByTag('input').findAll { !it.attr('type') == 'hidden'}
        def discount = Jsoup.parse(out).getElementById('hasDiscount').getElementsByTag('input').findAll { !it.attr('type') == 'hidden'}

        assert produced.every {
            it.attr('checked') == 'checked'
            it.val() == 'true'
        }

        assert sale.every {
            !it.hasAttr('checked')
            it.val() == 'true'
        }

        assert discount.every {
            !it.hasAttr('checked')
            it.val() == 'true'
        }

    }




    @Test
    void testValueMatchingForBooleanStrings()
    {
        cmd.brand = 'true'

        String template = '''
            <g:checkbox path="brand" value="true" />
            <g:radio path="brand" value="true" />
            <g:checkbox path="brand" value="anyvalue" />
            <g:radio path="brand" value="anyvalue" />
        '''
        String out = executeTemplateWithinForm(template)
        def inputs = Jsoup.parse(out).getElementsByTag('input')
                .findAll { it.attr('type') != 'hidden' }

        assert inputs.count { it.attr('checked') == 'checked'} == 2
        assert inputs.count { !it.hasAttr('checked') } == 2
    }

    @Test
    void testValueMatchingForCheckboxAndRadio()
    {


        String template = '''
            <div id="checked" >
                <g:checkbox path="brand" value="${carCommand.brand}" />
                <g:checkbox path="dateProduced" value="${carCommand.dateProduced}" />
                <g:checkbox path="dateProduced" value="${carCommand.dateProduced.toString()}" />
                <g:checkbox path="uniqueId" value="${carCommand.uniqueId}" />
                <g:checkbox path="uniqueId" value="${carCommand.uniqueId.toString()}" />

                <g:radio path="brand" value="${carCommand.brand}" />
                <g:radio path="dateProduced" value="${carCommand.dateProduced}" />
                <g:radio path="dateProduced" value="${carCommand.dateProduced.toString()}" />
                <g:radio path="uniqueId" value="${carCommand.uniqueId}" />
                <g:radio path="uniqueId" value="${carCommand.uniqueId.toString()}" />

            </div>
            <div id="unchecked" >
                <g:checkbox path="brand" value="test" />
                <g:checkbox path="dateProduced" value="test" />
                <g:checkbox path="dateProduced" value="${date}" />
                <g:checkbox path="uniqueId" value="test" />
                <g:checkbox path="uniqueId" value="${uid}" />

                <g:radio path="brand" value="test" />
                <g:radio path="dateProduced" value="test" />
                <g:radio path="dateProduced" value="${date}" />
                <g:radio path="uniqueId" value="test" />
                <g:radio path="uniqueId" value="${uid}" />
            </div>

        '''
        Calendar cal = Calendar.instance
        cal.add(Calendar.YEAR,1)
        def time = cal.getTime()

        String out = executeTemplateWithinForm(template, [date: time, uid: UUID.randomUUID()])
        def checked = Jsoup.parse(out).getElementById('checked').getElementsByTag('input')
                .findAll { it.attr('type') != 'hidden' }
        def unchecked = Jsoup.parse(out).getElementById('unchecked').getElementsByTag('input')
                .findAll { it.attr('type') != 'hidden' }


        assert checked.every { it.attr('checked') == 'checked'}
        assert unchecked.every { !it.hasAttr('checked')}
    }


    @Test
    void testRadio()
    {
        cmd.produced = true

        String template = '''
            <g:input type='radio' path="produced" value="true" />
            <g:input type='radio' path="parts[0].id" value="80" />
            <g:input type='radio' path="parts[0].id" value="99" />
            <g:input type='radio' path="brand" value="${brand}" />
        '''
        String out = executeTemplateWithinForm(template, [brand: cmd.brand])
        def inputs = parseDoc(out).getElementsByTag('input')
        assert inputs.every { it.attr('type') == 'radio'}
        assert inputs.count { it.attr('checked') == 'checked'} == 3
    }


    @Test
    void testLabel() {
        cmd.brand = "<b>Brand</b>"

        String template = '''
            <g:label path="id"  />
            <g:label path="brand"  />
            <g:label path="parts[0].id"  />
            <g:label path="dateProduced" />
            <g:label path="dateProduced" htmlEscape="false" />
        '''
        String out = executeTemplateWithinForm(template)
        def labels = Jsoup.parse(out).getElementsByTag('label')

        assert labels.size() == 5
        assert labels[0].attr('for') == 'id'
        assert labels[1].attr('for') == 'brand'
        assert labels[2].attr('for') == 'parts0.id'

        assert labels[0].html().trim() == "Car Id"

        // Although there is a message "car.brand", "carCommand.brand" overrides it
        assert labels[1].html().trim() == applicationContext.getMessage("carCommand.brand",null,null)
        assert labels[2].html().trim() == "Part Number"
        assert labels[3].html().trim() == HtmlUtils.htmlEscape(applicationContext.getMessage("car.dateProduced",null,null))
        assert labels[4].html().trim() == applicationContext.getMessage("car.dateProduced",null,null)
    }

    @Test
    void testSelect() {
        def binding = [:]
        binding.factories = (1..10).collect {
            new CarFactoryCommand((Long)it,"<strong>Factory${it}</strong>")
        }

        cmd.factory = new CarFactoryCommand(5L,"Factory5")

        String template = '''
            <g:select path="factory.id" items="${factories}" itemValue="id" itemLabel="name" />
            <g:select path="factory.id" items="${factories}" itemValue="id" itemLabel="name" noSelection="['':'Please select']" />
            <g:select path="factory.id" items="${factories}" itemValue="id" itemLabel="name" htmlEscape="false" />
            <g:select path="factory.id" items="${factories}"  />
        '''
        String out = executeTemplateWithinForm(template,binding)
        def doc = Jsoup.parse(out,'',Parser.xmlParser())
        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml)


        def selects = doc.getElementsByTag('select')

        assert selects[0].getElementsByTag('option').size() == binding.factories.size()
        assert selects[1].getElementsByTag('option').size() == binding.factories.size() + 1

        def option4 = selects[0].getElementsByTag('option')[3]
        assert option4.val() == binding.factories[3].id.toString()
        assert option4.html().trim() == HtmlUtils.htmlEscape(binding.factories[3].name)

        def optionHtml = selects[2].getElementsByTag('option')[3]
        assert optionHtml.val() == binding.factories[3].id.toString()
        assert optionHtml.html().trim() == binding.factories[3].name

        assert selects[0].getElementsByTag('option').find { it.val() == '5'}.attr('selected') == 'selected'
        assert selects[0].getElementsByTag('option').find { it.val() != '5'}.hasAttr('selected') == false

        // item converts to string
       assert out.contains('value="'+ HtmlUtils.htmlEscape(binding.factories[0].toString()) + '"')
        assert out.contains('>'+ HtmlUtils.htmlEscape(binding.factories[0].toString()) + '</option>')
    }

    @Test
    void testWithModel() {
        cmd.brand = "<b>Brand</b>"

        String template = '''
            <g:withModel modelAttribute="carCommand" >
                <g:label path="id"  />
                <g:label path="brand"  />
                <g:label path="parts[0].id"  />
                <g:label path="dateProduced" />
                <g:label path="dateProduced" htmlEscape="false" />
            </g:withModel>
        '''


        String out = executeTemplate(template)
        def labels = Jsoup.parse(out).getElementsByTag('label')

        assert labels.size() == 5
        assert labels[0].attr('for') == 'id'
        assert labels[1].attr('for') == 'brand'
        assert labels[2].attr('for') == 'parts0.id'

        assert labels[0].html().trim() == "Car Id"

        // Although there is a message "car.brand", "carCommand.brand" overrides it
        assert labels[1].html().trim() == applicationContext.getMessage("carCommand.brand",null,null)
        assert labels[2].html().trim() == "Part Number"
        assert labels[3].html().trim() == HtmlUtils.htmlEscape(applicationContext.getMessage("car.dateProduced",null,null))
        assert labels[4].html().trim() == applicationContext.getMessage("car.dateProduced",null,null)
    }


    @Test
    void testSelectWithMaps() {
        def binding = [:]
        binding.factories = (1..10).collect {
            [id:(Long)it,name:"<strong>Factory${it}</strong>"]
        }

        cmd.factory = new CarFactoryCommand(5L,"Factory5")

        String template = '''
            <g:select path="factory.id" items="${factories}" itemValue="id" itemLabel="name" />
            <g:select path="factory.id" items="${factories}" itemValue="id" itemLabel="name" noSelection="['':'Please select']" />
            <g:select path="factory.id" items="${factories}" itemValue="id" itemLabel="name" htmlEscape="false" />
        '''
        String out = executeTemplateWithinForm(template,binding)
        def doc = Jsoup.parse(out,'',Parser.xmlParser())
        doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml)


        def selects = doc.getElementsByTag('select')

        assert selects[0].getElementsByTag('option').size() == binding.factories.size()
        assert selects[1].getElementsByTag('option').size() == binding.factories.size() + 1

        def option4 = selects[0].getElementsByTag('option')[3]
        assert option4.val() == binding.factories[3].id.toString()
        assert option4.html().trim() == HtmlUtils.htmlEscape(binding.factories[3].name)

        def optionHtml = selects[2].getElementsByTag('option')[3]
        assert optionHtml.val() == binding.factories[3].id.toString()
        assert optionHtml.html().trim() == binding.factories[3].name

        assert selects[0].getElementsByTag('option').find { it.val() == '5'}.attr('selected') == 'selected'
        assert selects[0].getElementsByTag('option').find { it.val() != '5'}.hasAttr('selected') == false

    }

    private String executeTemplateWithinForm(String template, Map binding = null)
    {
        StringBuffer sb = new StringBuffer()
        sb.append("<g:form modelAttribute='carCommand'>\n")
        sb.append(template)
        sb.append("</g:form>")

        executeTemplate(sb.toString(), binding)
    }

    Document executeTemplateAndParse(String template, Map binding = null)
    {
        Jsoup.parse(executeTemplate(template, binding))
    }



}

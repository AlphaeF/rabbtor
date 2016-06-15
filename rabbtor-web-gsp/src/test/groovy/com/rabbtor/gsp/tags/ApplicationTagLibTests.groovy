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

import com.rabbtor.gsp.AbstractWebGspTests
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.web.util.HtmlUtils
import org.springframework.web.util.JavaScriptUtils

import javax.servlet.http.Cookie

@RunWith(SpringJUnit4ClassRunner)
class ApplicationTagLibTests extends AbstractWebGspTests
{


    @Test
    public void testEscape()
    {
        assert executeTemplate('<g:escape value=\'${g.message(code:"sample.html")}\' codec="HTML" />').trim() == applicationContext.getMessage("sample.html.encoded", null, null)
        assert executeTemplate('<g:escape value=\'${g.message(code:"sample.javascript")}\' codec="JavaScript" />').trim() == applicationContext.getMessage("sample.javascript.encoded", null, null)
    }

    @Test
    public void testEscapeBody()
    {

        assert executeTemplate('<g:escapeBody codec="HTML"><br></g:escapeBody>').trim() == HtmlUtils.htmlEscape("<br>")
        assert executeTemplate('<g:escapeBody codec="Javascript">\'/<>&"</g:escapeBody>').trim() == JavaScriptUtils.javaScriptEscape('\'/<>&"')
    }

    @Test
    public void testRaw()
    {
        assert executeTemplate('<g:raw><br></g:raw>').trim() == "<br>"
    }

    @Test
    public void testHtmlEscapeToSetDefault()
    {
        String template = '''
            <%@ page import="com.rabbtor.gsp.util.GspTagUtils" %>
            <g:set var="requestContext" value="${GspTagUtils.ensureRequestContext(request,response,application)}" />
            <g:htmlEscape />
            <span id="active">${requestContext.defaultHtmlEscape}</span>
            <g:htmlEscape defaultHtmlEscape="false" />
            <span id="passive">${requestContext.defaultHtmlEscape}</span>
            <g:htmlEscape defaultHtmlEscape="true" />
            <span id="activeAgain">${requestContext.defaultHtmlEscape}</span>

        '''
        String out = executeTemplate(template)
        def doc = parseDoc(out)

        assert doc.getElementById('active').html() == 'true'
        assert doc.getElementById('passive').html() == 'false'
        assert doc.getElementById('activeAgain').html() == 'true'


    }

    @Test
    public void testWithHtmlEscape()
    {
        String template = '''
            <%@ page import="com.rabbtor.gsp.util.GspTagUtils" %>
            <g:set var="requestContext" value="${GspTagUtils.ensureRequestContext(request,response,application)}" />
            <% requestContext.defaultHtmlEscape=false %>
            <g:withHtmlEscape >
                <span id="active">${requestContext.defaultHtmlEscape}</span>
            </g:withHtmlEscape>
            <g:withHtmlEscape defaultHtmlEscape="false">
                <span id="passive">${requestContext.defaultHtmlEscape}</span>
            </g:withHtmlEscape>
            <g:withHtmlEscape defaultHtmlEscape="true">
                <span id="activeAgain">${requestContext.defaultHtmlEscape}</span>
            </g:withHtmlEscape>

        '''
        String out = executeTemplate(template)
        def doc = parseDoc(out)

        assert doc.getElementById('active').html() == 'true'
        assert doc.getElementById('passive').html() == 'false'
        assert doc.getElementById('activeAgain').html() == 'true'
    }


    @Test
    public void testCookie()
    {
        request.setCookies(new Cookie('id', '100'))
        assert executeTemplate('<g:cookie name="id" />').trim() == '100'
        assert executeTemplate('${g.cookie(name:"id")}').trim() == '100'
    }

    @Test
    public void testHeader()
    {
        request.addHeader('content-type', 'text/html')
        assert executeTemplate('<g:header name="content-type" />').trim() == 'text/html'
    }

    @Test
    public void testUrl()
    {
        request.setContextPath('/myapp')

        // context relative
        assert executeTemplate('<g:url value="~/go" />').trim() == '/myapp/go'
        assert executeTemplate('<g:url value="/go" />').trim() == '/myapp/go'

        // with custom context prepended
        assert executeTemplate('<g:url value="~/go" context="/" />').trim() == '/go'
        assert executeTemplate('<g:url value="~/go" context="/anotherapp" />').trim() == '/anotherapp/go'
        assert executeTemplate('<g:url value="/go" context="/" />').trim() == '/go'

        // relative url
        assert executeTemplate('<g:url value="go" />').trim() == 'go'

        // context has no effect
        assert executeTemplate('<g:url value="go" context="/anotherapp" />').trim() == 'go'

        // absolute
        assert executeTemplate('<g:url value="http://go" />').trim() == 'http://go'

        // with variables
        def params = [['id', 10], ['name', 'foo'], ['extra', '1']]
        assert executeTemplate('<g:url value="~/go/{id}/{name}" params="${params}" />', [params: params]).trim() == '/myapp/go/10/foo?extra=1'
    }
}

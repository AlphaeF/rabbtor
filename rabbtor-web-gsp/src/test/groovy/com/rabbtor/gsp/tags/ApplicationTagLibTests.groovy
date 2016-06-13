package com.rabbtor.gsp.tags

import com.rabbtor.gsp.taglib.AbstractTagLibTests
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration

import javax.servlet.http.Cookie

@RunWith(SpringJUnit4ClassRunner)
class ApplicationTagLibTests extends AbstractTagLibTests
{


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
        out = executeTemplate("<g:message code='sample.html' htmlEscape='' />")
        assert out.trim() == applicationContext.getMessage("sample.html.encoded", null, null)

        out = executeTemplate("<g:message code='sample.javascript' javaScriptEscape='' />")
        assert out.trim() == applicationContext.getMessage("sample.javascript.encoded", null, null)

        // test args
        out = executeTemplate("<g:message code='msgWithArgs' args=\"[10,'test']\" />")
        assert out.trim() == applicationContext.getMessage("msgWithArgs", [10, 'test'] as Object[], null)

        // args and html escape
        out = executeTemplate("<g:message code='msgWithArgs' args=\"[10,'test']\" htmlEscape='' />")
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
    public void testEncode()
    {
        assert executeTemplate('<g:encode value=\'${g.message(code:"sample.html")}\' type="html" />').trim() == applicationContext.getMessage("sample.html.encoded", null, null)
        assert executeTemplate('<g:encode value=\'${g.message(code:"sample.javascript")}\' type="javascript" />').trim() == applicationContext.getMessage("sample.javascript.encoded", null, null)
    }

    @Test
    public void testCookie() {
        request.setCookies(new Cookie('id','100'))
        assert executeTemplate('<g:cookie name="id" />').trim() == '100'
        assert executeTemplate('${g.cookie(name:"id")}').trim() == '100'
    }

    @Test
    public void testHeader() {
        request.addHeader('content-type','text/html')
        assert executeTemplate('<g:header name="content-type" />').trim() == 'text/html'
    }

    @Test
    public void testUrl() {
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
        def params = [['id',10],['name','foo'],['extra','1']]
        assert executeTemplate('<g:url value="~/go/{id}/{name}" params="${params}" />',[params: params]).trim() == '/myapp/go/10/foo?extra=1'
    }
}

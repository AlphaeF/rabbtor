package com.rabbtor.gsp.taglib

import com.rabbtor.gsp.config.annotation.EnableWebGsp
import com.rabbtor.gsp.config.annotation.GspSettings
import com.rabbtor.gsp.config.annotation.WebGspConfigurerAdapter
import com.rabbtor.gsp.taglib.config.annotation.GspTagLibScan
import org.grails.buffer.GrailsPrintWriter
import org.grails.gsp.GroovyPagesTemplateEngine
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.ExpectedException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockServletContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@WebAppConfiguration
@ContextConfiguration
abstract class AbstractTagLibTests extends GroovyTestCase
{
    @Autowired
    ApplicationContext applicationContext

    @Autowired
    GroovyPagesTemplateEngine gspTemplateEngine

    @Autowired
    MockHttpServletRequest request

    @Autowired
    MockHttpServletResponse response

    @Autowired
    MockServletContext servletContext

    StringWriter writer = new StringWriter()
    GrailsPrintWriter grailsPrintWriter = new GrailsPrintWriter(writer)

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Before
    public void setup()
    {
        WebUtils.storeGrailsWebRequest(new GrailsWebRequest(request, response, servletContext))
    }

    @After
    public void cleanup() {
        grailsPrintWriter.close()
    }

    protected String writeTemplate(Writable writable) {
        writable.writeTo(grailsPrintWriter)
        grailsPrintWriter.flush()
        writer.getBuffer().toString()
    }

    String executeTemplate(String templateText, Map binding = null)
    {
        writer.getBuffer().setLength(0)
        StringReader reader = new StringReader(templateText)
        def template = gspTemplateEngine.createTemplate(reader)
        reader.close()
        def writable = binding ? template.make(binding) : template.make()
        return writeTemplate(writable)
    }

    @Configuration
    @EnableWebMvc
    @EnableWebGsp
    @GspTagLibScan
    public static class GspConfiguration extends WebGspConfigurerAdapter
    {
        @Bean(name = "messageSource")
        public MessageSource messageSource() {
            ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
            messageSource.setBasenames(
                    "i18n/messages");
            messageSource.setCacheSeconds(5);
            return messageSource;
        }

        @Override
        void configureGsp(GspSettings config)
        {
            config.gspReloadingEnabled = true
            config.gspLayoutCaching = false
            config.viewCacheTimeout = 0
            config.locatorCacheTimeout = -1
        }
    }


}

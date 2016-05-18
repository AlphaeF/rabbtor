package com.rabbtor.gsp

import com.rabbtor.taglib.TagLibraryLookup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import spock.lang.Shared
import spock.lang.Specification


@SpringApplicationConfiguration(GspApplication)
class GspTemplateEngineSpec extends Specification
{
    @Shared
    GroovyPagesTemplateEngine templateEngine

    @Autowired
    ApplicationContext applicationContext


    def setupSpec()
    {

    }

    def 'test simple page with standard tags'()
    {
        given:
        def templateEngine = applicationContext.getBean(GroovyPagesTemplateEngine)
        def path = "/simple-page.gsp"
        when:
        def template = templateEngine.createTemplate(path)
        then:
        template != null

        when:
        def writer = new StringWriter()
        def model = [people: [[name:'foo'], [name:'bar']], total: 2]
        template.make(model).writeTo(writer)
        writer.flush()
        def output = writer.getBuffer().toString()
        then:
        output != null
        output.contains('foo')
        output.contains('bar')
        output.contains(2*2)





    }


    @SpringBootApplication
    @Configuration
    static class GspApplication
    {
        @Autowired
        Environment environment

        @Bean
        GroovyPagesTemplateEngine templateEngine()
        {
            def engine = new GroovyPagesTemplateEngine(environment)
            engine.tagLibraryLookup = tagLibraryLookup()
            engine
        }

        @Bean
        TagLibraryLookup tagLibraryLookup()
        {
            return new TagLibraryLookup()
        }
    }
}




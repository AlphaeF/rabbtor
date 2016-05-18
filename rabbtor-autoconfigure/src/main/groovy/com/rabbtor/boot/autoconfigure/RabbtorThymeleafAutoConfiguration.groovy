package com.rabbtor.boot.autoconfigure

import com.rabbtor.web.servlet.view.thymeleaf.RabbtorDialect
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.dialect.IDialect

@Configuration
@ConditionalOnClass(IDialect.class)
@AutoConfigureAfter(ThymeleafAutoConfiguration.class)
class RabbtorThymeleafAutoConfiguration
{


    @Bean
    IDialect rabbtorDialect() {
        return new RabbtorDialect()
    }

}

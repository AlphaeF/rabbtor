package com.rabbtor.web.servlet.mvc.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DelegatingRabbtorWebMvcConfiguration.class)
public @interface EnableRabbtorWebMvc
{
}

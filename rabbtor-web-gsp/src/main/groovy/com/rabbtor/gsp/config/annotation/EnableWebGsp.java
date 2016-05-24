package com.rabbtor.gsp.config.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({WebGspConfiguration.class,WebTagLibraryLookupRegistrar.class})
public @interface EnableWebGsp
{
    TagLibScan tagLibScan() default @TagLibScan;
    Class<?>[] tagLibClasses() default {};
}

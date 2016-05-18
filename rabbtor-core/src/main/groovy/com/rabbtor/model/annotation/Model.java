package com.rabbtor.model.annotation;


import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Model
{
    /**
     * Alias for {@link #name}.
     */
    @AliasFor("name")
    String value() default "";

    /**
     * The name of the model to bind to.
     * @since 1.0
     */
    @AliasFor("value")
    String name() default "";

    String[] whitelist() default "";

    String[] blacklist() default "";

}

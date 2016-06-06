
package com.rabbtor.gsp.config.annotation;


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class GspEnabledCondition implements Condition
{
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata)
    {
        return context.getEnvironment().getProperty("spring.gsp.enabled",Boolean.class,false);
    }
}

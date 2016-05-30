package com.rabbtor.gsp.config.annotation;


import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;

public class JspCondition implements Condition
{
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata)
    {
        boolean isJspPresent = ClassUtils.isPresent("javax.servlet.jsp.tagext.JspTag",JspCondition.class.getClassLoader());
        boolean isGspJspPresent = ClassUtils.isPresent("org.grails.gsp.jsp.TagLibraryResolverImpl",JspCondition.class.getClassLoader());
        return isGspJspPresent && isJspPresent;
    }
}

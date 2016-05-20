package com.rabbtor.taglib;



import java.lang.annotation.*;

/**
 * Marker annotation to recognize GSP tag libraries. Classes annotated with this annotation
 * are scanned by the {@link TagLibrariesBeanFactoryPostProcessor} when bean factory is initializing.
 * When registering tag lib beans, the bean name must end with "GspTagLib" i.e. "MyCustomGspTagLib".
 *
 * @Author Cagatay.Kalan
 * @Since 1.0
 *
 * @see TagLibrariesBeanFactoryPostProcessor
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface TagLib
{
}

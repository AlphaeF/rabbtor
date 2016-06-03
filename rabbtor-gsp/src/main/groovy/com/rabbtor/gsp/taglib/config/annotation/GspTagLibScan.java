package com.rabbtor.gsp.taglib.config.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import(TagLibraryLookupRegistrar.class)
public @interface GspTagLibScan
{
    /**
     * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation
     * declarations e.g.: {@code @ServletComponentScan("org.my.pkg")} instead of
     * {@code @ServletComponentScan(basePackages="org.my.pkg")}.
     * @return the base packages to scan
     */
    String[] value() default {};

    /**
     * Base packages to scan for annotated servlet components. {@link #value()} is an
     * alias for (and mutually exclusive with) this attribute.
     * <p>
     * Use {@link #basePackageClasses()} for a type-safe alternative to String-based
     * package names.
     * @return the base packages to scan
     */
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to
     * scan for annotated servlet components. The package of each class specified will be
     * scanned.
     * @return classes from the base packages to scan
     */
    Class<?>[] basePackageClasses() default {};


}

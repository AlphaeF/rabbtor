/*
 * Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rabbtor.taglib;

import com.rabbtor.util.ClassPropertyFetcher;
import com.rabbtor.util.ClassUtils;
import com.rabbtor.util.MetaClassUtils;
import com.rabbtor.util.NameUtils;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MetaProperty;
import org.springframework.beans.BeanWrapper;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Default implementation of a tag lib class.
 *
 * @author Graeme Rocher
 */
public class DefaultTagLibClass implements TagLibClass
{



    private Set<String> tags = new HashSet<String>();
    private String namespace = TagLibClass.DEFAULT_NAMESPACE;
    private Set<String> returnObjectForTagsSet = new HashSet<String>();
    private Object defaultEncodeAs = null;
    private Map<String, Object> encodeAsForTags = new HashMap<String, Object>();
    private final Class<?> clazz;
    private BeanWrapper reference;
    private final String fullName;
    private final String name;
    private final String packageName;
    private final String naturalName;
    private final String shortName;
    private final String propertyName;
    private final String logicalPropertyName;
    private final ClassPropertyFetcher classPropertyFetcher;
    private boolean isAbstract;
    private final String beanName;

    /**
     * Default contructor.
     *
     * @param clazz        the class which contains the tag methods
     */
    @SuppressWarnings("rawtypes")
    public DefaultTagLibClass(Class<?> clazz,String beanName, String trailingName) {

        Assert.notNull(clazz, "clazz parameter should not be null");

        this.clazz = clazz;
        this.beanName = beanName;
        fullName = clazz.getName();
        packageName = org.springframework.util.ClassUtils.getPackageName(clazz);
        naturalName = NameUtils.getNaturalName(clazz.getName());
        shortName = org.springframework.util.ClassUtils.getShortName(clazz);
        name = NameUtils.getLogicalName(clazz, trailingName);
        propertyName = NameUtils.getPropertyNameRepresentation(shortName);
        if (!StringUtils.hasText(name)) {
            logicalPropertyName = propertyName;
        }
        else {
            logicalPropertyName = NameUtils.getPropertyNameRepresentation(name);
        }
        classPropertyFetcher = ClassPropertyFetcher.forClass(clazz);
        isAbstract = Modifier.isAbstract(clazz.getModifiers());



        for (PropertyDescriptor prop : getPropertyDescriptors()) {
            Method readMethod = prop.getReadMethod();
            if (readMethod == null || Modifier.isStatic(readMethod.getModifiers())) {
                continue;
            }

            if (Closure.class.isAssignableFrom(prop.getPropertyType())) {
                tags.add(prop.getName());
            }
        }

        String ns = getStaticPropertyValue(NAMESPACE_FIELD_NAME, String.class);
        if (ns != null && !"".equals(ns.trim())) {
            namespace = ns.trim();
        }

        List returnObjectForTagsList = getStaticPropertyValue(RETURN_OBJECT_FOR_TAGS_FIELD_NAME, List.class);
        if (returnObjectForTagsList != null) {
            for (Object tagName : returnObjectForTagsList) {
                returnObjectForTagsSet.add(String.valueOf(tagName));
            }
        }

        defaultEncodeAs = getStaticPropertyValue(DEFAULT_ENCODE_AS_FIELD_NAME, Object.class);

        Map encodeAsForTagsMap = getStaticPropertyValue(ENCODE_AS_FOR_TAGS_FIELD_NAME, Map.class);
        if (encodeAsForTagsMap != null) {
            for (@SuppressWarnings("unchecked")
                 Iterator<Map.Entry> it = encodeAsForTagsMap.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = it.next();
                encodeAsForTags.put(entry.getKey().toString(), entry.getValue());
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getNaturalName() {
        return naturalName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getLogicalPropertyName() {
        return logicalPropertyName;
    }

    public String getPackageName() {
        return packageName;
    }

    public Object getReferenceInstance() {
        Object obj = classPropertyFetcher.getReference();
        if (obj instanceof GroovyObject) {
            ((GroovyObject)obj).setMetaClass(getMetaClass());
        }
        return obj;
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return classPropertyFetcher.getPropertyDescriptors();
    }

    public Class<?> getPropertyType(String typeName) {
        return classPropertyFetcher.getPropertyType(typeName);
    }

    public boolean isReadableProperty(String propName) {
        return classPropertyFetcher.isReadableProperty(propName);
    }

    public boolean hasTag(String tagName) {
        return tags.contains(tagName);
    }

    public Set<String> getTagNames() {
        return tags;
    }

    public String getNamespace() {
        return namespace;
    }

    public Set<String> getTagNamesThatReturnObject() {
        return returnObjectForTagsSet;
    }

    public Object getEncodeAsForTag(String tagName) {
        return encodeAsForTags.get(tagName);
    }

    public Object getDefaultEncodeAs() {
        return defaultEncodeAs;
    }

    @Override
    public String getBeanName()
    {
        return beanName;
    }


    /**
     * <p>Looks for a property of the reference instance with a given name and type.</p>
     * <p>If found its value is returned. We follow the Java bean conventions with augmentation for groovy support
     * and static fields/properties. We will therefore match, in this order:
     * </p>
     * <ol>
     * <li>Public static field
     * <li>Public static property with getter method
     * <li>Standard public bean property (with getter or just public field, using normal introspection)
     * </ol>
     *
     * @return property value or null if no property or static field was found
     */
    protected <T> T getPropertyOrStaticPropertyOrFieldValue(String name, Class<T> type) {
        Object value = classPropertyFetcher.getPropertyValue(name);
        return returnOnlyIfInstanceOf(value, type);
    }
    
    /**
     * Get the value of the named static property.
     *
     * @param propName
     * @param type
     * @return The property value or null
     */
    public <T> T getStaticPropertyValue(String propName, Class<T> type) {
        T value = classPropertyFetcher.getStaticPropertyValue(propName, type);
        if (value == null) {
            return getGroovyProperty(propName, type, true);
        }
        return value;
    }

    /**
     * Get the value of the named property, with support for static properties in both Java and Groovy classes
     * (which as of Groovy JSR 1.0 RC 01 only have getters in the metaClass)
     * @param propName
     * @param type
     * @return The property value or null
     */
    public <T> T getPropertyValue(String propName, Class<T> type) {
        T value = classPropertyFetcher.getPropertyValue(propName, type);
        if (value == null) {
            // Groovy workaround
            return getGroovyProperty(propName, type, false);
        }
        return returnOnlyIfInstanceOf(value, type);
    }

    private <T> T  getGroovyProperty(String propName, Class<T> type, boolean onlyStatic) {
        Object value = null;
        if (GroovyObject.class.isAssignableFrom(getClazz())) {
            MetaProperty metaProperty = getMetaClass().getMetaProperty(propName);
            if (metaProperty != null) {
                int modifiers = metaProperty.getModifiers();
                if (Modifier.isStatic(modifiers)) {
                    value = metaProperty.getProperty(clazz);
                }
                else if (!onlyStatic) {
                    value = metaProperty.getProperty(getReferenceInstance());
                }
            }
        }
        return returnOnlyIfInstanceOf(value, type);
    }

    public Object getPropertyValueObject(String propertyNAme) {
        return getPropertyValue(propertyNAme, Object.class);
    }

    @SuppressWarnings("unchecked")
    private <T> T returnOnlyIfInstanceOf(Object value, Class<T> type) {
        if ((value != null) && (type==Object.class || ClassUtils.isGroovyAssignableFrom(type, value.getClass()))) {
            return (T)value;
        }

        return null;
    }

    /* (non-Javadoc)
     * @see grails.core.GrailsClass#getPropertyValue(java.lang.String)
     */
    public Object getPropertyValue(String propName) {
        return getPropertyOrStaticPropertyOrFieldValue(propName, Object.class);
    }

    public boolean isAbstract() {

        return isAbstract;
    }

    /* (non-Javadoc)
    * @see grails.core.GrailsClass#hasProperty(java.lang.String)
    */
    public boolean hasProperty(String propName) {
        return classPropertyFetcher.isReadableProperty(propName);
    }

    /**
     * @return the metaClass
     */
    public MetaClass getMetaClass() {
        return MetaClassUtils.getExpandoMetaClass(getClazz());
    }

    public Class<?> getClazz()
    {
        return clazz;
    }
}

package com.rabbtor.gsp.taglib.config.annotation;


import java.util.Set;

public class TagLibraryRegistry
{
    private Set<Class<?>> tagLibInstances;

    public Set<Class<?>> getTagLibInstances()
    {
        return tagLibInstances;
    }

    public void setTagLibInstances(Set<Class<?>> tagLibInstances)
    {
        this.tagLibInstances = tagLibInstances;
    }
}

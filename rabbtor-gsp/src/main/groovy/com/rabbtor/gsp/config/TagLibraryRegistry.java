package com.rabbtor.gsp.config;


import java.util.List;
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

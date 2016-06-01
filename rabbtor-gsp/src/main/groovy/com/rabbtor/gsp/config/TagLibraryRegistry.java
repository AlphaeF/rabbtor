package com.rabbtor.gsp.config;


import java.util.List;

public class TagLibraryRegistry
{
    private List<Class<?>> tagLibInstances;

    public List<Class<?>> getTagLibInstances()
    {
        return tagLibInstances;
    }

    public void setTagLibInstances(List<Class<?>> tagLibInstances)
    {
        this.tagLibInstances = tagLibInstances;
    }
}
